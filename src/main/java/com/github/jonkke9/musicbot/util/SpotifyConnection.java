package com.github.jonkke9.musicbot.util;

import com.github.jonkke9.musicbot.Bot;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SpotifyConnection {

    private static final Logger LOGGER = Logger.getLogger(SpotifyConnection.class.getName());

    private long tokenExpirationTime;
    private final SpotifyApi api;
    private final ClientCredentialsRequest clientCredentialsRequest;
    private final boolean enabled;

    public SpotifyConnection(final Bot bot) {
        final SpotifyApi.Builder builder = new SpotifyApi.Builder();

        builder.setClientId( bot.config.spotifyClientId);
        builder.setClientSecret( bot.config.spotifyClientSecret);
        this.api = builder.build();
        this.clientCredentialsRequest = api.clientCredentials().build();

        this.enabled = checkSpotifyCredentials();

        if (enabled) {
            LOGGER.log(Level.INFO, "Spotify credentials ok");
        } else {
            LOGGER.log(Level.WARNING, "Spotify credentials not ok");
        }
    }

    // Checks if client credential have expired and gets new ones if needed
    public void clientCredentials() {
        final long currentTime = System.currentTimeMillis();

        if (currentTime < tokenExpirationTime) {
            return;
        }

        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            api.setAccessToken(clientCredentials.getAccessToken());

            final int expiresIn = clientCredentials.getExpiresIn();
            tokenExpirationTime = currentTime + expiresIn * 1000L;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while fetching client credentials for spotify api.", e);
        }
    }

    // Since lavaplayer does not support playing audio from spotify we are going to get around that by getting the
    // names of artists and tracks from spotify's api and with those we are going to search for the track from YT.
    // This method transforms spotify track url to the format that lavaplayer uses to search from YT.
    private String spotifyTrackToYtSearch (final Track track) {

        final ArtistSimplified[] artists = track.getArtists();
        final StringBuilder search = new StringBuilder();
        search.append("ytsearch: ");

        for (final ArtistSimplified artist : artists) {
            search.append(artist.getName()).append(' ');
        }

        search.append(track.getName());
        return search.toString();
    }

    public String spotifyToYt (final String url) {
        clientCredentials();
        final String[] parts = url.split("/");

        try {
            final Track track = api.getTrack(parts[parts.length - 1]).build().execute();
            return spotifyTrackToYtSearch(track);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.log(Level.WARNING, "Exception was caught while fetching track information from spotify api.");
            return url;
        }
    }

    public List<String> spotifyPlaylistToYtSearch (final String url) {

        final String[] parts = url.split("/");
        final List<String> searches = new ArrayList<>();

        clientCredentials();
        try {
            final Playlist playlist = api.getPlaylist(parts[parts.length - 1]).build().execute();

            for (final PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {
                final Track track = api.getTrack(playlistTrack.getTrack().getId()).build().execute();
                searches.add(spotifyTrackToYtSearch(track));
            }

        } catch (IOException  | SpotifyWebApiException | ParseException e) {
            LOGGER.log(Level.WARNING, "Exception was caught while fetching playlist information from spotify api.", e);

        }
        return searches;
    }

    public List<String> spotifyAlbumToYtSearch (final String url) {

        final String[] parts = url.split("/");
        final List<String> searches = new ArrayList<>();

        clientCredentials();
        try {
            final Album album = api.getAlbum(parts[parts.length - 1]).build().execute();
            final TrackSimplified[] tracks = album.getTracks().getItems();

            for (int i = 0; i < tracks.length; i++ ){
                final Track track = api.getTrack(tracks[i].getId()).build().execute();
                searches.add(spotifyTrackToYtSearch(track));
            }

        } catch (IOException  | SpotifyWebApiException | ParseException e) {
            LOGGER.log(Level.WARNING, "Exception was caught while fetching album information from spotify api.", e);

        }
        return searches;
    }

    private boolean checkSpotifyCredentials() {
        clientCredentials();
        try {
            api.getTrack("6Sy9BUbgFse0n0LPA5lwy5").build().execute();
            return true;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Exception was caught while checking testing spotify apis credential.", e);
            return false;
        }
    }
    
    public boolean isSpotifySupportEnabled() {
        if (enabled) {
            return checkSpotifyCredentials();
        }
        return false;
    }
}
