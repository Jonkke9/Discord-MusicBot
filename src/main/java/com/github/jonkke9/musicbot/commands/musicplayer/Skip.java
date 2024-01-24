package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Skip class implements the Command interface and is responsible for handling command "skip"
 * The skip command is used to skip the currently playing track
 */
public class Skip implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s", "fs", "forceskip");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-skip-description");
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
        final AudioTrack track = Main.getBot().playerManager.getMusicManager(cmd.getGuild()).player.getPlayingTrack();
         Main.getBot().playerManager.getMusicManager(cmd.getGuild()).scheduler.skip();
         cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("track-skipped", track.getInfo().title)).queue();
    }
}
