package com.smpcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

public class SellManager {

    private final PriceManager priceManager;
    private final EconomyManager economyManager;

    private final Set<UUID> intentionalCloses = new HashSet<>();

    public SellManager(PriceManager priceManager, EconomyManager economyManager) {
        this.priceManager = priceManager;
        this.economyManager = economyManager;
    }

    public void addIntentionalClose(UUID uuid) {
        intentionalCloses.add(uuid);
    }

    public boolean isIntentionalClose(UUID uuid) {
        return intentionalCloses.contains(uuid);
    }

    public void removeIntentionalClose(UUID uuid) {
        intentionalCloses.remove(uuid);
    }

    public void processSell(Player p, Inventory inv) {
        double totalValue = 0.0;
        int itemsSold = 0;
        Map<Material, Integer> soldCounts = new HashMap<>();

        
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                if (priceManager.hasPrice(item.getType())) {
                    double pricePerItem = priceManager.getSellPrice(item.getType());
                    totalValue += pricePerItem * item.getAmount();
                    itemsSold += item.getAmount();

                    
                    soldCounts.put(item.getType(), soldCounts.getOrDefault(item.getType(), 0) + item.getAmount());

                    inv.setItem(i, null);
                } else {
                    p.getInventory().addItem(item);
                }
            }
        }

        
        if (itemsSold > 0) {
            economyManager.deposit(p, totalValue);

            
            for (Map.Entry<Material, Integer> entry : soldCounts.entrySet()) {
                
                priceManager.adjustPrice(entry.getKey(), entry.getValue(), false);
            }

            p.sendMessage(ChatColor.GREEN + "Sold " + itemsSold + " items for $" + String.format("%,.2f", totalValue));
            Sounds.playSuccess(p);
        } else {
            p.sendMessage(ChatColor.RED + "No sellable items found.");
            Sounds.playError(p);
        }

        p.closeInventory();
    }
}