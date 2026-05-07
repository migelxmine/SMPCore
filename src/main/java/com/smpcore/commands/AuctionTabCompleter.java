package com.smpcore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuctionTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String input = args[0].toLowerCase();

            
            suggestions.add("sell");

            
            
            for (Material m : Material.values()) {
                if (m.isItem()) {
                    suggestions.add(m.name().toLowerCase());
                }
            }

            
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(input, suggestions, completions);
            Collections.sort(completions);
            return completions;
        }

        
        if (args.length == 2 && args[0].equalsIgnoreCase("sell")) {
            List<String> amounts = new ArrayList<>();
            amounts.add("100");
            amounts.add("500");
            amounts.add("1000");
            amounts.add("5000");
            amounts.add("10000");

            return StringUtil.copyPartialMatches(args[1], amounts, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}