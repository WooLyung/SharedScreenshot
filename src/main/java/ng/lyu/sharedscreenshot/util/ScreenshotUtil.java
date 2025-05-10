package ng.lyu.sharedscreenshot.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScreenshotUtil {
    public static byte[] captureMinecraftScreenToJPG() {
        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        try {
            GL11.glReadBuffer(GL11.GL_BACK);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();

            BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int i = ((height - y - 1) * width + x) * 4;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    // 알파는 무시
                    int rgb = (r << 16) | (g << 8) | b;
                    rgbImage.setRGB(x, y, rgb);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(rgbImage, "JPG", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    public static void trySendOpenScreenshot(UUID uuid) {
        byte[] imageData = ImageCache.getImage(uuid);
        if (imageData == null)
            return;

        Component message = Component.translatable("ng.lyu.sharedscreenshot.send", ImageCache.getSender(uuid))
                .append(Component.literal(": "))
                .append(Component.translatable("ng.lyu.sharedscreenshot.screenshot").withStyle(style -> style.withHoverEvent(
                                new net.minecraft.network.chat.HoverEvent(
                                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                        Component.translatable("ng.lyu.sharedscreenshot.click")
                                )
                        ).withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openshot " + uuid.toString())
                        ).withColor(0x2CD50B)
                ));
        Minecraft.getInstance().player.sendSystemMessage(message);
    }
}