package com.github.jonkke9.musicbot.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

// This class contains everything that is specific for a guild (Discord server)
public final class GuildMusicManager {

    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    public final AudioPlayerSendHandler sendHandler;
    public final Guild guild;

    public GuildMusicManager(final AudioPlayerManager manager, final Guild guild) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player, guild);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.player);
        this.guild = guild;
    }
}
