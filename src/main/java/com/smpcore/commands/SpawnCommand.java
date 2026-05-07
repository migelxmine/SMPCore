package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final SMPCore plugin;

    public SpawnCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player p = (Player) sender;

        
        if (plugin.getConfig().getLocation("spawn") == null) {
            p.sendMessage("§cSpawn is not set yet! Ask an admin to do /setspawn.");
            return true;
        }

        Location spawnLoc = plugin.getConfig().getLocation("spawn");

        
        
        p.sendMessage("§eTeleporting to Spawn...");
        plugin.getTeleportManager().queueTeleport(p, spawnLoc, 5);

        return true;
    }
}