package ng.lyu.sharedscreenshot.command;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import ng.lyu.sharedscreenshot.network.NetworkHandler;
import ng.lyu.sharedscreenshot.network.SharedScreenshotOpenPacket;
import ng.lyu.sharedscreenshot.screen.ScreenshotViewerScreen;
import ng.lyu.sharedscreenshot.util.ImageCache;

import java.util.UUID;

@Mod.EventBusSubscriber
public class OpenShot {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("openshot")
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                .executes(ctx -> {
                                    UUID id = UuidArgument.getUuid(ctx, "uuid");
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    SharedScreenshotOpenPacket packet = new SharedScreenshotOpenPacket(id);
                                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
                                    return 1;
                                }))
        );
    }
}