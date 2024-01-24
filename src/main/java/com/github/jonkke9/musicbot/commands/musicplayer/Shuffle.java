package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.commands.Command;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Shuffle class implements the Command interface and is responsible for handling command "shuffle"
 * The shuffle command is used to shuffle the track queue
 */
public class Shuffle implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }


    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-shuffle-description");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void execute(final Message cmd, final String[] args) {

        if (!PlayerUtils.canRunMusicPlayerCommand(cmd)) {
            return;
        }

        final GuildMusicManager musicManager =  Main.getBot().playerManager.getMusicManager(cmd.getGuild());
        musicManager.scheduler.shuffleQueue();
        cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("queue-shuffled")).queue();
    }
}
