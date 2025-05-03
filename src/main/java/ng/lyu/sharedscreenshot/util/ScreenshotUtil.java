package ng.lyu.sharedscreenshot.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenshotUtil {
    public static byte[] captureMinecraftScreenToPng() {
        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        try {
            GL11.glReadBuffer(GL11.GL_BACK);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int i = ((height - y - 1) * width + x) * 4;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    int a = buffer.get(i + 3) & 0xFF;
                    image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    public static boolean savePngToFile(byte[] pngData, File outputFile) {
        if (pngData == null || pngData.length == 0) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(pngData);
            System.out.println("PNG 저장 완료: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("PNG 저장 실패: " + e.getMessage());
            return false;
        }
    }
}