package com.github.jonkke9.musicbot.audioplayer;

import com.github.jonkke9.musicbot.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// This class handles the results of loading audio tracks or playlists.
public final class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {

    private static final Logger LOGGER = Logger.getLogger(AudioLoadResultHandlerImpl.class.getName());

    private final GuildMusicManager musicManager;
    private final TextChannel channel;
    private final boolean announce;

    public AudioLoadResultHandlerImpl(final TextChannel channel, final GuildMusicManager musicManager, final boolean announce) {
        this.channel = channel;
        this.musicManager = musicManager;
        this.announce = announce;
    }

    // This method is called when a single track is loaded.
    @Override
    public void trackLoaded(final AudioTrack track) {
        // If a track is already playing, notify user that track has been added to the queue
        if (musicManager.player.getPlayingTrack() != null) {
            sendMessage("added-one-to-queue", track.getInfo().title, String.valueOf(musicManager.scheduler.getQueue().size() + 1));
        }
        musicManager.scheduler.queue(track); // Add track to the queue
    }

    // This method is called when a playlist is loaded (multiple tracks)
    @Override
    public void playlistLoaded(final AudioPlaylist audioPlaylist) {
        final List<AudioTrack> tracks = audioPlaylist.getTracks();

        // This meas that playlist is actually a YouTube search result. We are only going to take the first track and ignore the rest
        if (audioPlaylist.isSearchResult()) {
            // If a track is already playing, notify user that track has been added to the queue
            if (musicManager.player.getPlayingTrack() != null) {
                sendMessage("added-one-to-queue", tracks.get(0).getInfo().title, String.valueOf(musicManager.scheduler.getQueue().size() + 1));
            }
            musicManager.scheduler.queue(tracks.get(0)); // Add track to the queue
        } else {
            // If the playlist is not a search result, queue all tracks
            for (final AudioTrack track : tracks) {
                musicManager.scheduler.queue(track);
            }
            // Tell the user how many tracks were added to the queue
            sendMessage("added-many-to-queue", String.valueOf(tracks.size()));
        }
    }

    // This method is called when no matches are found for the requested track or playlist.
    @Override
    public void noMatches() {
        sendMessage("no-matches");
        // The bot joins voice channel when play command is used. if loading fails we want to make sure that the bot is not left to the channel
        if (musicManager.player.getPlayingTrack() == null) {
            channel.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    // This method is called when loading a track or playlist fails.
    @Override
    public void loadFailed(final FriendlyException e) {
        LOGGER.log(Level.WARNING, "Track loading failed.", e);
        sendMessage("no-matches");
        // The bot joins voice channel when play command is used. if loading fails we want to make sure that the bot is not left to the channel
        if (musicManager.player.getPlayingTrack() == null) {
            channel.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    // This method sends a message to the channel, if the 'announce' flag is true.
    private void sendMessage(final String key, final String ...args) {
        if (announce) {
            channel.sendMessage( Main.getBot().config.getMessage(key, args)).queue();
        }
    }
}
