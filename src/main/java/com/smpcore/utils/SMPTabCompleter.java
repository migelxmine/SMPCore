package com.smpcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SMPTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        
        if (cmd.getName().equalsIgnoreCase("order")) {
            if (args.length == 1) {
                
                for (Material mat : Material.values()) {
                    if (mat.isItem()) suggestions.add(mat.name().toLowerCase());
                }
            } else if (args.length == 2) {
                suggestions.addAll(Arrays.asList("1", "16", "32", "64"));
            } else if (args.length == 3) {
                suggestions.addAll(Arrays.asList("100", "500", "1000", "5000"));
            }
        }

        
        else if (cmd.getName().equalsIgnoreCase("eco")) {
            if (args.length == 1) {
                suggestions.addAll(Arrays.asList("add", "set", "remove", "reset"));
            } else if (args.length == 2) {
                Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            }
        }

        
        else if (cmd.getName().equalsIgnoreCase("pay")) {
            if (args.length == 1) {
                Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            }
        }

        
        StringUtil.copyPartialMatches(args[args.length - 1], suggestions, completions);
        Collections.sort(completions);
        return completions;
    }
}