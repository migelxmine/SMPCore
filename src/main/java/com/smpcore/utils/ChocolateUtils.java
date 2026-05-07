package com.smpcore.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChocolateUtils {

    
    public static ItemStack getChocolate() {
        ItemStack item = new ItemStack(Material.BROWN_GLAZED_TERRACOTTA);
        ItemMeta meta = item.getItemMeta();
        
        String rainbowName = "§c§lC§6§lH§e§lO§a§lC§9§lO§3§lL§9§lA§d§lT§5§lE §c§lB§6§lL§e§lO§a§lC§9§lK";
        meta.setDisplayName(rainbowName);
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isChocolate(ItemStack item) {
        if (item == null || item.getType() != Material.BROWN_GLAZED_TERRACOTTA) return false;
        if (!item.hasItemMeta()) return false;
        
        return item.getItemMeta().getDisplayName().contains("§c§lC§6§lH§e§lO");
    }

    
    public static ItemStack getDirtyChocolate() {
        ItemStack item = new ItemStack(Material.BROWN_GLAZED_TERRACOTTA);
        ItemMeta meta = item.getItemMeta();

        
        meta.setDisplayName("§4§lDIRTY CHOCOLATE BLOCK");

        
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isDirtyChocolate(ItemStack item) {
        if (item == null || item.getType() != Material.BROWN_GLAZED_TERRACOTTA) return false;
        if (!item.hasItemMeta()) return false;
        
        return item.getItemMeta().getDisplayName().equals("§4§lDIRTY CHOCOLATE BLOCK");
    }
}