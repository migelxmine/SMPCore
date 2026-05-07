package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.PurityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JewelerMenu implements InventoryHolder {

    private final Inventory inv;
    private final Random random = new Random();

    
    public static final int INPUT_SLOT = 20;
    public static final int BUTTON_SLOT = 22;
    public static final int OUTPUT_SLOT = 24;

    public JewelerMenu() {
        this.inv = Bukkit.createInventory(this, 45, "§8The Jeweler's Bench");
        init();
    }

    private void init() {
        
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, glass);
        }

        
        inv.setItem(INPUT_SLOT, null);  
        inv.setItem(OUTPUT_SLOT, null); 

        
        updateButton(false, 0);

        
        inv.setItem(40, createItem(Material.BOOK, "§e§lHow it works",
                "§7Put an ore in the left slot.",
                "§7Pay the evaluation fee.",
                "§7Get a certified ore with",
                "§7random purity (0.5% - 100%).",
                " ",
                "§c§lWARNING:",
                "§cLower purity ores are worth less!",
                "§cNo refunds."));
    }

    
    public void updateButton(boolean active, double cost) {
        if (active) {
            ItemStack anvil = new ItemStack(Material.ANVIL);
            ItemMeta meta = anvil.getItemMeta();
            meta.setDisplayName("§a§lEVALUATE ORE");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to appraise this item.");
            lore.add(" ");
            lore.add("§7Cost: §c$" + String.format("%,.0f", cost));
            lore.add(" ");
            lore.add("§eClick to Confirm!");
            meta.setLore(lore);
            anvil.setItemMeta(meta);
            inv.setItem(BUTTON_SLOT, anvil);
        } else {
            ItemStack barrier = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName("§c§lWAITING FOR ORE...");
            List<String> lore = new ArrayList<>();
            lore.add("§7Place a valid ore in the");
            lore.add("§7left slot to begin.");
            meta.setLore(lore);
            barrier.setItemMeta(meta);
            inv.setItem(BUTTON_SLOT, barrier);
        }
    }

    
    public double calculatePurity() {
        
        double chance = random.nextDouble(); 

        if (chance < 0.50) { 
            return 0.5 + (random.nextDouble() * 39.5);
        } else if (chance < 0.80) { 
            return 40.0 + (random.nextDouble() * 30.0);
        } else if (chance < 0.95) { 
            return 70.0 + (random.nextDouble() * 20.0);
        } else { 
            return 90.0 + (random.nextDouble() * 10.0);
        }
    }

    
    public static double getEvaluationCost(Material m) {
        switch (m) {
            case COAL_ORE: return 50.0;
            case IRON_ORE: return 100.0;
            case GOLD_ORE: return 250.0;
            case DIAMOND_ORE: return 500.0;
            case EMERALD_ORE: return 1000.0;
            case ANCIENT_DEBRIS: return 2000.0;
            default: return 0.0;
        }
    }

    public static boolean isValidOre(Material m) {
        return getEvaluationCost(m) > 0;
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(s);
        meta.setLore(l);
        item.setItemMeta(meta);
        return item;
    }

    public static void open(Player p) {
        p.openInventory(new JewelerMenu().getInventory());
    }

    @Override
    public Inventory getInventory() { return inv; }
}