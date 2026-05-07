package com.smpcore.minigames;

import com.smpcore.SMPCore;
import com.smpcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IceControlGame {

    private final SMPCore plugin;
    private final List<UUID> players = new ArrayList<>();
    private final Location arenaCenter;
    private final int killY;
    private BukkitTask gameLoop;
    private boolean isRunning = false;

    // Daily Diminishing Returns System
    private final Map<UUID, Integer> dailyWins = new HashMap<>();
    private int currentDay = LocalDate.now().getDayOfYear();

    public IceControlGame(SMPCore plugin) {
        this.plugin = plugin;
        this.arenaCenter = new Location(plugin.getSpawnGenManager().getLobbyWorld(), 1000, 100, 1000);
        this.killY = 90;
    }

    public void start(List<UUID> matchedPlayers) {
        if (isRunning) return;
        this.players.clear();
        this.players.addAll(matchedPlayers);
        this.isRunning = true;

        IceControlArenaBuilder.buildArena(arenaCenter);

        ItemStack stick = ItemBuilder.of(Material.STICK, "§b§lICE BREAKER", "§7Hit players off the edge!");
        stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);

        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.teleport(arenaCenter.clone().add(0, 1.5, 0));
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
                p.getInventory().setItem(0, stick);
                p.sendMessage("§b§lICE CONTROL §7» §fSurvive and knock everyone off!");
            }
        }

        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    this.cancel();
                    return;
                }

                List<UUID> toRemove = new ArrayList<>();

                for (UUID uuid : players) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null || !p.isOnline()) {
                        toRemove.add(uuid);
                        continue;
                    }

                    if (p.getLocation().getY() < killY) {
                        p.teleport(plugin.getSpawnGenManager().getLobbyWorld().getSpawnLocation());
                        p.getInventory().clear();
                        p.sendMessage("§c§lELIMINATED! §7You fell off the arena.");
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1f, 1f);
                        plugin.getMinigameManager().restorePlayer(p);
                        toRemove.add(uuid);
                    }
                }

                players.removeAll(toRemove);

                if (players.size() == 1) {
                    Player winner = Bukkit.getPlayer(players.get(0));
                    if (winner != null) {
                        winner.sendMessage("§a§lVICTORY! §7You won Ice Control!");
                        winner.sendTitle("§b§lVICTORY", "§7You survived the ice!", 10, 60, 10);
                        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                        giveDailyReward(winner);

                        winner.teleport(plugin.getSpawnGenManager().getLobbyWorld().getSpawnLocation());
                        winner.getInventory().clear();
                        plugin.getMinigameManager().restorePlayer(winner);
                    }
                    endGame();
                } else if (players.isEmpty()) {
                    endGame();
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void giveDailyReward(Player winner) {
        // Reset the tracker if it's a new real-life day
        int today = LocalDate.now().getDayOfYear();
        if (today != currentDay) {
            dailyWins.clear();
            currentDay = today;
        }

        UUID uuid = winner.getUniqueId();
        int wins = dailyWins.getOrDefault(uuid, 0) + 1;
        dailyWins.put(uuid, wins);

        double reward;
        switch (wins) {
            case 1: reward = 5000000.0; break; // 50M
            case 2: reward = 1000000.0; break; // 10M
            case 3: reward = 500000.0; break;  // 5M
            case 4: reward = 100000.0; break;  // 1M
            case 5: reward = 50000.0; break;   // 500k
            default: reward = 50000.0; break;   // 50k
        }

        // Using deposit() which takes an OfflinePlayer
        plugin.getEconomyManager().deposit(Bukkit.getOfflinePlayer(uuid), reward);

        String formattedReward = String.format("%,.0f", reward).replace(",", ".");
        winner.sendMessage("§a§l+$" + formattedReward + " Coins! §7(Win #" + wins + " today)");

        if (wins == 1) {
            winner.sendMessage("§eWow! Your first win of the day gives a MASSIVE bonus!");
        } else if (wins >= 6) {
            winner.sendMessage("§8Reward reduced to minimum (50k). Come back tomorrow for the 50M bonus!");
        }
    }

    private void endGame() {
        isRunning = false;
        players.clear();
        if (gameLoop != null) gameLoop.cancel();
    }
    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
    }
    public boolean isRunning() { return isRunning; }
}