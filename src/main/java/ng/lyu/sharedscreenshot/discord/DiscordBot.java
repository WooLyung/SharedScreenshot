package ng.lyu.sharedscreenshot.discord;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.minecraft.network.chat.Component;
import ng.lyu.sharedscreenshot.util.ImageCache;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.UUID;

public class DiscordBot {

    public static void trySendImage(UUID uuid) {
        byte[] image = ImageCache.getImage(uuid);
        if (image == null)
            return;

        GuildMessageChannel channel = DiscordIntegration.INSTANCE.getChannel();
        if (channel == null)
            return;

        ByteArrayInputStream stream = new ByteArrayInputStream(image);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("%s's shared screenshot".formatted(ImageCache.getSender(uuid)))
                .setColor(Color.CYAN)
                .setImage("attachment://" + "screenshot.jpg");

        channel.sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(stream, "screenshot.jpg"))
                .queue();
    }
}
