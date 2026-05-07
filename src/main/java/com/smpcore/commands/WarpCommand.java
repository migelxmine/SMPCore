package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements TabExecutor {

    private final SMPCore plugin;

    public WarpCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage("§cUsage: /warp <location>");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            teleportToSpawn(p);
            return true;
        } else if (args[0].equalsIgnoreCase("lobby")) {
            teleportToLobby(p);
            return true;
        }

        p.sendMessage("§cWarp not found. Available: spawn, lobby");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("spawn");
            suggestions.add("lobby");
        }

        return suggestions;
    }

    private void teleportToSpawn(Player p) {
        World spawnWorld = Bukkit.getWorld("SMP_Spawn");

        if (spawnWorld != null) {
            Location loc = new Location(spawnWorld, 895.5, 167, 736.5, 90, 0);
            plugin.getTeleportManager().teleportWithWarmup(p, loc);
        } else {
            p.sendMessage("§cError: Spawn world not loaded.");
        }
    }

    private void teleportToLobby(Player p) {
        World lobbyWorld = Bukkit.getWorld("SMP_Lobby");

        if (lobbyWorld != null) {

            Location loc = new Location(lobbyWorld, 15.5, 3, -12.5, 0, 0);
            plugin.getTeleportManager().teleportWithWarmup(p, loc);
        } else {
            p.sendMessage("§cError: Lobby world not loaded.");
        }
    }
}