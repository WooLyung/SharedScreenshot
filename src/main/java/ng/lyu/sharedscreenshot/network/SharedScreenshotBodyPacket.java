package ng.lyu.sharedscreenshot.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import ng.lyu.sharedscreenshot.discord.DiscordBot;
import ng.lyu.sharedscreenshot.util.ImageCache;
import ng.lyu.sharedscreenshot.util.ScreenshotUtil;

import java.util.UUID;
import java.util.function.Supplier;

public class SharedScreenshotBodyPacket {

    private final UUID uuid;
    private final int segment;
    private final byte[] segmentData;

    public SharedScreenshotBodyPacket(UUID uuid, int segment, byte[] segmentData) {
        this.uuid = uuid;
        this.segment = segment;
        this.segmentData = segmentData;
    }

    public static void encode(SharedScreenshotBodyPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.segment);
        buf.writeInt(msg.segmentData.length);
        buf.writeByteArray(msg.segmentData);
    }

    public static SharedScreenshotBodyPacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int segment = buf.readInt();
        int length = buf.readInt();
        byte[] data = buf.readByteArray(length);
        return new SharedScreenshotBodyPacket(id, segment, data);
    }

    public static void handle(SharedScreenshotBodyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) {
                ImageCache.insertOrUpdate(msg.uuid, msg.segment, msg.segmentData);
                ScreenshotUtil.trySendOpenScreenshot(msg.uuid);
            }
            else {
                for (ServerPlayer target : ctx.get().getSender().server.getPlayerList().getPlayers())
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), msg);
                ImageCache.insertOrUpdate(msg.uuid, msg.segment, msg.segmentData);
                DiscordBot.trySendImage(msg.uuid);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}