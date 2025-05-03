package ng.lyu.sharedscreenshot.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ng.lyu.sharedscreenshot.screen.ScreenshotViewerScreen;
import ng.lyu.sharedscreenshot.util.ImageCache;

import java.util.UUID;
import java.util.function.Supplier;

public class SharedScreenshotOpenPacket {
    private final UUID uuid;

    public SharedScreenshotOpenPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public static void encode(SharedScreenshotOpenPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.uuid);
    }

    public static SharedScreenshotOpenPacket decode(FriendlyByteBuf buf) {
        return new SharedScreenshotOpenPacket(buf.readUUID());
    }

    public static void handle(SharedScreenshotOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            byte[] image = ImageCache.getImage(msg.uuid);
            if (image != null) {
                Minecraft.getInstance().setScreen(new ScreenshotViewerScreen(image));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}