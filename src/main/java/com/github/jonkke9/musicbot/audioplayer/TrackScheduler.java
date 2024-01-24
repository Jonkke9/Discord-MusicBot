package com.github.jonkke9.musicbot.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
* This class is also partly taken from lavaplayer documentation, but it has some improvements and additional methods.
 * responsible for managing audio playing this is guild specific and is contained by GuildMusicManage.
*/
public final class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final Guild guild;
    private boolean isLooping;

    public TrackScheduler(final AudioPlayer player, final Guild guild) {
        super();
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    // Method to add a track to the queue
    public void queue(final AudioTrack track) {
        //make sure that the bot is in audiochannel
        if (guild.getSelfMember().getVoiceState().inAudioChannel() && track != null && !player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    // Method to skip to the next track in the queue
    private void nextTrack() {
        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioTrack track = queue.poll();
            if (track != null) {
                player.startTrack(track, false);
            }
        }
    }

    public void skip() {
        if (isLooping && player.getPlayingTrack() != null) {
            queue.offer(player.getPlayingTrack().makeClone());
        }
        nextTrack();
    }

    // Event handler for when a track end
    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        //if looping is turned on, the old audio tract is added to the queue
        if (isLooping && track != null) {
            queue.offer(track.makeClone());
        }

        if (endReason.mayStartNext) {
            
            nextTrack();
        }
    }

    @Override
    public void onTrackStuck(final AudioPlayer player, final AudioTrack track, final long thresholdMs) {
        nextTrack();
    }

    public AudioPlayer getPlayer() {
        return this.player;
    }

    public void shuffleQueue() {
        final List<AudioTrack> list = new ArrayList<>(queue);
        Collections.shuffle(list);
        queue.clear();
        queue.addAll(list);
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return new LinkedBlockingQueue<>(queue);
    }

    public void clearQueue() {
        queue.clear();
    }

    public void setLooping(final boolean looping) {
        isLooping = looping;
    }

    public boolean isLooping() {
        return isLooping;
    }
}
