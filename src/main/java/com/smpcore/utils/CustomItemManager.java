package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomItemManager {

    private final SMPCore plugin;
    public static final String DRILL_KEY = "drill_tool";
    public static final String FASTY_KEY = "fasty_tool";

    public CustomItemManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public ItemStack getDrill() {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        
        meta.setDisplayName("§6§lᴅʀɪʟʟ");

        List<String> lore = new ArrayList<>();
        lore.add("§7This powerful tool can");
        lore.add("§7break a §e3x3 area§7 at once.");
        lore.add(" ");
        lore.add("§6§lʟᴇɢᴇɴᴅᴀʀʏ ᴛᴏᴏʟ");
        meta.setLore(lore);

        
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
        meta.addEnchant(Enchantment.DIG_SPEED, 6, true); 

        
        NamespacedKey key = new NamespacedKey(plugin, DRILL_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getFasty() {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        
        meta.setDisplayName("§9§lғᴀsᴛʏ");

        List<String> lore = new ArrayList<>();
        lore.add("§7Breaks blocks §9instantly§7.");
        lore.add("§7Speed of light!");
        lore.add(" ");
        lore.add("§9§lᴍʏᴛʜɪᴄ ᴛᴏᴏʟ");
        meta.setLore(lore);

        
        meta.addEnchant(Enchantment.DIG_SPEED, 255, true);
        meta.addEnchant(Enchantment.DURABILITY, 10, true); 

        
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        
        NamespacedKey key = new NamespacedKey(plugin, FASTY_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getClaimShovel() {
        ItemStack item = new ItemStack(Material.GOLDEN_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lCLAIM SHOVEL");

        List<String> lore = new ArrayList<>();
        lore.add("§7Type: §eAnti-Grief Tool");
        lore.add(" ");
        lore.add("§9Left Click: §7Set Corner 1");
        lore.add("§9Right Click: §7Set Corner 2");
        lore.add("§9Shift + R-Click: §aConfirm & Claim");
        lore.add(" ");
        lore.add("§7Max Size: §c50x50x50");
        lore.add("§c§lCONSUMABLE (1 Use)");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}