package com.smpcore.minigames;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.*;

public class BuildMemoryGame implements Listener {

    private final SMPCore plugin;
    private final List<UUID> players = new ArrayList<>();
    private final Location arenaCenter;
    private BukkitTask gameLoop;

    private boolean isRunning = false;
    private boolean isRanked = false;

    private final Map<UUID, Integer> dailyWins = new HashMap<>();
    private final Map<UUID, Long> doubleJumpCooldown = new HashMap<>();
    private int currentDay = LocalDate.now().getDayOfYear();

    // Sistema de Máquina de Estados e Rondas
    private int state = 0; // 0 = Countdown, 1 = Memorize, 2 = Building, 3 = Round End
    private int timer = 3;
    private int currentRound = 1;
    private final int MAX_ROUNDS = 5;
    private final Map<UUID, Integer> scores = new HashMap<>();

    private final Material[][] pattern = new Material[3][3];
    private final Map<Material, Integer> exactBlocks = new HashMap<>();
    private final Material[] possibleColors = {
            Material.RED_CONCRETE, Material.BLUE_CONCRETE,
            Material.YELLOW_CONCRETE, Material.LIME_CONCRETE
    };

    public BuildMemoryGame(SMPCore plugin) {
        this.plugin = plugin;
        this.arenaCenter = new Location(plugin.getSpawnGenManager().getLobbyWorld(), 2000, 100, 2000);
    }

    public void start(List<UUID> matchedPlayers, boolean ranked) {
        if (isRunning) return;
        this.players.clear();
        this.players.addAll(matchedPlayers);
        this.isRanked = ranked;
        this.isRunning = true;
        this.state = 0;
        this.timer = 3;
        this.currentRound = 1;
        this.scores.clear();
        this.doubleJumpCooldown.clear();

        for (UUID u : players) scores.put(u, 0);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        BuildMemoryArenaBuilder.buildArena(arenaCenter);
        setupNextRound();

        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));
            if (p != null) {
                p.setGameMode(GameMode.SURVIVAL);

                // Sem Poções! Limpo e profissional.
                p.setAllowFlight(false);
                p.setFlying(false);

                p.getInventory().clear();

                if (i == 0) p.teleport(arenaCenter.clone().add(0, 1.5, -15));
                else if (i == 1) p.teleport(arenaCenter.clone().add(0, 1.5, 15));
                else if (i == 2) p.teleport(arenaCenter.clone().add(15, 1.5, 0));

                p.sendMessage("§a§lBUILD MEMORY §7» §fRound 1 of " + MAX_ROUNDS);
            }
        }

        startGameLoop();
    }

    private void setupNextRound() {
        exactBlocks.clear();
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                if (Math.random() < 0.3) {
                    pattern[x][z] = Material.AIR;
                } else {
                    Material mat = possibleColors[new Random().nextInt(possibleColors.length)];
                    pattern[x][z] = mat;
                    exactBlocks.put(mat, exactBlocks.getOrDefault(mat, 0) + 1);
                }
            }
        }
    }

    private void startGameLoop() {
        gameLoop = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    this.cancel();
                    return;
                }

                players.removeIf(uuid -> Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline());
                if (players.isEmpty()) {
                    endGame();
                    return;
                }

                // SISTEMA ANTI-QUEDA (Safety Net)
                for (int i = 0; i < players.size(); i++) {
                    Player p = Bukkit.getPlayer(players.get(i));
                    if (p != null) {
                        // Se cair 3 blocos abaixo do nível da arena, é sugado de volta!
                        if (p.getLocation().getY() < arenaCenter.getY() - 3) {
                            Location safeLoc = arenaCenter.clone();
                            if (i == 0) safeLoc.add(0, 1.5, -15);
                            else if (i == 1) safeLoc.add(0, 1.5, 15);
                            else if (i == 2) safeLoc.add(15, 1.5, 0);

                            p.teleport(safeLoc);
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                        }
                    }
                }

                if (state == 0) {
                    if (timer > 0) {
                        playSoundAll(Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f);
                        sendTitleAll("§e" + timer, "§7Round " + currentRound + " starting...", 0, 25, 0);
                        timer--;
                    } else {
                        state = 1;
                        timer = 5;
                        showPattern(arenaCenter);
                        playSoundAll(Sound.BLOCK_NOTE_BLOCK_CHIME, 2.0f);
                        sendTitleAll("§b§lMEMORIZE!", "§7You can FLY now!", 5, 40, 5);

                        for (UUID uuid : players) {
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null) {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                            }
                        }
                    }
                }
                else if (state == 1) {
                    if (timer > 0) {
                        playSoundAll(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f);
                        timer--;
                    } else {
                        state = 2;
                        timer = 15;
                        hidePattern(arenaCenter);
                        giveBlocks();
                        playSoundAll(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                        sendTitleAll("§c§lBUILD!", "§7Double-Jump & Insta-Break enabled!", 5, 40, 5);

                        for (UUID uuid : players) {
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null) {
                                p.setFlying(false);
                                p.setAllowFlight(true);
                            }
                        }
                    }
                }
                else if (state == 2) {
                    if (timer > 0) {
                        if (timer <= 5) {
                            playSoundAll(Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f);
                            sendTitleAll("", "§c" + timer + " seconds left!", 0, 20, 0);
                        } else if (timer % 5 == 0) {
                            playSoundAll(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f);
                        }

                        Player roundWinner = checkWinners();
                        if (roundWinner != null) {
                            handleRoundWin(roundWinner);
                            return;
                        }
                        timer--;
                    } else {
                        playSoundAll(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f);
                        sendTitleAll("§c§lTIME'S UP!", "§7Nobody completed the puzzle.", 10, 60, 10);
                        endRound();
                    }
                }
                else if (state == 3) {
                    if (timer > 0) {
                        timer--;
                    } else {
                        if (currentRound >= MAX_ROUNDS) {
                            finishGameOverall();
                        } else {
                            currentRound++;
                            state = 0;
                            timer = 3;
                            clearPlayerPlatforms();
                            setupNextRound();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // O NOVO INSTA-BREAK PREMIUM (MÃO VAZIA)
    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;

        // Cancela sempre o dano para proteger o chão da arena (Quartzo e Relva)
        e.setCancelled(true);

        if (state == 2) {
            org.bukkit.block.Block b = e.getBlock();
            Material type = b.getType();

            boolean isColor = false;
            for (Material mat : possibleColors) {
                if (mat == type) { isColor = true; break; }
            }

            // Se for cimento, apaga instantaneamente e devolve o bloco ao jogador!
            if (isColor) {
                b.setType(Material.AIR);
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
                p.getInventory().addItem(new ItemStack(type, 1));
            }
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;

        if (state == 2) {
            e.setCancelled(true);
            p.setFlying(false);

            long now = System.currentTimeMillis();
            if (now - doubleJumpCooldown.getOrDefault(p.getUniqueId(), 0L) < 1000) {
                return;
            }
            doubleJumpCooldown.put(p.getUniqueId(), now);

            Vector v = p.getLocation().getDirection().multiply(1.2).setY(0.9);
            p.setVelocity(v);
            p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);

        } else if (state == 0 || state == 3) {
            e.setCancelled(true);
            p.setFlying(false);
        }
    }

    private void showPattern(Location center) {
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        org.bukkit.World w = center.getWorld();

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                if (pattern[x][z] != Material.AIR) {
                    w.getBlockAt(cx - 1 + x, cy + 1, cz - 1 + z).setType(pattern[x][z]);
                }
            }
        }
    }

    private void hidePattern(Location center) {
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        org.bukkit.World w = center.getWorld();

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                w.getBlockAt(cx - 1 + x, cy + 1, cz - 1 + z).setType(Material.AIR);
            }
        }
    }

    private void clearPlayerPlatforms() {
        org.bukkit.World w = arenaCenter.getWorld();
        int cx = arenaCenter.getBlockX();
        int cy = arenaCenter.getBlockY();
        int cz = arenaCenter.getBlockZ();

        for (int i = 0; i < players.size(); i++) {
            int px = cx, pz = cz;
            if (i == 0) pz = cz - 15;
            else if (i == 1) pz = cz + 15;
            else if (i == 2) px = cx + 15;

            for (int x = -1; x <= 1; x++) {
                for (int y = 1; y <= 3; y++) {
                    for (int z = -1; z <= 1; z++) {
                        w.getBlockAt(px + x, cy + y, pz + z).setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void giveBlocks() {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.getInventory().clear();
                for (Map.Entry<Material, Integer> entry : exactBlocks.entrySet()) {
                    p.getInventory().addItem(new ItemStack(entry.getKey(), entry.getValue()));
                }
            }
        }
    }

    private Player checkWinners() {
        org.bukkit.World w = arenaCenter.getWorld();
        int cx = arenaCenter.getBlockX();
        int cy = arenaCenter.getBlockY();
        int cz = arenaCenter.getBlockZ();

        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));
            if (p == null) continue;

            int px = cx;
            int pz = cz;

            if (i == 0) pz = cz - 15;
            else if (i == 1) pz = cz + 15;
            else if (i == 2) px = cx + 15;

            boolean correct = true;
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Material placed = w.getBlockAt(px - 1 + x, cy + 1, pz - 1 + z).getType();
                    if (placed != pattern[x][z]) {
                        correct = false;
                        break;
                    }
                }
                if (!correct) break;
            }

            if (correct) return p;
        }
        return null;
    }

    private void handleRoundWin(Player winner) {
        playSoundAll(Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f);
        sendTitleAll("§a§l" + winner.getName() + " WINS!", "§7Point awarded!", 10, 60, 10);

        scores.put(winner.getUniqueId(), scores.getOrDefault(winner.getUniqueId(), 0) + 1);

        endRound();
    }

    private void endRound() {
        state = 3;
        timer = 4;

        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.getInventory().clear();
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }
    }

    private void finishGameOverall() {
        Player overallWinner = null;
        int highestScore = -1;

        for (Map.Entry<UUID, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                overallWinner = Bukkit.getPlayer(entry.getKey());
            }
        }

        if (overallWinner != null && highestScore > 0) {
            sendTitleAll("§6§lGAME OVER", "§a" + overallWinner.getName() + " won with " + highestScore + " points!", 10, 80, 10);
            playSoundAll(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f);

            if (isRanked) {
                giveDailyReward(overallWinner);
            } else {
                overallWinner.sendMessage("§e§lTRAINING COMPLETE! §7You got the most points!");
            }
        } else {
            sendTitleAll("§c§lGAME OVER", "§7It's a tie / Nobody won!", 10, 80, 10);
        }

        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.teleport(plugin.getSpawnGenManager().getLobbyWorld().getSpawnLocation());
                p.setAllowFlight(false);
                p.setFlying(false);
                plugin.getMinigameManager().restorePlayer(p);
            }
        }
        endGame();
    }

    private void giveDailyReward(Player winner) {
        int today = LocalDate.now().getDayOfYear();
        if (today != currentDay) {
            dailyWins.clear();
            currentDay = today;
        }

        UUID uuid = winner.getUniqueId();
        int wins = dailyWins.getOrDefault(uuid, 0) + 1;
        dailyWins.put(uuid, wins);

        double reward = switch (wins) {
            case 1 -> 50000000.0;
            case 2 -> 10000000.0;
            case 3 -> 5000000.0;
            case 4 -> 1000000.0;
            case 5 -> 500000.0;
            default -> 50000.0;
        };

        plugin.getEconomyManager().deposit(Bukkit.getOfflinePlayer(uuid), reward);
        String formattedReward = String.format("%,.0f", reward).replace(",", ".");
        winner.sendMessage("§a§l+$" + formattedReward + " Coins! §7(Ranked Win #" + wins + " today)");
    }

    private void playSoundAll(Sound sound, float pitch) {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.playSound(p.getLocation(), sound, 1f, pitch);
        }
    }

    private void sendTitleAll(String title, String sub, int in, int stay, int out) {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendTitle(title, sub, in, stay, out);
        }
    }

    private void endGame() {
        isRunning = false;
        players.clear();
        if (gameLoop != null) gameLoop.cancel();
        HandlerList.unregisterAll(this);
    }
    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
    }
    public boolean isRunning() { return isRunning; }
}