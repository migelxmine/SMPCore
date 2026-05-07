package com.smpcore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SlotSymbol {

    
    C_TRASH(Material.POPPY, 0.0),      
    C_LOW(Material.APPLE, 2.0),        
    C_MED(Material.GOLD_INGOT, 5.0),   
    C_HIGH(Material.DIAMOND, 10.0),    
    C_JACKPOT(Material.EMERALD, 50.0), 

    
    N_TRASH(Material.ROTTEN_FLESH, 0.0),
    N_LOW(Material.QUARTZ, 3.0),
    N_MED(Material.BLAZE_POWDER, 8.0),
    N_HIGH(Material.GHAST_TEAR, 20.0),
    N_JACKPOT(Material.NETHER_STAR, 100.0),

    
    A_TRASH(Material.KELP, 0.0),
    A_LOW(Material.PRISMARINE_SHARD, 2.5),
    A_MED(Material.NAUTILUS_SHELL, 6.0),
    A_HIGH(Material.HEART_OF_THE_SEA, 15.0),
    A_JACKPOT(Material.TRIDENT, 75.0),

    
    M_TRASH(Material.COBBLESTONE, 0.0),
    M_LOW(Material.COAL, 2.0),
    M_MED(Material.IRON_INGOT, 4.0),
    M_HIGH(Material.DIAMOND, 10.0),
    M_JACKPOT(Material.NETHERITE_INGOT, 60.0),

    
    K_TRASH(Material.PAPER, 0.0),
    K_LOW(Material.COPPER_INGOT, 5.0),
    K_MED(Material.GOLD_BLOCK, 15.0),
    K_HIGH(Material.EMERALD_BLOCK, 30.0),
    K_JACKPOT(Material.BEACON, 200.0); 

    private final Material material;
    private final double multiplier;

    SlotSymbol(Material material, double multiplier) {
        this.material = material;
        this.multiplier = multiplier;
    }

    public ItemStack getItem() {
        if (multiplier > 0) {
            return ItemBuilder.of(material, "§6§l" + multiplier + "x Payout");
        }
        return ItemBuilder.of(material, "§7Try Again");
    }

    public double getMultiplier() {
        return multiplier;
    }
}