package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

/**
 * The NowPlaying class implements the Command interface and is responsible for handling command "now-playing"
 * The now-playing command is used to display currently playing track info.
 */
public class NowPlaying implements Command {
    @Override
    public String getName() {
        return "now-playing";
    }

    @Override
    public List<String> getAliases() {
        return List.of("nowplaying", "np");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-now-playing-description");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }

    @Override
    public void execute(final Message cmd, final String... args) {
        final GuildMusicManager musicManager = Main.getBot().playerManager.getMusicManager(cmd.getGuild());
        final AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            cmd.reply(Main.getBot().config.getMessage("nothing-is-playing")).queue();
            return;
        }

        cmd.getChannel().asTextChannel().sendMessageEmbeds(nowPlayingCardEmbed(musicManager)).queue();
    }

    private MessageEmbed nowPlayingCardEmbed(final GuildMusicManager musicManager) {
        final AudioPlayer player = musicManager.player;
        final EmbedBuilder eb = new EmbedBuilder();
        final AudioTrack track = player.getPlayingTrack();
        final AudioTrackInfo info = track.getInfo();

        eb.setColor( Main.getBot().config.color);
        eb.setTitle(info.title, info.uri);
        eb.setDescription(info.author);
        eb.addField(getTimeline(musicManager));

        if (info.uri.startsWith("https://www.youtube.com/watch?v=") || info.uri.startsWith("https://youtu.be/")) {
            eb.setImage("https://img.youtube.com/vi/" + info.identifier + "/mqdefault.jpg");
        }
        return eb.build();
    }

    private MessageEmbed.Field getTimeline(final GuildMusicManager musicManager) {
        final AudioPlayer player = musicManager.player;
        final AudioTrack track = player.getPlayingTrack();
        final AudioTrackInfo info = track.getInfo();
        final String looping = loopingIndicator(musicManager);
        final String audio = audio(player);

        if (info.isStream) {
            return new MessageEmbed.Field("**🔴 Live**", audio + looping, false);
        }

        final StringBuilder timeline = new StringBuilder();
        final double d = track.getPosition() / (track.getDuration() / 20.0d);
        final String timeStamp = formatTimestamp(track.getPosition()) + " / " + formatTimestamp(track.getDuration());

        timeline.append('[' );

        for (int i = 0; i <= 20; i++) {

            if (i == (int) Math.round(d)) {
                timeline.append("🔘");
            } else {
                timeline.append('▬');
            }
        }
        timeline.append(']' );
        return new MessageEmbed.Field(timeline.toString(), timeStamp  + audio + looping, false);
    }

    private String loopingIndicator(final GuildMusicManager musicManager) {
        if (musicManager.scheduler.isLooping()) {
            return "  **|**  \uD83D\uDD01";
        }
        return "";
    }

    private String audio(final AudioPlayer player) {
        final StringBuilder builder = new StringBuilder();
        final int vol = player.getVolume();

        if (!player.getPlayingTrack().getInfo().isStream) {
            builder.append("  **|**  ");
        }

        if (vol > 0) {
            if (vol <= 33) {
                builder.append("\uD83D\uDD08");
            } else if (vol <= 66) {
                builder.append("\uD83D\uDD09");
            } else {
                builder.append("\uD83D\uDD0A");
            }

            builder.append(": ");
            builder.append(vol);
            builder.append('%');
        } else {
            builder.append("\uD83D\uDD07");
        }

        return builder.toString();
    }

    private String formatTimestamp(final long timestamp) {
        final long second = (timestamp / 1000) % 60;
        final long minute = (timestamp / (1000 * 60)) % 60;
        final long hour = (timestamp / (1000 * 60 * 60)) % 24;

        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else if (minute > 0) {
            return String.format("%02d:%02d", minute, second);
        } else {
            return String.format("%01d:%02d", minute, second);
        }
    }
}
