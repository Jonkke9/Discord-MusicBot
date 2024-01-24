package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.List;

/**
 * The Disconnect class implements the Command interface and is responsible for handling command "disconnect"
 * The disconnect command is used to disconnect the bot from voice channel and to stop the music player
 */
public final class Disconnect implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public List<String> getAliases() {
        return List.of("dc", "stop");
    }

    @Override
    public void execute(final Message cmd, final String[] args) {
        final Guild guild = cmd.getGuild();
        final Member selfmember = guild.getSelfMember();
        final GuildVoiceState selfVoiceState = selfmember.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            cmd.reply( Main.getBot().config.getMessage("bot-not-in-vc")).queue();
            return;
        }
        final Member member = cmd.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            cmd.reply( Main.getBot().config.getMessage("user-not-in-vc")).queue();
            return;
        }
        final VoiceChannel memberVoiceChannel = memberVoiceState.getChannel().asVoiceChannel();
        final VoiceChannel selfVoiceChannel = selfVoiceState.getChannel().asVoiceChannel();

        if (!memberVoiceChannel.equals(selfVoiceChannel)) {
            cmd.reply( Main.getBot().config.getMessage("not-in-same-vc")).queue();
            return;
        }
        guild.getAudioManager().closeAudioConnection();
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public String getDescription() {
        return  Main.getBot().config.getMessage("cmd-disconnect-description");
    }
}
