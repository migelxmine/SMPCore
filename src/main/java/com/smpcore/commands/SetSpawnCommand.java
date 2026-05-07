package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final SMPCore plugin;

    public SetSpawnCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        
        if (!p.hasPermission("smpcore.admin")) {
            p.sendMessage("§cNo permission.");
            return true;
        }

        World w = p.getWorld();

        
        w.setSpawnLocation(p.getLocation());

        p.sendMessage("§a§lSUCCESS: §fSpawn point for world §e" + w.getName() + " §fset to your location!");
        p.sendMessage("§7X: " + p.getLocation().getBlockX() + " Y: " + p.getLocation().getBlockY() + " Z: " + p.getLocation().getBlockZ());

        return true;
    }
}