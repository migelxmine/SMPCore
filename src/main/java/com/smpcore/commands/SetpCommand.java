package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.listeners.SpawnListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetpCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final SpawnListener listener;

    public SetpCommand(SMPCore plugin, SpawnListener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (!p.hasPermission("smpcore.admin")) {
            p.sendMessage("§cPermission denied.");
            return true;
        }

        
        if (args.length > 0 && args[0].equalsIgnoreCase("build")) {
            listener.toggleBuildMode(p);
            return true;
        }

        
        if (args.length > 0 && args[0].equalsIgnoreCase("tpspawn")) {
            p.teleport(plugin.getSpawnGenManager().getSpawnWorld().getSpawnLocation());
            p.sendMessage("§aTeleported to SMP_Spawn.");
            return true;
        }

        p.sendMessage("§cUsage: /setp <build|tpspawn>");
        return true;
    }
}