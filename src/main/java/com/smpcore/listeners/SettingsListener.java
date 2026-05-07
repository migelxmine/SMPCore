package com.smpcore.listeners;

import com.smpcore.SMPCore;
import com.smpcore.menus.SettingsMenu;
import com.smpcore.utils.SettingsManager;
import com.smpcore.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Iterator;

public class SettingsListener implements Listener {

    private final SMPCore plugin;
    private final SettingsManager settingsManager;

    public SettingsListener(SMPCore plugin) {
        this.plugin = plugin;
        this.settingsManager = plugin.getSettingsManager();
    }

    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Iterator<Player> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) {
            Player p = iterator.next();
            if (!settingsManager.getSetting(p, "public_chat")) {
                iterator.remove();
            }
        }

        if (!settingsManager.getSetting(e.getPlayer(), "public_chat")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou have public chat disabled.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        String msg = "§e" + e.getPlayer().getName() + " joined the game";
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (settingsManager.getSetting(p, "chat_server_msgs")) {
                p.sendMessage(msg);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        String msg = "§e" + e.getPlayer().getName() + " left the game";
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (settingsManager.getSetting(p, "chat_server_msgs")) {
                p.sendMessage(msg);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        if (msg.startsWith("/msg ") || msg.startsWith("/tell ") || msg.startsWith("/w ")) {
            String[] args = msg.split(" ");
            if (args.length > 1) {
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target != null && !settingsManager.getSetting(target, "private_messages")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§cThis player has disabled private messages.");
                }
            }
        }
    }

    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§8sᴇᴛᴛɪɴɢs")) return;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        
        if (clickedItem.getType() == Material.ARROW) {
            p.closeInventory();
            Sounds.playClick(p);
            return;
        }

        
        String key = getSettingKey(e.getSlot());
        if (key != null) {
            boolean newState = settingsManager.toggleSetting(p, key);
            Sounds.playClick(p);

            
            
            if (key.equals("scoreboard")) {
                if (newState) {
                    
                    
                    try {
                        Method createBoardMethod = plugin.getScoreboardHandler().getClass().getDeclaredMethod("createBoard", Player.class);
                        createBoardMethod.setAccessible(true);
                        createBoardMethod.invoke(plugin.getScoreboardHandler(), p);
                    } catch (Exception ex) {
                        
                        ex.printStackTrace();
                    }
                } else {
                    
                    p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }

            
            SettingsMenu.open(p, settingsManager);
        }
    }

    
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        
        if (e.getEntity() instanceof Monster && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {

            
            for (Player p : e.getLocation().getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(e.getLocation()) < 1600) {

                    
                    
                    if (settingsManager.getSetting(p, "disable_mob_spawns")) {
                        e.setCancelled(true);
                        return; 
                    }
                }
            }
        }
    }

    private String getSettingKey(int slot) {
        
        switch (slot) {
            case 0: return "public_chat";
            case 1: return "private_messages";
            case 2: return "chat_server_msgs";
            case 3: return "auction_alerts";
            case 9: return "totem_particles";
            case 10: return "explosion_particles";
            case 11: return "disable_mob_spawns"; 
            case 18: return "player_visibility";
            case 19: return "scoreboard";
            case 20: return "tpa_confirm_menu";
            case 21: return "sound_notifications";
            case 22: return "order_notification";
            case 27: return "tpa_requests";
            case 28: return "payments";
            case 29: return "quiet_spawn";
            default: return null;
        }
    }
}