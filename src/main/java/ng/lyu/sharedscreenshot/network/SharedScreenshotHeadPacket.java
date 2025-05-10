package ng.lyu.sharedscreenshot.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import ng.lyu.sharedscreenshot.discord.DiscordBot;
import ng.lyu.sharedscreenshot.util.ImageCache;
import ng.lyu.sharedscreenshot.util.ScreenshotUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

public class SharedScreenshotHeadPacket {

    private final UUID uuid;
    private final String sender;
    private final int segCnt;

    public SharedScreenshotHeadPacket(UUID uuid, String sender, int segCnt) {
        this.uuid = uuid;
        this.sender = sender;
        this.segCnt = segCnt;
    }

    public static void encode(SharedScreenshotHeadPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.sender.length());
        buf.writeCharSequence(msg.sender,  StandardCharsets.UTF_8);
        buf.writeInt(msg.segCnt);
    }

    public static SharedScreenshotHeadPacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        int senderLength = buf.readInt();
        String sender = buf.readCharSequence(senderLength, StandardCharsets.UTF_8).toString();
        int segCnt = buf.readInt();
        return new SharedScreenshotHeadPacket(uuid, sender, segCnt);
    }

    public static void handle(SharedScreenshotHeadPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) {
                ImageCache.insertOrUpdate(msg.uuid, msg.sender, msg.segCnt);
                ScreenshotUtil.trySendOpenScreenshot(msg.uuid);
            }
            else {
                for (ServerPlayer target : ctx.get().getSender().server.getPlayerList().getPlayers())
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), msg);
                ImageCache.insertOrUpdate(msg.uuid, msg.sender, msg.segCnt);
                DiscordBot.trySendImage(msg.uuid);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}