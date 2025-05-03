package ng.lyu.sharedscreenshot.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import ng.lyu.sharedscreenshot.util.ImageCache;
import ng.lyu.sharedscreenshot.util.ScreenshotUtil;

import java.io.File;
import java.util.UUID;
import java.util.function.Supplier;

public class SharedScreenshotPacket {

    private final UUID id;
    private final byte[] imageData;

    public SharedScreenshotPacket(UUID id, byte[] imageData) {
        this.id = id;
        this.imageData = imageData;
    }

    public static void encode(SharedScreenshotPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.id);
        buf.writeInt(msg.imageData.length);
        buf.writeByteArray(msg.imageData);
    }

    public static SharedScreenshotPacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int length = buf.readInt();
        byte[] data = buf.readByteArray(length);
        return new SharedScreenshotPacket(id, data);
    }

    public static void handle(SharedScreenshotPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                ImageCache.addImage(msg.id, msg.imageData);
                Component message = Component.translatable("ng.lyu.sharedscreenshot.send", sender.getName())
                        .append(Component.literal(": "))
                        .append(Component.translatable("ng.lyu.sharedscreenshot.screenshot").withStyle(style -> style.withHoverEvent(
                                new net.minecraft.network.chat.HoverEvent(
                                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                        Component.translatable("ng.lyu.sharedscreenshot.click")
                                )
                        ).withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openshot " + msg.id.toString())
                        ).withColor(0x2CD50B)
                        ));
                sender.server.getPlayerList().broadcastSystemMessage(message, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}