package com.smpcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EcoTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> completions = new ArrayList<>();

        
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("give");
            subCommands.add("remove");

            
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            Collections.sort(completions);
            return completions;
        }

        
        if (args.length == 2) {
            
            return null;
        }

        
        if (args.length == 3) {
            List<String> amounts = new ArrayList<>();
            amounts.add("100");
            amounts.add("1000");
            amounts.add("10000");
            amounts.add("100000");

            StringUtil.copyPartialMatches(args[2], amounts, completions);
            return completions;
        }

        return Collections.emptyList();
    }
}