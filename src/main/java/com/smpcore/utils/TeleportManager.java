package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeleportManager {

    private final SMPCore plugin;

    private final Set<UUID> teleportingPlayers = new HashSet<>();

    public TeleportManager(SMPCore plugin) {
        this.plugin = plugin;
    }


    public void teleportWithWarmup(Player p, Location dest) {

        if (plugin.getCombatManager() != null && plugin.getCombatManager().isInCombat(p)) {
            p.sendMessage("§cYou cannot teleport while in combat!");
            Sounds.playError(p);
            return;
        }

        if (teleportingPlayers.contains(p.getUniqueId())) {
            p.sendMessage("§cYou are already preparing a teleport!");
            return;
        }

        Location startLoc = p.getLocation();
        teleportingPlayers.add(p.getUniqueId());

        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (!p.isOnline()) {
                    teleportingPlayers.remove(p.getUniqueId());
                    this.cancel();
                    return;
                }


                Location currentLoc = p.getLocation();
                if (startLoc.getBlockX() != currentLoc.getBlockX() ||
                        startLoc.getBlockY() != currentLoc.getBlockY() ||
                        startLoc.getBlockZ() != currentLoc.getBlockZ()) {

                    p.sendActionBar("§cTeleport cancelled! You moved.");
                    Sounds.playError(p);
                    teleportingPlayers.remove(p.getUniqueId());
                    this.cancel();
                    return;
                }

                if (count > 0) {
                    p.sendActionBar("§fTeleporting in §9" + count + "§9...");
                    Sounds.playClick(p);
                    count--;
                } else {

                    p.teleport(dest);
                    p.sendTitle("§a§lWHOOSH!", "§7Teleported successfully.", 10, 40, 10);
                    Sounds.playSuccess(p);

                    teleportingPlayers.remove(p.getUniqueId());
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    public void queueTeleport(Player p, Location dest, int seconds) {
        teleportWithWarmup(p, dest);
    }
}