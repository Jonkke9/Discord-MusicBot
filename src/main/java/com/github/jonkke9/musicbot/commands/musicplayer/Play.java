package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * The Play class implements the Command interface and is responsible for handling
 * play commands witch is used search and play audio tracks from the internet.
 */
public final class Play implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName() + " <link/name>";
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public List<String> getAliases() {
        return List.of("p");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-play-description");
    }

    @Override
    public void execute(final Message cmd, final String[] args) {

        if (args.length == 0 || args[0].length() == 0) {
            cmd.reply(this.getUsage()).queue();
            return;
        }

        final GuildVoiceState memberVoiceState = cmd.getMember().getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            cmd.reply( Main.getBot().config.getMessage("user-not-in-vc")).queue();
            return;
        }

        final Guild guild = cmd.getGuild();
        final VoiceChannel memberVoiceChannel = memberVoiceState.getChannel().asVoiceChannel();
        final GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();

        if (selfVoiceState.inAudioChannel()) {
            final VoiceChannel selfVoiceChannel = selfVoiceState.getChannel().asVoiceChannel();
            if (!memberVoiceChannel.equals(selfVoiceChannel)) {
                cmd.reply( Main.getBot().config.getMessage("not-in-same-vc")).queue();
                return;
            }
        } else {
             Main.getBot().playerManager.connectToVoiceChannel(memberVoiceChannel);
        }

        String url = String.join(" ", args);
        final TextChannel textChannel = cmd.getChannel().asTextChannel();


        if (!isValidURL(url)) {
            url = "ytsearch:" + url;
        }

        //TODO: clean up this mess
        //handling spotify urls
        if (url.startsWith("https://open.spotify.com/")) {
            if (! Main.getBot().spotifyConnection.isSpotifySupportEnabled()) {
                cmd.reply( Main.getBot().config.getMessage("spotify-not-enabled")).queue();
                return;
            }
            if (url.startsWith("https://open.spotify.com/track/")) {
                textChannel.sendMessage( Main.getBot().config.getMessage("cmd-play-spotify-support-warning")).queue();
                url =  Main.getBot().spotifyConnection.spotifyToYt(url);

            }else if (url.startsWith("https://open.spotify.com/playlist/")) {
                textChannel.sendMessage( Main.getBot().config.getMessage("cmd-play-spotify-support-warning")).queue();
                final List<String> urls =  Main.getBot().spotifyConnection.spotifyPlaylistToYtSearch(url);

                if (urls != null) {
                     Main.getBot().playerManager.loadAndPlayMultiple(textChannel, urls);
                    return;
                }
            } else if (url.startsWith("https://open.spotify.com/album/")) {
                textChannel.sendMessage( Main.getBot().config.getMessage("cmd-play-spotify-support-warning")).queue();
                final List<String> urls =  Main.getBot().spotifyConnection.spotifyAlbumToYtSearch(url);

                if (urls != null) {
                    Main.getBot().playerManager.loadAndPlayMultiple(textChannel, urls);
                    return;
                }
            }
        }
         Main.getBot().playerManager.loadAndPlay(textChannel, url);
    }

    private boolean isValidURL(final String url){
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }


}
