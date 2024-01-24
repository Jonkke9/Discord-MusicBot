package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.commands.Command;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Rewind class implements the Command interface and is responsible for handling command "Rewind"
 * The rewind command is used to rewind specific amount of seconds
 */
public final class Rewind implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName() + "<num>";
    }

    @Override
    public String getName() {
        return "rewind";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-rewind-description");
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
        final long currentPosition = audioPlayer.getPlayingTrack().getPosition();
        final int rewindSeconds = parseRewindSeconds(args, cmd);
        final long newPosition = Math.max(0L, currentPosition - rewindSeconds);

        audioPlayer.getPlayingTrack().setPosition(newPosition);
        cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("track-rewinded", String.valueOf((newPosition - currentPosition) / 1000))).queue();
    }

    private int parseRewindSeconds(final String[] args, final Message cmd) {
        int rewindSeconds = 10;

        if (args.length > 0) {
            try {
                rewindSeconds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                cmd.reply( Main.getBot().config.getMessage("incorrect-num", args[0])).queue();
            }
        }

        return Math.max(rewindSeconds * 1000, 0);
    }
}
