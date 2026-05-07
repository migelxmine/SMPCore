package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TprManager {

    private final SMPCore plugin;
    private final Random random = new Random();
    private final Set<UUID> teleportingPlayers = new HashSet<>();

    private final int MAX_X = 5000;
    private final int MAX_Z = 5000;

    public TprManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player p, World world, double cost) {
        if (plugin.getCombatManager() != null && plugin.getCombatManager().isInCombat(p)) {
            p.sendMessage("§cYou cannot RTP while in combat!");
            Sounds.playError(p);
            return;
        }

        if (teleportingPlayers.contains(p.getUniqueId())) {
            p.sendMessage("§cYou are already preparing a teleport!");
            return;
        }

        Location startLoc = p.getLocation();
        teleportingPlayers.add(p.getUniqueId());


        CompletableFuture<Location> futureSafeLoc = new CompletableFuture<>();
        findSafeLocationAsync(p, world, 30, futureSafeLoc);

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

                    if (futureSafeLoc.isDone()) {
                        try {
                            Location safeLoc = futureSafeLoc.get();
                            if (safeLoc != null) {
                                if (cost > 0) plugin.getEconomyManager().withdraw(p, cost);
                                p.teleport(safeLoc);
                                p.sendTitle("§a§lWHOOSH!", "§7Randomly teleported.", 10, 40, 10);
                                Sounds.playSuccess(p);
                            } else {
                                p.sendMessage("§cCould not find a safe location. Please try again.");
                                Sounds.playError(p);
                            }
                        } catch (Exception e) {
                            p.sendMessage("§cAn error occurred during teleport.");
                        }
                        teleportingPlayers.remove(p.getUniqueId());
                        this.cancel();
                    } else {

                        p.sendActionBar("§9Processing...");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void findSafeLocationAsync(Player p, World world, int attemptsLeft, CompletableFuture<Location> future) {
        if (attemptsLeft <= 0) {
            future.complete(null);
            return;
        }

        int x = random.nextInt(MAX_X * 2) - MAX_X;
        int z = random.nextInt(MAX_Z * 2) - MAX_Z;

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!p.isOnline()) {
                    future.complete(null);
                    return;
                }

                int groundY = world.getHighestBlockYAt(x, z);

                if (world.getEnvironment() == World.Environment.NETHER && groundY >= 126) {
                    findSafeLocationAsync(p, world, attemptsLeft - 1, future);
                    return;
                }

                Block ground = world.getBlockAt(x, groundY, z);
                Block feet = ground.getRelative(BlockFace.UP);
                Block head = feet.getRelative(BlockFace.UP);

                if (isSafe(ground.getType()) && feet.getType().isAir() && head.getType().isAir()) {
                    Location safeLoc = new Location(world, x + 0.5, groundY + 1.0, z + 0.5);
                    future.complete(safeLoc);
                } else {
                    findSafeLocationAsync(p, world, attemptsLeft - 1, future);
                }
            });
        }).exceptionally(ex -> {
            future.complete(null);
            return null;
        });
    }

    private boolean isSafe(Material type) {
        return !type.equals(Material.LAVA) &&
                !type.equals(Material.WATER) &&
                !type.equals(Material.MAGMA_BLOCK) &&
                !type.equals(Material.CACTUS) &&
                !type.equals(Material.FIRE) &&
                !type.equals(Material.CAMPFIRE) &&
                !type.equals(Material.SOUL_CAMPFIRE) &&
                !type.equals(Material.SWEET_BERRY_BUSH) &&
                !type.equals(Material.SEAGRASS) &&
                !type.equals(Material.KELP) &&
                !type.isAir() &&
                type.isSolid();
    }


    public boolean isTprEnabled(Player p) {
        return plugin.getSettingsManager().getSetting(p, "tpa_requests");
    }

    public void toggleTpr(Player p) {
        boolean newState = plugin.getSettingsManager().toggleSetting(p, "tpa_requests");
        if (newState) p.sendMessage("§aTeleport Requests enabled.");
        else p.sendMessage("§cTeleport Requests disabled.");
    }
}