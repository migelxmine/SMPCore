package com.smpcore.minigames;

import com.smpcore.SMPCore;
import com.smpcore.menus.BuildMemoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MinigameManager implements Listener {

    private final SMPCore plugin;
    private final Map<UUID, PlayerData> backedUpData = new HashMap<>();

    // Ice Control
    private final List<UUID> iceControlQueue = new ArrayList<>();
    private final IceControlGame iceControlGame;

    // Build Memory
    private final List<UUID> buildMemorySoloQueue = new ArrayList<>();
    private final List<UUID> buildMemoryRankedQueue = new ArrayList<>();
    private final BuildMemoryGame buildMemoryGame;

    public MinigameManager(SMPCore plugin) {
        this.plugin = plugin;
        this.iceControlGame = new IceControlGame(plugin);
        this.buildMemoryGame = new BuildMemoryGame(plugin);

        // Regista o Manager como um ouvinte para detetar fugas de jogadores
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void joinArcade(Player p, String game) {
        if (game.equals("Build Memory")) {
            BuildMemoryMenu.open(p);
            return;
        }
        processQueueEntry(p, game, false);
    }

    public void joinBuildMemory(Player p, boolean isRanked) {
        processQueueEntry(p, "Build Memory", isRanked);
    }

    private void processQueueEntry(Player p, String game, boolean isRanked) {
        if (backedUpData.containsKey(p.getUniqueId())) {
            p.sendMessage("§c§lERROR: §cYou are already in a game or queue!");
            p.sendMessage("§8(Tip: Type /leave to exit)");
            return;
        }

        backedUpData.put(p.getUniqueId(), new PlayerData(p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getExp(), p.getLevel()));
        p.getInventory().clear();
        p.setExp(0);
        p.setLevel(0);

        p.sendMessage("§a§lARCADE: §7You joined the queue for §f" + game + (isRanked ? " §c(Ranked)" : "") + "§7!");
        p.sendMessage("§8(Tip: Type /leave to exit at any time)");

        if (game.equals("Ice Control")) {
            iceControlQueue.add(p.getUniqueId());
            checkIceControlQueue();
        } else if (game.equals("Build Memory")) {
            if (isRanked) {
                buildMemoryRankedQueue.add(p.getUniqueId());
                checkBuildMemoryRankedQueue();
            } else {
                buildMemorySoloQueue.add(p.getUniqueId());
                checkBuildMemorySoloQueue();
            }
        }
    }

    private void checkIceControlQueue() {
        if (iceControlQueue.size() >= 1 && !iceControlGame.isRunning()) {
            List<UUID> playersToPlay = new ArrayList<>(iceControlQueue.subList(0, Math.min(iceControlQueue.size(), 4)));
            iceControlQueue.removeAll(playersToPlay);
            iceControlGame.start(playersToPlay);
        }
    }

    private void checkBuildMemorySoloQueue() {
        if (buildMemorySoloQueue.size() >= 1 && !buildMemoryGame.isRunning()) {
            List<UUID> playersToPlay = new ArrayList<>(buildMemorySoloQueue.subList(0, 1));
            buildMemorySoloQueue.removeAll(playersToPlay);
            buildMemoryGame.start(playersToPlay, false);
        }
    }

    private void checkBuildMemoryRankedQueue() {
        if (buildMemoryRankedQueue.size() >= 1 && !buildMemoryGame.isRunning()) {
            List<UUID> playersToPlay = new ArrayList<>(buildMemoryRankedQueue.subList(0, Math.min(buildMemoryRankedQueue.size(), 3)));
            buildMemoryRankedQueue.removeAll(playersToPlay);
            buildMemoryGame.start(playersToPlay, true);
        }
    }

    // 🚨 SISTEMA DE SAÍDA DE EMERGÊNCIA 🚨
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!backedUpData.containsKey(p.getUniqueId())) return;

        String cmd = e.getMessage().toLowerCase();
        // Se o jogador tentar usar teleporte ou o nosso novo /leave
        if (cmd.startsWith("/warp") || cmd.startsWith("/spawn") || cmd.startsWith("/home") ||
                cmd.startsWith("/tpa") || cmd.startsWith("/leave") || cmd.startsWith("/rtp")) {

            forceLeave(p);

            // Se foi apenas o comando de sair, cancela para o Bukkit não dar erro de comando inexistente
            if (cmd.equals("/leave")) {
                e.setCancelled(true);
                p.teleport(plugin.getSpawnGenManager().getLobbyWorld().getSpawnLocation());
                p.sendMessage("§c§lARCADE: §7You left the minigame.");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        // Se a net dele for abaixo, damos os itens de volta para o cofre virtual dele!
        if (backedUpData.containsKey(p.getUniqueId())) {
            forceLeave(p);
        }
    }

    public void forceLeave(Player p) {
        UUID uuid = p.getUniqueId();

        // Remove de todas as Filas
        iceControlQueue.remove(uuid);
        buildMemorySoloQueue.remove(uuid);
        buildMemoryRankedQueue.remove(uuid);

        // Remove forçosamente do meio de um jogo
        iceControlGame.removePlayer(p);
        buildMemoryGame.removePlayer(p);

        // Limpa os poderes VIP (Voo/DoubleJump do BuildMemory)
        p.setAllowFlight(false);
        p.setFlying(false);
        p.removePotionEffect(org.bukkit.potion.PotionEffectType.FAST_DIGGING);

        // Restaura os itens na perfeição
        restorePlayer(p);
    }

    public void restorePlayer(Player p) {
        PlayerData data = backedUpData.remove(p.getUniqueId());
        if (data != null) {
            p.getInventory().setContents(data.inventory);
            p.getInventory().setArmorContents(data.armor);
            p.setExp(data.exp);
            p.setLevel(data.level);
            p.sendMessage("§a§lARCADE: §7Your items and XP have been fully restored.");
        }
    }

    private static class PlayerData {
        ItemStack[] inventory; ItemStack[] armor; float exp; int level;
        public PlayerData(ItemStack[] inv, ItemStack[] arm, float e, int l) {
            inventory = inv; armor = arm; exp = e; level = l;
        }
    }
}