package com.smpcore.listeners;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class GlitchProtectionListener implements Listener {

    private final SMPCore plugin;

    public GlitchProtectionListener(SMPCore plugin) {
        this.plugin = plugin;
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();

        
        scanAndPunish(p);

        
        ItemStack cursor = p.getItemOnCursor();
        if (isMenuItem(cursor)) {
            p.setItemOnCursor(null); 
            kickPlayer(p);
        }
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        scanAndPunish(e.getPlayer());
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (isMenuItem(e.getItemDrop().getItemStack())) {
            e.getItemDrop().remove(); 
            scanAndPunish(e.getPlayer()); 
        }
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (isMenuItem(e.getOffHandItem()) || isMenuItem(e.getMainHandItem())) {
            e.setCancelled(true);
            scanAndPunish(e.getPlayer());
        }
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        
        e.getDrops().removeIf(this::isMenuItem);
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (isMenuItem(e.getCurrentItem()) || isMenuItem(e.getCursor())) {
            
            if (!(e.getInventory().getHolder() instanceof com.smpcore.menus.MigelSMPMenu)) {
                e.setCancelled(true);
                if (e.getWhoClicked() instanceof Player) {
                    
                    e.setCurrentItem(null);
                    e.setCursor(null);
                    ((Player) e.getWhoClicked()).sendMessage("§cItem confiscated.");
                }
            }
        }
    }

    
    private void scanAndPunish(Player p) {
        boolean foundIllegal = false;

        
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (isMenuItem(item)) {
                p.getInventory().setItem(i, null);
                foundIllegal = true;
            }
        }

        if (foundIllegal) {
            kickPlayer(p);
        }
    }

    private void kickPlayer(Player p) {
        plugin.getLogger().warning("[Security] " + p.getName() + " had illegal GUI items. Kicking...");

        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            p.kickPlayer("§c§lSMP SECURITY\n\n§cIllegal Menu Item Detected.\n§7(Please do not attempt to take GUI items)");
        });
    }

    
    private boolean isMenuItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;

        String name = item.getItemMeta().getDisplayName();

        
        
        
        
        if (!name.contains("§")) {
            return false;
        }

        String stripped = ChatColor.stripColor(name);

        
        
        if ((item.getType() == Material.BLACK_STAINED_GLASS_PANE ||
                item.getType() == Material.GRAY_STAINED_GLASS_PANE ||
                item.getType() == Material.PURPLE_STAINED_GLASS_PANE ||
                item.getType() == Material.LIME_STAINED_GLASS_PANE ||
                item.getType() == Material.RED_STAINED_GLASS_PANE) &&
                (stripped.trim().isEmpty())) {
            return true;
        }

        
        if (name.contains("ᴘᴜʙʟɪᴄ") || name.contains("ᴄʜᴀᴛ") ||
                name.contains("ᴀᴜᴄᴛɪᴏɴ ᴀʟᴇʀᴛs") || name.contains("ᴛᴏᴛᴇᴍ ᴘᴀʀᴛɪᴄʟᴇs") ||
                name.contains("sᴄᴏʀᴇʙᴏᴀʀᴅ") || name.contains("ᴘʟᴀʏᴇʀ ᴠɪsɪʙɪʟɪᴛʏ")) {
            return true;
        }

        
        if (name.contains("Confirm") || name.contains("Cancel") ||
                name.contains("Back") || name.contains("Previous") ||
                name.contains("Next") || name.contains("ʙᴀᴄᴋ") ||
                name.contains("ᴄᴏɴғɪʀᴍ")) {
            return true;
        }

        return false;
    }
}