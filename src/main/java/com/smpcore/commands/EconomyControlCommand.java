package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EconomyControlCommand implements CommandExecutor {

    private final SMPCore plugin;

    public EconomyControlCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cPermission denied.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "freeze":
                handleFreeze(sender, args);
                break;
            case "resume":
                handleResume(sender, args);
                break;
            case "ban":
                handleBan(sender, args, true);
                break;
            case "unban":
                handleBan(sender, args, false);
                break;
            default:
                sendHelp(sender);
        }
        return true;
    }

    private void handleFreeze(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /economy freeze <player|server> [reason]");
            return;
        }
        String targetName = args[1];
        String reason = args.length > 2 ? Arrays.stream(args).skip(2).collect(Collectors.joining(" ")) : "No reason specified";

        if (targetName.equalsIgnoreCase("server")) {
            plugin.getEconomyControlManager().setServerFrozen(true);
            Bukkit.broadcastMessage("§c§lSERVER: §cThe global economy has been temporarily frozen.");
            sender.sendMessage("§aServer economy frozen. Reason: " + reason);
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            plugin.getEconomyControlManager().setFrozen(target.getUniqueId(), true, reason);
            sender.sendMessage("§aPlayer " + target.getName() + " frozen. Reason: " + reason);
            if (target.isOnline()) {
                target.getPlayer().sendMessage("§c§lALERT: §cYour bank account has been frozen. Reason: " + reason);
            }
        }
    }

    private void handleResume(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /economy resume <player|server>");
            return;
        }
        if (args[1].equalsIgnoreCase("server")) {
            plugin.getEconomyControlManager().setServerFrozen(false);
            Bukkit.broadcastMessage("§a§lSERVER: §aThe global economy has been unfrozen.");
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            plugin.getEconomyControlManager().setFrozen(target.getUniqueId(), false, null);
            sender.sendMessage("§aPlayer " + target.getName() + " unfrozen.");
        }
    }

    private void handleBan(CommandSender sender, String[] args, boolean ban) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /economy " + (ban ? "ban" : "unban") + " <player> [reason]");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String reason = args.length > 2 ? Arrays.stream(args).skip(2).collect(Collectors.joining(" ")) : "Admin Action";

        plugin.getEconomyControlManager().setEconomyBanned(target.getUniqueId(), ban, reason);
        sender.sendMessage("§aPlayer " + target.getName() + " economy " + (ban ? "BANNED" : "UNBANNED"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§lSMPCore Economy Control:");
        sender.sendMessage("§e/economy freeze <player|server> [reason]");
        sender.sendMessage("§e/economy resume <player|server>");
        sender.sendMessage("§e/economy ban <player>");
        sender.sendMessage("§e/economy unban <player>");
    }
}