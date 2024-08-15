package com.github.jonkke9.musicbot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// This class contains different config values and is responsible for getting them from the config.json file
public final class Config {

    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    private static final String CONFIG_DIRECTORY = "config";
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY + "/" + CONFIG_FILE_NAME;

    public final String token;
    public final String spotifyClientId;
    public final String spotifyClientSecret;
    public final String activity;

    public final String prefix;
    public final Color color;

    private final Map<String, String> messages = new ConcurrentHashMap<>();

    //load config from the file and assing fields
    public Config() {
        final Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        try {
            final File configDir = new File(CONFIG_DIRECTORY);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            final File configFile = new File(CONFIG_FILE_PATH);
            if (!configFile.exists()) {
                try (InputStream is = getClass().getResourceAsStream("/" + CONFIG_FILE_NAME)) {
                    if (is != null) {
                        Files.copy(is, Paths.get(CONFIG_FILE_PATH));
                    }
                }
            }

            try (FileReader reader = new FileReader(CONFIG_FILE_PATH)) {
                map = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>(){}.getType());
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Something went wrong while loading config", e);
        }

        this.token = (String) map.get("token");

        if (this.token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        this.spotifyClientId = (String) map.get("spotify-client-id");
        this.spotifyClientSecret = (String) map.get("spotify-client-secret");

        map.putIfAbsent("prefix", "-");
        map.putIfAbsent("activity", "-Play");
        map.putIfAbsent("color", "#1bb51e");

        this.prefix = (String) map.get("prefix");
        this.color =  Color.decode((String) map.get("color"));
        this.activity = (String) map.get("activity");

        if (map.get("messages") instanceof Map) {
            final Map<String, String> messageMap = (Map<String, String>) map.get("messages");
            this.messages.putAll(messageMap);
        }
    }

    // Method to get a string by string key. takes array of args witch it maps to the string if there are placeholders
    public String getMessage(final String key, final String...args) {
        String message = key;

        if (messages.containsKey(key) && messages.get(key) != null) {
            message = messages.get(key);
        }

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                message = message.replaceFirst("%arg" + i + "%", args[i]);
            }
        }
        return message;
    }




}
