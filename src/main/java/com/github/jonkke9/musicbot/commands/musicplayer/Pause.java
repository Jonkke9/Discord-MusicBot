package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Pause class implements the Command interface and is responsible for handling command "pause"
 * The pause command is used to pause or un pause the track
 */
public class Pause implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }


    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-pause-description");
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

        audioPlayer.setPaused(!audioPlayer.isPaused());
    }

}
