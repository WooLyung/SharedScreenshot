package ng.lyu.sharedscreenshot.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ng.lyu.sharedscreenshot.SharedScreenshot;
import ng.lyu.sharedscreenshot.network.NetworkHandler;
import ng.lyu.sharedscreenshot.network.SharedScreenshotPacket;
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
            NetworkHandler.CHANNEL.sendToServer(new SharedScreenshotPacket(UUID.randomUUID(), ScreenshotUtil.captureMinecraftScreenToPng()));
            doCapture = false;
        }
    }
}
