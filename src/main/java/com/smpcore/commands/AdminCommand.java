package com.smpcore.commands;

import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final LangManager langManager;

    public AdminCommand(EconomyManager economyManager, LangManager langManager) {
        this.economyManager = economyManager;
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smpcore.admin")) {
            sender.sendMessage(langManager.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "give":
                handleGive(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            default:
                sendUsage(sender);
                break;
        }
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /eco give <player> <amount>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(langManager.getMessage("messages.player-not-found", "%player%", args[1]));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.getMessage("commands.pay.invalid-amount"));
            return;
        }
        if (amount <= 0) {
            sender.sendMessage(langManager.getMessage("commands.pay.invalid-amount"));
            return;
        }
        economyManager.deposit(target, amount);
        sender.sendMessage(ChatColor.GREEN + "You gave $" + String.format("%,.2f", amount) + " to " + target.getName() + ".");
        if (sender instanceof Player) {
            Sounds.playSuccess((Player) sender);
        }
        target.sendMessage(ChatColor.GREEN + "You received $" + String.format("%,.2f", amount) + " from an administrator.");
        Sounds.playSuccess(target);
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /eco remove <player> <amount>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(langManager.getMessage("messages.player-not-found", "%player%", args[1]));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.getMessage("commands.pay.invalid-amount"));
            return;
        }
        if (amount <= 0) {
            sender.sendMessage(langManager.getMessage("commands.pay.invalid-amount"));
            return;
        }
        economyManager.withdraw(target, amount);
        sender.sendMessage(ChatColor.GREEN + "You removed $" + String.format("%,.2f", amount) + " from " + target.getName() + ".");
        if (sender instanceof Player) {
            Sounds.playSuccess((Player) sender);
        }
        target.sendMessage(ChatColor.RED + "An administrator removed $" + String.format("%,.2f", amount) + " from your account.");
        Sounds.playError(target);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- SMPCore Admin Commands ---");
        sender.sendMessage(ChatColor.YELLOW + "/eco give <player> <amount> - Gives money to a player.");
        sender.sendMessage(ChatColor.YELLOW + "/eco remove <player> <amount> - Removes money from a player.");
    }
}