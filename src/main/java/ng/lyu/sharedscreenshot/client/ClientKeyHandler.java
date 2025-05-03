package ng.lyu.sharedscreenshot.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ng.lyu.sharedscreenshot.SharedScreenshot;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SharedScreenshot.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientKeyHandler {
    public static final KeyMapping SCREENSHOT_KEY = new KeyMapping(
            "ng.lyu.sharedscreenshot.screenshotkey",
            KeyConflictContext.UNIVERSAL,
            InputConstants.getKey(GLFW.GLFW_KEY_GRAVE_ACCENT, 0),
            "key.categories.misc"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SCREENSHOT_KEY);
    }
}