package com.github.jonkke9.musicbot.commands.musicplayer;

import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

/**
 * The Help class implements the Command interface and is responsible for handling command "help"
 * The help command is used to display descriptions of different commands to the user
 */
public final class Help implements Command {

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName() + "<cmd>";
    }


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h", "commands");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-help-description");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void execute(final Message cmd, final String[] args) {

        final EmbedBuilder builder = new EmbedBuilder();
        final List<Command> commands = new ArrayList<>();

        if (args.length == 0) {
            commands.addAll( Main.getBot().commandManager.getCommands());
            builder.setTitle( Main.getBot().config.getMessage("cmd-list-title"));
            builder.setThumbnail( Main.getBot().jda.getSelfUser().getAvatarUrl());

        } else if ( Main.getBot().commandManager.getCommand(args[0]) != null) {
            commands.add( Main.getBot().commandManager.getCommand(args[0]));
        } else {
            cmd.reply( Main.getBot().config.getMessage("command-not-found", args[0])).queue();
            return;
        }

        for (final Command command : commands) {
            final MessageEmbed.Field field = getCommandDescriptionField(command);

            if (field != null) {
                builder.addField(field);
            }
        }

        builder.setColor( Main.getBot().config.color);
        cmd.getChannel().asTextChannel().sendMessageEmbeds(builder.build()).queue();
    }

    public MessageEmbed.Field getCommandDescriptionField(final Command command) {
        if (command == null || command.getName() == null) {
            return null;
        }

        final String aliases = command.getAliases() != null ? String.join(", ", command.getAliases()) : "";
        final String description = command.getDescription() != null ? command.getDescription() : "";
        final String usage = command.getUsage();
        return new MessageEmbed.Field(command.getName(),  Main.getBot().config.getMessage("cmd-info", aliases, description, usage), false);
    }
}
