package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KeyManager {

    private final SMPCore plugin;
    
    private final NamespacedKey keyIdParams;
    private final NamespacedKey keyTierParams;
    private final NamespacedKey keyUuidParams; 

    public KeyManager(SMPCore plugin) {
        this.plugin = plugin;
        this.keyIdParams = new NamespacedKey(plugin, "smp_key_id");
        this.keyTierParams = new NamespacedKey(plugin, "smp_key_tier");
        this.keyUuidParams = new NamespacedKey(plugin, "smp_key_uuid");
    }

    
    public ItemStack getKeyItem(KeyType type, int amount) {
        ItemStack item = new ItemStack(type.getMaterial(), amount);
        ItemMeta meta = item.getItemMeta();

        
        String name = type.getColor() + "§l" + type.getDisplayName().toUpperCase();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add("§7Type: " + type.getColor() + type.name());
        lore.add("§7Rarity: " + getStars(type.getTier()));
        lore.add(" ");
        if (type == KeyType.BUSINESS) {
            lore.add("§a§l$$$ SPECIAL REWARD $$$");
            lore.add("§7Gives random money between");
            lore.add("§a$1,000 §7and §a$100,000");
        } else {
            lore.add("§eRight-click to open!");
            lore.add("§8(Visit /warp crates)");
        }
        lore.add(" ");
        lore.add("§8ID: " + UUID.randomUUID().toString().substring(0, 8)); 
        meta.setLore(lore);

        
        
        
        meta.getPersistentDataContainer().set(keyIdParams, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(keyTierParams, PersistentDataType.INTEGER, type.getTier());
        meta.getPersistentDataContainer().set(keyUuidParams, PersistentDataType.STRING, UUID.randomUUID().toString());

        item.setItemMeta(meta);
        return item;
    }

    
    public boolean isKey(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;

        
        return item.getItemMeta().getPersistentDataContainer().has(keyIdParams, PersistentDataType.STRING);
    }

    public KeyType getKeyType(ItemStack item) {
        if (!isKey(item)) return null;
        try {
            String typeName = item.getItemMeta().getPersistentDataContainer().get(keyIdParams, PersistentDataType.STRING);
            return KeyType.valueOf(typeName);
        } catch (Exception e) {
            return null;
        }
    }

    
    public void giveKey(Player p, KeyType type, int amount) {
        ItemStack key = getKeyItem(type, amount);
        if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(key);
            p.sendMessage("§aReceived §ex" + amount + " " + type.getDisplayName());
        } else {
            p.getWorld().dropItemNaturally(p.getLocation(), key);
            p.sendMessage("§eInventory full! Key dropped on ground.");
        }
    }

    
    
    public boolean takeKey(Player p, KeyType type, int amount) {
        int contentAmount = 0;
        for (ItemStack is : p.getInventory().getContents()) {
            if (isKey(is) && getKeyType(is) == type) {
                contentAmount += is.getAmount();
            }
        }

        if (contentAmount >= amount) {
            
            int leftToRemove = amount;
            for (ItemStack is : p.getInventory().getContents()) {
                if (leftToRemove <= 0) break;
                if (isKey(is) && getKeyType(is) == type) {
                    if (is.getAmount() <= leftToRemove) {
                        leftToRemove -= is.getAmount();
                        p.getInventory().removeItem(is);
                    } else {
                        is.setAmount(is.getAmount() - leftToRemove);
                        leftToRemove = 0;
                    }
                }
            }
            p.updateInventory();
            return true;
        }
        return false;
    }

    
    private String getStars(int count) {
        StringBuilder sb = new StringBuilder("§e");
        for (int i=0; i<count; i++) sb.append("★");
        return sb.toString();
    }

    
    public NamespacedKey getKeyIdParams() { return keyIdParams; }
}