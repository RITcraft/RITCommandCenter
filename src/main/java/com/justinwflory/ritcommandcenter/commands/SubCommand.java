package com.justinwflory.ritcommandcenter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Rogue on 10/25/2015.
 */
public interface SubCommand extends CommandExecutor {

    public void exec(CommandSender sender, String... args);

    public String getName();

    default boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("Error: Attempted to execute a subcommand");
        return false;
    }

    default String usage() {
        return this.getName();
    }

}
