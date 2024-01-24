package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.commands.Command;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The fast-forward class implements the Command interface and is responsible for handling command "fast-forward"
 * The fast-forward command is used to skip specific amount of seconds
 */
public final class FastForward implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName() + "<num>";
    }

    @Override
    public String getName() {
        return "fast-forward";
    }

    @Override
    public List<String> getAliases() {
        return List.of("fastforward");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-fast-forward-description");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void execute(final Message cmd, final String[] args) {

        if (!PlayerUtils.canRunMusicPlayerCommand(cmd, true)) {
            return;
        }

        final GuildMusicManager musicManager =  Main.getBot().playerManager.getMusicManager(cmd.getGuild());
        final AudioPlayer audioPlayer = musicManager.player;
        final int fastForwardSeconds = parseFastForwardSeconds(args, cmd);
        final long oldPosition = audioPlayer.getPlayingTrack().getPosition();
        final long newPosition = calculateNewPosition(audioPlayer, fastForwardSeconds);
        audioPlayer.getPlayingTrack().setPosition(newPosition);
        cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("track-fast-forwarded", String.valueOf((newPosition - oldPosition) / 1000))).queue();
    }

    private int parseFastForwardSeconds(final String[] args, final Message cmd) {
        int fastForwardSeconds = 10;

        if (args.length > 0) {
            try {
                fastForwardSeconds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                cmd.reply( Main.getBot().config.getMessage("incorrect-num")).queue();
            }
        }

        return Math.max(fastForwardSeconds, 0);
    }

    private long calculateNewPosition(final AudioPlayer audioPlayer, final int fastForwardSeconds) {
        final long currentPosition = audioPlayer.getPlayingTrack().getPosition();
        final long trackDuration = audioPlayer.getPlayingTrack().getDuration();
        return Math.min(currentPosition + fastForwardSeconds * 1000L, trackDuration);
    }
}
