package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.utils.CustomItemManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToolsCommand implements CommandExecutor {

    private final CustomItemManager itemManager;

    public ToolsCommand(SMPCore plugin) {
        this.itemManager = new CustomItemManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (!p.hasPermission("smpcore.admin")) {
            p.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /givetool <drill|fasty>");
            return true;
        }

        if (args[0].equalsIgnoreCase("drill")) {
            p.getInventory().addItem(itemManager.getDrill());
            p.sendMessage(ChatColor.GREEN + "Gave you a Drill!");
        } else if (args[0].equalsIgnoreCase("fasty")) {
            p.getInventory().addItem(itemManager.getFasty());
            p.sendMessage(ChatColor.GREEN + "Gave you a Fasty!");
        } else {
            p.sendMessage(ChatColor.RED + "Unknown tool. Use 'drill' or 'fasty'.");
        }

        return true;
    }
}