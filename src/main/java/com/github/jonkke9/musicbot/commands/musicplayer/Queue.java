package com.github.jonkke9.musicbot.commands.musicplayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.github.jonkke9.musicbot.Main;
import com.github.jonkke9.musicbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * The Queue class implements the Command interface and is responsible for handling command "queue"
 * The queue command is used to display all tracks in the guilds queue
 */
public final class Queue implements Command {

    private static final int ITEMS_PER_PAGE = 10; // this should be 25 or smaller because JDA only allows max 25 field per embed message

    @Override
    public String getUsage() {
        return Main.getBot().config.prefix + getName();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public List<String> getAliases() {
        return List.of("q");
    }

    @Override
    public String getDescription() {
        return Main.getBot().config.getMessage("cmd-queue-description");
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void execute(final Message cmd, final String[] args) {

        final BlockingQueue<AudioTrack> queue =  Main.getBot().playerManager.getMusicManager(cmd.getGuild()).scheduler.getQueue();
        final AudioTrack[] queueArray = queue.toArray(new AudioTrack[0]);

        if (queueArray.length == 0) {
            cmd.getChannel().sendMessage( Main.getBot().config.getMessage("cmd-queue-empty")).queue();
            return;
        }

        final int page = parsePageNumber(args, cmd);

        if (page >= 0) {
            cmd.getChannel().asTextChannel().sendMessageEmbeds(queueMessage(queueArray, page)).queue();
        }

    }

    private MessageEmbed queueMessage(final AudioTrack[] queue, final int page) {

        final EmbedBuilder builder = new EmbedBuilder();
        final int pages = (queue.length + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        final int pageClamped = Math.max(0, Math.min(pages - 1, page)); // clamp the given page number to 0 - amount of pages

        // Add each track on the current page to the embed message
        for (int i = pageClamped * ITEMS_PER_PAGE; i < (pageClamped + 1) * ITEMS_PER_PAGE; i++) {
            if (i >= queue.length) {
                break;
            }
            final AudioTrack track = queue[i];
            builder.addField( Main.getBot().config.getMessage("cmd-queue-track", String.valueOf(i + 1), track.getInfo().title), track.getInfo().uri, false);
        }

        //cosmetic stuff
        builder.setTitle( Main.getBot().config.getMessage("cmd-queue-title"));
        builder.setColor( Main.getBot().config.color);
        builder.setDescription( Main.getBot().config.getMessage("cmd-queue-page", String.valueOf(pageClamped + 1), String.valueOf(pages)));

        return builder.build();
    }

    private int parsePageNumber(final String[] args, final Message cmd) {
        int page = 0;
        if (args.length > 0 ) {
            try{
                page = Integer.parseInt(args[0]) -1;
            }
            catch (NumberFormatException ex){
                cmd.reply( Main.getBot().config.getMessage("incorrect-num", args[0])).queue();
                page = -1;
            }
        }
        return page;
    }
}
