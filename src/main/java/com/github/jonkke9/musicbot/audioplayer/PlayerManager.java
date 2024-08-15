package com.github.jonkke9.musicbot.audioplayer;

import com.github.jonkke9.musicbot.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this class is responsible for containing and managing a GuildMusicManagers and has some methods that are not guild specific
public final class PlayerManager {

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        final YoutubeAudioSourceManager ytSourceManager = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        this.audioPlayerManager.registerSourceManager(ytSourceManager);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager,
                com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
    }

    // Method to get the music manager for a guild
    public GuildMusicManager getMusicManager(final Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
            return guildMusicManager;
        });
    }

    // Method to load and play a track
    public void loadAndPlay(final TextChannel channel, final String trackUrl) {
        final Guild guild = channel.getGuild();
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandlerImpl(channel, musicManager, true));
    }

    public void loadAndPlayMultiple(final TextChannel channel, final List<String> urls) {
        final Guild guild = channel.getGuild();
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        final AudioLoadResultHandlerImpl audioLoadResultHandler = new AudioLoadResultHandlerImpl(channel, musicManager, false);

        if (urls != null) {
            for (final String url : urls) {
                audioPlayerManager.loadItemOrdered(musicManager, url, audioLoadResultHandler);
            }
            channel.sendMessage(Main.getBot().config.getMessage("added-many-to-queue", String.valueOf(urls.size()))).queue();
        }
    }

    public void connectToVoiceChannel(final VoiceChannel channel) {
        final AudioManager audioManager = channel.getGuild().getAudioManager();
        getMusicManager(channel.getGuild()).scheduler.clearQueue();
        audioManager.openAudioConnection(channel);
    }

    public Collection<GuildMusicManager> getMusicManagers() {
        return  musicManagers.values();
    }
}
