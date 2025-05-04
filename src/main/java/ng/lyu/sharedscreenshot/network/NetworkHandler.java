package ng.lyu.sharedscreenshot.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import ng.lyu.sharedscreenshot.SharedScreenshot;

public class NetworkHandler {
    private static final String PROTOCOL = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(SharedScreenshot.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SharedScreenshotBodyPacket.class,
                SharedScreenshotBodyPacket::encode,
                SharedScreenshotBodyPacket::decode,
                SharedScreenshotBodyPacket::handle
        );
        CHANNEL.registerMessage(id++, SharedScreenshotHeadPacket.class,
                SharedScreenshotHeadPacket::encode,
                SharedScreenshotHeadPacket::decode,
                SharedScreenshotHeadPacket::handle
        );
        CHANNEL.registerMessage(id++, SharedScreenshotOpenPacket.class,
                SharedScreenshotOpenPacket::encode,
                SharedScreenshotOpenPacket::decode,
                SharedScreenshotOpenPacket::handle
        );
    }
}