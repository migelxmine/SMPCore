package com.smpcore.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class KeyRewards {

    private static final Random random = new Random();

    
    public static ItemStack getRandomReward(KeyType type) {
        ItemStack item;

        switch (type) {
            case NORMAL: 
                if (chance(50)) item = new ItemStack(Material.IRON_INGOT, 32);
                else if (chance(30)) item = new ItemStack(Material.COOKED_BEEF, 16);
                else item = new ItemStack(Material.IRON_SWORD);
                break;

            case RARE: 
                if (chance(50)) item = new ItemStack(Material.DIAMOND, 5);
                else if (chance(30)) item = new ItemStack(Material.GOLDEN_APPLE, 2);
                else item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                break;

            case LEGENDARY: 
                if (chance(50)) item = new ItemStack(Material.NETHERITE_SCRAP, 4);
                else if (chance(30)) item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
                else item = new ItemStack(Material.ELYTRA);
                break;

            case XTREME: 
                if (chance(40)) item = new ItemStack(Material.NETHERITE_INGOT, 2);
                else if (chance(40)) item = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
                else item = new ItemStack(Material.BEACON, 1);
                break;

            case PRIME: 
                if (chance(40)) item = new ItemStack(Material.NETHERITE_BLOCK, 1); 
                else if (chance(30)) item = new ItemStack(Material.TRIDENT, 1);
                else if (chance(20)) item = new ItemStack(Material.END_CRYSTAL, 10);
                else item = new ItemStack(Material.SHULKER_BOX);
                break;

            case BUSINESS: 
                item = new ItemStack(Material.PAPER);
                ItemMeta m = item.getItemMeta();
                m.setDisplayName("§aMoney Check");
                item.setItemMeta(m);
                break;

            default: 
                item = new ItemStack(Material.COAL, 10);
                break;
        }

        
        return addGlow(item);
    }

    
    public static ItemStack addGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            
            meta.addEnchant(Enchantment.LUCK, 1, true);
            
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static boolean chance(int percent) {
        return random.nextInt(100) < percent;
    }
}