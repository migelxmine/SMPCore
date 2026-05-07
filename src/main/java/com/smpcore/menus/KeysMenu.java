package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.KeyType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KeysMenu implements InventoryHolder {

    private final Inventory inv;

    public KeysMenu(Player p, SMPCore plugin) {
        this.inv = Bukkit.createInventory(this, 27, "§8My Keys");

        int[] slots = {10, 11, 12, 13, 14, 16}; 
        int index = 0;

        for (KeyType type : KeyType.values()) {
            if (index >= slots.length) break;

            
            int amount = countKeys(p, type, plugin);

            ItemStack icon = new ItemStack(type.getMaterial());
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(type.getColor() + "§l" + type.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add("§7Rarity: " + type.getTier());
            lore.add(" ");
            lore.add("§7You have: §e" + amount);
            lore.add(" ");
            if (amount > 0) {
                lore.add("§aClick to open!");
            } else {
                lore.add("§cYou don't have this key.");
            }
            meta.setLore(lore);
            icon.setItemMeta(meta);

            
            inv.setItem(slots[index], icon);
            index++;
        }

        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i=0; i<27; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, glass);
        }
    }

    private int countKeys(Player p, KeyType type, SMPCore plugin) {
        int count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (plugin.getKeyManager().isKey(item)) {
                if (plugin.getKeyManager().getKeyType(item) == type) {
                    count += item.getAmount();
                }
            }
        }
        return count;
    }

    public static void open(Player p, SMPCore plugin) {
        p.openInventory(new KeysMenu(p, plugin).getInventory());
    }

    @Override
    public Inventory getInventory() { return inv; }
}