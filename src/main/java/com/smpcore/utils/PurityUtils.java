package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PurityUtils {

    private static SMPCore plugin;

    public static void init(SMPCore pl) {
        plugin = pl;
    }

    public static ItemStack setPurity(ItemStack item, double purity) {
        
        
        if (item == null || item.getType().isAir()) return item;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item; 

        NamespacedKey key = new NamespacedKey(plugin, "smp_purity");

        
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, purity);

        
        String grade = getGrade(purity);
        meta.setDisplayName("§a" + formatName(item.getType()) + " §7(§e" + String.format("%.1f", purity) + "%§7)");

        List<String> lore = new ArrayList<>();
        lore.add("§7Grade: " + grade);
        lore.add("§7Certified by the Jeweler");
        lore.add(" ");
        lore.add("§8This item is evaluated.");
        meta.setLore(lore);

        
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public static double getPurity(ItemStack item) {
        
        if (item == null || item.getType().isAir()) return -1.0;
        if (!item.hasItemMeta()) return -1.0; 

        NamespacedKey key = new NamespacedKey(plugin, "smp_purity");
        if (!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.DOUBLE)) {
            return -1.0;
        }

        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
    }

    private static String getGrade(double purity) {
        if (purity >= 90.0) return "§6§lLEGENDARY";
        if (purity >= 70.0) return "§9RARE";
        if (purity >= 40.0) return "§eUNCOMMON";
        return "§7COMMON";
    }

    private static String formatName(org.bukkit.Material m) {
        String[] parts = m.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : parts) sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        return sb.toString().trim();
    }
}