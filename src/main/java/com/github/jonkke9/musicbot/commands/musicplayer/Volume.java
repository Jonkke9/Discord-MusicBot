package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.commands.Command;
import com.github.jonkke9.musicbot.util.PlayerUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * The Volume class implements the Command interface and is responsible for handling command "volume"
 * The volume command is used to change the volume of audio
 */
public class Volume implements Command{

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName() + "<num>";
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public List<String> getAliases() {
        return List.of("vol", "v");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-volume-description");
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
        int volume = audioPlayer.getVolume();

        if (args.length > 0 ) {
            try{
                volume = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e){
                cmd.reply( Main.getBot().config.getMessage("incorrect-num", args[0])).queue();
                return;
            }
        } else {
            volume += 10;

            if (volume > 100) {
                volume = 0;
            }
        }

        volume = Math.max(0, Math.min(100, volume));
        audioPlayer.setVolume(volume);
        cmd.getChannel().asTextChannel().sendMessage(Main.getBot().config.getMessage("volume-set", String.valueOf(volume))).queue();
    }
}
