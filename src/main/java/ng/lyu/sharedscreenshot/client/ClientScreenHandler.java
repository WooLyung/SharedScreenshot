package ng.lyu.sharedscreenshot.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ng.lyu.sharedscreenshot.screen.ScreenshotViewerScreen;
import ng.lyu.sharedscreenshot.util.ImageCache;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientScreenHandler {
    public static void handleOpenViewer(UUID uuid) {
        byte[] image = ImageCache.getImage(uuid);
        if (image != null) {
            Minecraft.getInstance().setScreen(new ScreenshotViewerScreen(image));
        }
    }
}
