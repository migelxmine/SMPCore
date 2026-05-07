package com.smpcore.listeners;

import com.smpcore.SMPCore;
import com.smpcore.utils.VaultManager;
import com.smpcore.utils.LotteryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class VaultListener implements Listener {

    private final SMPCore plugin;
    private final VaultManager vaultManager;
    private final LotteryManager lotteryManager; 

    public VaultListener(SMPCore plugin, VaultManager vaultManager, LotteryManager lotteryManager) {
        this.plugin = plugin;
        this.vaultManager = vaultManager;
        this.lotteryManager = lotteryManager;
    }

    @EventHandler
    public void onVaultClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§8Private Vault")) return;
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        int unlocked = vaultManager.getUnlockedCount(p.getUniqueId());

        
        if (slot >= 0 && slot < 27) {
            
            if (slot >= unlocked) {
                e.setCancelled(true);
                
                if (slot == unlocked) {
                    vaultManager.unlockNext(p, plugin.getEconomyManager());
                }
            }
        }

        
        if (e.isShiftClick() && e.getClickedInventory().equals(p.getInventory())) {
            e.setCancelled(true);
            p.sendMessage("§cPlease place items manually in the unlocked slots.");
        }
    }

    @EventHandler
    public void onVaultClose(InventoryCloseEvent e) {
        
        if (e.getView().getTitle().equals("§8Private Vault")) {
            Player p = (Player) e.getPlayer();

            
            vaultManager.saveContents(p, e.getInventory());
        }
    }
}