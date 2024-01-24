package com.github.jonkke9.musicbot;

public final class Main {

    private static Bot bot;

    public static void main(final String[] args) {
        bot = new Bot();
    }

    public static Bot getBot() {
        return bot;
    }
}
