package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.SuperShopMenu; 
import com.smpcore.utils.SuperMoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SuperMoneyCommand implements CommandExecutor, TabCompleter {

    private final SMPCore plugin;
    private final SuperMoneyManager sm;

    public SuperMoneyCommand(SMPCore plugin) {
        this.plugin = plugin;
        this.sm = plugin.getSuperMoneyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            if (sender instanceof Player) {
                SuperShopMenu.open((Player) sender); 
                return true;
            } else {
                sender.sendMessage("§cOnly players can open the shop.");
                return true;
            }
        }

        
        if (!sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cYou don't have permission to manage Super Money.");
            return true;
        }

        String sub = args[0].toLowerCase();

        
        if (sub.equals("check") && args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }
            sender.sendMessage("§dSuperMoney: §f" + target.getName() + " has §d" + sm.getBalance(target) + " SM");
            return true;
        }

        
        if (sub.equals("reset") && args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }
            sm.setBalance(target, 0);
            sender.sendMessage("§aReset SM of " + target.getName());
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /sm <give|take|set> <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount.");
            return true;
        }

        switch (sub) {
            case "give":
                sm.deposit(target, amount);
                sender.sendMessage("§aGave §d" + amount + " SM §ato " + target.getName());
                break;
            case "take":
                sm.withdraw(target, amount);
                sender.sendMessage("§cTook §d" + amount + " SM §cfrom " + target.getName());
                break;
            case "set":
                sm.setBalance(target, amount);
                sender.sendMessage("§eSet §d" + amount + " SM §efor " + target.getName());
                break;
            default:
                sender.sendMessage("§cUnknown sub-command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("smpcore.admin")) return Collections.emptyList();
        if (args.length == 1) return StringUtil.copyPartialMatches(args[0], Arrays.asList("give", "take", "set", "check", "reset"), new ArrayList<>());
        return null;
    }
}