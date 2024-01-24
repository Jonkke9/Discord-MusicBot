package com.github.jonkke9.musicbot.util;

import com.github.jonkke9.musicbot.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public final class PlayerUtils {

    private PlayerUtils () {
        //This empty constructor is to hide the default one
    }

    // Many commands
    public static boolean canRunMusicPlayerCommand(final Message cmd, final boolean requirePlayingTrack) {

        final Guild guild = cmd.getGuild();
        final Member selfmember = guild.getSelfMember();
        final GuildVoiceState selfVoiceState = selfmember.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            cmd.reply( Main.getBot().config.getMessage("bot-not-in-vc")).queue();
            return false;
        }
        final Member member = cmd.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            cmd.reply( Main.getBot().config.getMessage("user-not-in-vc")).queue();
            return false;
        }

        final VoiceChannel memberVoiceChannel = memberVoiceState.getChannel().asVoiceChannel();
        final VoiceChannel selfVoiceChannel = selfVoiceState.getChannel().asVoiceChannel();

        if (!memberVoiceChannel.equals(selfVoiceChannel)) {
            cmd.reply( Main.getBot().config.getMessage("not-in-same-vc")).queue();
            return false;
        }

        if (requirePlayingTrack) {
            final AudioPlayer player =  Main.getBot().playerManager.getMusicManager(guild).player;
            if (player.getPlayingTrack() == null) {
                cmd.reply( Main.getBot().config.getMessage("nothing-is-playing")).queue();
                return false;
            }
        }
        return true;
    }

    public static boolean canRunMusicPlayerCommand(final Message cmd) {
        return canRunMusicPlayerCommand(cmd, false);
    }

}
