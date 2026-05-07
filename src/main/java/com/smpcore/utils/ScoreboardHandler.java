package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

    private final SMPCore plugin;
    private final EconomyManager eco;

    private final CryptoManager crypto; 
    private final TprManager tpr;
    private final SettingsManager settings;
    private final LangManager lang;
    private final SuperMoneyManager sm;

    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    public ScoreboardHandler(SMPCore plugin, EconomyManager eco, CryptoManager crypto, TprManager tpr, SettingsManager settings, LangManager lang, SuperMoneyManager sm) {
        this.plugin = plugin;
        this.eco = eco;
        this.crypto = crypto;
        this.tpr = tpr;
        this.settings = settings;
        this.lang = lang;
        this.sm = sm;
        startUpdater();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        createBoard(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        boards.remove(e.getPlayer().getUniqueId());
    }

    private void createBoard(Player p) {
        
        if (!settings.getSetting(p, "scoreboard")) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();



        String title = plugin.getConfig().getString("scoreboard-title", "          §f§lSMP Core");
        Objective obj = board.registerNewObjective("smpcore", "dummy", title);

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        p.setScoreboard(board);
        boards.put(p.getUniqueId(), board);

        updateBoard(p);
    }

    private void updateBoard(Player p) {
        
        
        if (!settings.getSetting(p, "scoreboard")) {
            if (p.getScoreboard().getObjective("smpcore") != null) {
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); 
            }
            return;
        }

        
        if (!boards.containsKey(p.getUniqueId())) {
            createBoard(p); 
            return;
        }

        Scoreboard board = boards.get(p.getUniqueId());
        Objective obj = board.getObjective("smpcore");

        if (obj == null) {
            
            String title = "  " + lang.getMessage("plugin-name") + "  ";
            obj = board.registerNewObjective("smpcore", "dummy", title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        
        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        
        double balance = eco.getBalance(p);
        double superMoney = sm.getBalance(p);
        int kills = p.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = p.getStatistic(Statistic.DEATHS);
        String ping = p.getPing() + "ms";

        
        int line = 15;

        setScore(obj, "§r", line--);

        
        setScore(obj, "§9§l PLAYER", line--);
        setScore(obj, "  §f" + p.getName(), line--);
        setScore(obj, "  §7Ping: " + ping, line--);
        setScore(obj, "§r§r", line--);

        
        setScore(obj, "§a§l ECONOMY", line--);
        setScore(obj, "  §f$" + formatValue(balance), line--);
        setScore(obj, "  §d" + formatValue(superMoney) + " SM", line--);
        setScore(obj, "§r§r§r", line--);

        
        setScore(obj, "§c§l PVP STATS", line--);
        setScore(obj, "  §fKills: " + kills, line--);
        setScore(obj, "  §fDeaths: " + deaths, line--);

        setScore(obj, "§r§r§r§r", line--);

        
        setScore(obj, "§8      /rtp to get started!      ", line--);
    }

    private String formatValue(double value) {
        if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000);
        if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
        if (value >= 1_000) return String.format("%.1fk", value / 1_000);
        return String.format("%.0f", value);
    }

    private void setScore(Objective obj, String text, int score) {
        if (text.length() > 40) text = text.substring(0, 40);
        Score s = obj.getScore(text);
        s.setScore(score);
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    updateBoard(p);
                }
            }
        }.runTaskTimer(plugin, 20L, 60L); 
    }
}