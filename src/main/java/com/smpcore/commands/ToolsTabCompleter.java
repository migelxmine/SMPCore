package com.smpcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> tools = new ArrayList<>();
            tools.add("drill");
            tools.add("fasty");

            StringUtil.copyPartialMatches(args[0], tools, completions);
            Collections.sort(completions);
            return completions;
        }

        return Collections.emptyList();
    }
}