package ng.lyu.sharedscreenshot;

import net.minecraftforge.fml.common.Mod;
import ng.lyu.sharedscreenshot.network.NetworkHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SharedScreenshot.MODID)
public class SharedScreenshot {
    public static final String MODID = "sharedscreenshot";

    public SharedScreenshot() {
        NetworkHandler.register();
    }
}
