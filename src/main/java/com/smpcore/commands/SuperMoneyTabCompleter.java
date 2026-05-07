package com.smpcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuperMoneyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        
        if (args.length == 1) {
            List<String> sub = new ArrayList<>();
            sub.add("shop");
            sub.add("bal");
            sub.add("convert");

            StringUtil.copyPartialMatches(args[0], sub, completions);
            Collections.sort(completions);
            return completions;
        }

        
        if (args.length == 2 && args[0].equalsIgnoreCase("convert")) {
            List<String> amounts = new ArrayList<>();
            amounts.add("10000");
            amounts.add("50000");
            amounts.add("100000");

            StringUtil.copyPartialMatches(args[1], amounts, completions);
            return completions;
        }

        return Collections.emptyList();
    }
}