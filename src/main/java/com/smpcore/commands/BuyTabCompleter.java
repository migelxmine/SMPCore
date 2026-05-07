package com.smpcore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> materials = new ArrayList<>();
            String input = args[0].toUpperCase();

            
            for (Material m : Material.values()) {
                
                if (m.isItem()) {
                    materials.add(m.name());
                }
            }

            
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(input, materials, completions);
            Collections.sort(completions);
            return completions;
        }
        return Collections.emptyList();
    }
}