/*
 * Copyright 2015 RITcraft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.justinwflory.ritcommandcenter;

import com.justinwflory.ritcommandcenter.commands.FoodCommand;
import com.justinwflory.ritcommandcenter.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RitCommand implements CommandExecutor {

    private Map<String, SubCommand> subcommands = new HashMap<String, SubCommand>() {{
        for (SubCommand sc : Arrays.asList(new FoodCommand())) {
            put(sc.getName(), sc);
        }
    }};
    private final String usage = this.getUsage();

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(this.usage);
            return true;
        }
        SubCommand arg = this.subcommands.get(args[0]);
        if (arg == null) {
            sender.sendMessage(this.usage);
        } else {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            arg.exec(sender, newArgs);
        }
        return true;
    }

    private String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GREEN + "Available commands:\n");
        this.subcommands.values().stream().map(SubCommand::usage).map(s -> "/rit " + s).forEach(sb::append);
        return sb.toString();
    }
}
