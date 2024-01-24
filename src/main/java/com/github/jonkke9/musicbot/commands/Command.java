package com.github.jonkke9.musicbot.commands;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;

// This interface defines the structure for a command used by this bot
public interface Command {

    String getName(); // primary name for the command

    List<String> getAliases(); // additional names for the command

    String getDescription(); // information shown about the command in the command list

    boolean canRun();

    String getUsage(); // correct form in witch the command should be used

    void execute(Message cmd, String ...args); // is run when user uses the command
}
