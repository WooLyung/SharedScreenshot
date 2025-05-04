package ng.lyu.sharedscreenshot.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ng.lyu.sharedscreenshot.SharedScreenshot;
import ng.lyu.sharedscreenshot.network.NetworkHandler;
import ng.lyu.sharedscreenshot.network.SharedScreenshotBodyPacket;
import ng.lyu.sharedscreenshot.network.SharedScreenshotHeadPacket;
import ng.lyu.sharedscreenshot.util.ScreenshotUtil;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SharedScreenshot.MODID, value = Dist.CLIENT)
public class ScreenShotHandler {
    private static boolean doCapture = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS && ClientKeyHandler.SCREENSHOT_KEY.matches(event.getKey(), event.getScanCode())) {
            doCapture = true;
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (doCapture) {
            final int segSize = 30000;
            byte[] image = ScreenshotUtil.captureMinecraftScreenToJPG();
            if (image == null)
                return;

            UUID uuid = UUID.randomUUID();
            String sender = Minecraft.getInstance().player.getName().getString();
            int segCnt = image.length / segSize + (image.length % segSize == 0 ? 0 : 1);

            NetworkHandler.CHANNEL.sendToServer(new SharedScreenshotHeadPacket(uuid, sender, segCnt));
            for (int i = 0; i < segCnt; i++) {
                int segLength = Math.min(segSize, image.length - i * segSize);
                byte[] segment = new byte[segLength];
                System.arraycopy(image, i * segSize, segment, 0, segLength);
                NetworkHandler.CHANNEL.sendToServer(new SharedScreenshotBodyPacket(uuid, i, segment));
            }

            doCapture = false;
        }
    }
}
