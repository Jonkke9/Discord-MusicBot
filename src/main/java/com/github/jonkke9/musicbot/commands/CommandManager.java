package com.github.jonkke9.musicbot.commands;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.musicplayer.Disconnect;
import com.github.jonkke9.musicbot.commands.musicplayer.FastForward;
import com.github.jonkke9.musicbot.commands.musicplayer.Help;
import com.github.jonkke9.musicbot.commands.musicplayer.Loop;
import com.github.jonkke9.musicbot.commands.musicplayer.NowPlaying;
import com.github.jonkke9.musicbot.commands.musicplayer.Pause;
import com.github.jonkke9.musicbot.commands.musicplayer.Play;
import com.github.jonkke9.musicbot.commands.musicplayer.Queue;
import com.github.jonkke9.musicbot.commands.musicplayer.Rewind;
import com.github.jonkke9.musicbot.commands.musicplayer.Shuffle;
import com.github.jonkke9.musicbot.commands.musicplayer.Skip;
import com.github.jonkke9.musicbot.commands.musicplayer.Volume;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// This class is responsible for managing and executing commands
public class CommandManager extends ListenerAdapter {

    private final List<Command> commands = new ArrayList<>();

    // Event handler for when a message is received
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        super.onMessageReceived(event);

        //This should be run in async because some commands might take some time to process, and it would be good if everything would not freeze when commands are used
        CompletableFuture.runAsync(() -> {
            final Message message = event.getMessage();

            if (message.getAuthor().isBot()) {
                return;
            }

            final String msgStr = message.getContentRaw();

            // Before every command user should pun a prefix defined in config.json. this is a way to recognize the command
            if (msgStr.startsWith( Main.getBot().config.prefix)) {
                final String[] msgParts = msgStr.split(" ");
                final String cmdStr = msgParts[0].substring( Main.getBot().config.prefix.length());
                final Command command = getCommand(cmdStr);
                if (command != null) {
                    // Add reaction to the users message to show that the command is being handled.
                    message.addReaction(Emoji.fromUnicode("\uD83D\uDC40")).queue();
                    // We want to pass rest of the words behind the actual command as arguments
                    final String[] args = Arrays.copyOfRange(msgParts, 1, msgParts.length);
                    command.execute(message, args);
                }
            }
        });
    }

    public CommandManager() {
        super();
        // Add all commands to the list
        commands.add(new Skip());
        commands.add(new Disconnect());
        commands.add(new Volume());
        commands.add(new Play());
        commands.add(new Loop());
        commands.add(new Help());
        commands.add(new Queue());
        commands.add(new Shuffle());
        commands.add(new FastForward());
        commands.add(new Rewind());
        commands.add(new NowPlaying());
        commands.add(new Pause());
    }

    // Method to get a command by its name or alias. if command is not found returns null
    public Command getCommand(final String name) {
        final String nameLower = name.toLowerCase();
        for (final Command cmd : commands) {
            if (nameLower.equals(cmd.getName()) || cmd.getAliases().contains(nameLower)) {
                return cmd;
            }
        }
        return null;
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }
}
