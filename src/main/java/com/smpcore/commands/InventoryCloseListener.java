package com.smpcore.commands;

import com.smpcore.utils.SellManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryCloseListener implements Listener {

    private final SellManager sellManager;

    public InventoryCloseListener(SellManager sellManager) {
        this.sellManager = sellManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        String strippedTitle = ChatColor.stripColor(title);

        
        if (strippedTitle.equals("Sell Items")) {
            if (sellManager.isIntentionalClose(player.getUniqueId())) {
                sellManager.removeIntentionalClose(player.getUniqueId());
                return;
            }

            boolean returnedItems = false;
            for (int i = 0; i < 36; i++) {
                ItemStack item = event.getInventory().getItem(i);
                if (item != null) {
                    player.getInventory().addItem(item);
                    returnedItems = true;
                }
            }
            if (returnedItems) {
                player.sendMessage(ChatColor.YELLOW + "Sale cancelled. Your items have been returned.");
            }
        }

        
        
        else if (title.contains("The Jeweler's Bench")) {
            returnItem(player, event.getInventory().getItem(20)); 
            returnItem(player, event.getInventory().getItem(24)); 
        }
    }

    
    private void returnItem(Player p, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(item);
            } else {
                
                p.getWorld().dropItem(p.getLocation(), item);
                p.sendMessage("§cInventory full! Items dropped on the ground.");
            }
        }
    }
}