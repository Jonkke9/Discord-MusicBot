package com.github.jonkke9.musicbot.listener;

import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.Main;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CompletableFuture;

public final class MusicPlayerEventListener extends ListenerAdapter {

    // This is called when someone changes
    @Override
    public void onGuildVoiceUpdate(final GuildVoiceUpdateEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getMember().getUser().equals( Main.getBot().jda.getSelfUser()) && event.getNewValue() == null) {
                final GuildMusicManager musicManager =  Main.getBot().playerManager.getMusicManager(event.getGuild());
                //reset the state of audio player and scheduler
                musicManager.scheduler.getPlayer().stopTrack();
                musicManager.scheduler.clearQueue();
                musicManager.scheduler.setLooping(false);
                musicManager.player.setPaused(false);
            }
        });

    }
}
