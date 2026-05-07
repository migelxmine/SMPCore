package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueueManager {

    private final SMPCore plugin;
    private final List<QueueEntry> queue = new ArrayList<>();


    private final int MAX_PLAYERS = 50;

    private Location queueLocation;

    public QueueManager(SMPCore plugin) {
        this.plugin = plugin;
        startQueueTask();
    }

    private void setupQueueLocation() {
        if (plugin.getSpawnGenManager().getSpawnWorld() != null) {

            this.queueLocation = new Location(plugin.getSpawnGenManager().getSpawnWorld(), 1000.5, 250, 1000.5);

            this.queueLocation.clone().subtract(0, 1, 0).getBlock().setType(Material.BARRIER);
        }
    }

    public void addPlayer(Player p) {
        if (queueLocation == null) setupQueueLocation();


        if (queueLocation != null) p.teleport(queueLocation);

        p.setGameMode(GameMode.ADVENTURE);
        p.setAllowFlight(false);
        p.setFlying(false);


        for (Player online : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, online);
            online.hidePlayer(plugin, p);
        }


        int priority = calculatePriority(p);
        queue.add(new QueueEntry(p.getUniqueId(), priority, System.currentTimeMillis()));
        sortQueue();

        p.sendTitle("§6§lQUEUE", "§7Verifying priority ", 10, 60, 20);
    }

    public void removePlayer(Player p) {
        queue.removeIf(entry -> entry.uuid.equals(p.getUniqueId()));
    }

    public boolean isInQueue(Player p) {
        return queue.stream().anyMatch(entry -> entry.uuid.equals(p.getUniqueId()));
    }

    private int calculatePriority(Player p) {
        RankManager rm = plugin.getRankManager();
        RankManager.StaffRole role = rm.getStaffRole(p);
        RankManager.SocialRank rank = rm.getSocialRank(p);


        if (role == RankManager.StaffRole.OWNER) return 100;
        if (role == RankManager.StaffRole.SR_ADMIN) return 90;
        if (role == RankManager.StaffRole.JR_ADMIN) return 80;
        if (role == RankManager.StaffRole.EV_HOST) return 70;


        if (rank == RankManager.SocialRank.VIP) return 50;
        if (rank == RankManager.SocialRank.EXCLUSIVE) return 30;

        return 10;
    }

    private void sortQueue() {
        queue.sort((a, b) -> {
            if (a.priority != b.priority) {
                return Integer.compare(b.priority, a.priority);
            }
            return Long.compare(a.timestamp, b.timestamp);
        });
    }

    private void startQueueTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (queue.isEmpty()) return;


                for (int i = 0; i < queue.size(); i++) {
                    Player p = Bukkit.getPlayer(queue.get(i).uuid);
                    if (p != null) {
                        p.sendActionBar("§e position in line: §9#" + (i + 1) + " §7| §aPriority: " + getPriorityName(queue.get(i).priority));
                    }
                }


                long activePlayers = Bukkit.getOnlinePlayers().stream().filter(p -> !isInQueue(p)).count();


                if (activePlayers < MAX_PLAYERS) {
                    QueueEntry first = queue.remove(0);
                    Player p = Bukkit.getPlayer(first.uuid);
                    if (p != null) {
                        releasePlayer(p);
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void releasePlayer(Player p) {
        p.sendTitle("§a§lJoining...", "§7Connected successfully", 10, 40, 10);
        Sounds.playSuccess(p);


        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!isInQueue(online)) {
                p.showPlayer(plugin, online);
                online.showPlayer(plugin, p);
            }
        }


        if (!p.hasPlayedBefore()) {
            plugin.getSpawnGenManager().teleportToSpawn(p);
            p.setGameMode(GameMode.SURVIVAL);
        } else {

            plugin.getSpawnGenManager().teleportToSpawn(p);
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    private String getPriorityName(int priority) {
        if (priority >= 70) return "§cSTAFF";
        if (priority == 50) return "§aVIP";
        if (priority == 30) return "§6EXCLUSIVE";
        return "§7MEMBER";
    }

    private static class QueueEntry {
        UUID uuid;
        int priority;
        long timestamp;

        public QueueEntry(UUID uuid, int priority, long timestamp) {
            this.uuid = uuid;
            this.priority = priority;
            this.timestamp = timestamp;
        }
    }
}