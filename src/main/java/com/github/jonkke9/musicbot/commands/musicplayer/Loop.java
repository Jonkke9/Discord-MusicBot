package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Loop class implements the Command interface and is responsible for handling command "loop"
 * The loop command is used to toggle bot's music looping feature
 */
// this class is responsible for handling command witch is used to toggle bots music looping feature
public final class Loop implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l", "repeat");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-loop-description");
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
        musicManager.scheduler.setLooping(!musicManager.scheduler.isLooping());
        // tell the user that repeat has been toggled
        String status = Main.getBot().config.getMessage("off");
        if (musicManager.scheduler.isLooping()) {
            status = Main.getBot().config.getMessage("on");
        }
        cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("repeat-status", status)).queue();
    }
}
