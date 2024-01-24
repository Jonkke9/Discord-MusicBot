package com.github.jonkke9.musicbot;

import com.github.jonkke9.musicbot.audioplayer.GuildMusicManager;
import com.github.jonkke9.musicbot.audioplayer.PlayerManager;
import com.github.jonkke9.musicbot.commands.CommandManager;
import com.github.jonkke9.musicbot.listener.MusicPlayerEventListener;
import com.github.jonkke9.musicbot.util.SpotifyConnection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// This clas contains instances of everything that is needed by multiple classes
public class Bot {

    private static final Logger LOGGER = Logger.getLogger(Bot.class.getName());

    public final JDA jda;
    public final CommandManager commandManager;
    public final PlayerManager playerManager;
    public final SpotifyConnection spotifyConnection;
    public final Config config;

    public Bot () {
        this.config = new Config();
        this.commandManager = new CommandManager();
        this.playerManager = new PlayerManager();
        this.spotifyConnection = new SpotifyConnection(this);
        final String token = config.token;
        final JDABuilder builder = JDABuilder.createDefault(token);

        if (token.length() <= 1) {
            LOGGER.log(Level.SEVERE, "Bot token not defined in the config.json");
        }

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        jda = builder.build();
        jda.addEventListener(commandManager);
        jda.addEventListener(new MusicPlayerEventListener());
        jda.getPresence().setActivity(Activity.listening(config.activity));

        // If the channel where the bot is empty or only has other bots in it, we do not want that the bot stays there playing music for no one.
        final Timer timer = new Timer();
        // run the check every 3 minutes
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (final GuildMusicManager musicManager : playerManager.getMusicManagers()) {
                    final GuildVoiceState voiceState = musicManager.guild.getSelfMember().getVoiceState();

                    if (voiceState != null && voiceState.inAudioChannel()) {
                        final VoiceChannel channel = voiceState.getChannel().asVoiceChannel();
                        if (channel.getMembers().stream().allMatch(member -> member.getUser().isBot()) || musicManager.player.getPlayingTrack() == null) {
                            musicManager.guild.getAudioManager().closeAudioConnection();
                        }
                    }
                }
            }
        }, TimeUnit.MINUTES.toMillis(3), TimeUnit.MINUTES.toMillis(3));
    }
}
