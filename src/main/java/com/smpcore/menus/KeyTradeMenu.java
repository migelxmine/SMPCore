package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.ChocolateUtils;
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

public class KeyTradeMenu implements InventoryHolder {

    private final Inventory inv;

    public KeyTradeMenu(SMPCore plugin) {
        this.inv = Bukkit.createInventory(this, 27, "§8Trade: Chocolate -> Keys");
        inv.setItem(4, ChocolateUtils.getChocolate());

        
        setupTradeItem(10, KeyType.NORMAL, 10);
        setupTradeItem(11, KeyType.RARE, 25);
        setupTradeItem(12, KeyType.LEGENDARY, 50);
        setupTradeItem(14, KeyType.XTREME, 100);
        setupTradeItem(16, KeyType.PRIME, 250);

        
        ItemStack glass = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, glass);
        }
    }

    private void setupTradeItem(int slot, KeyType type, int cost) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(type.getColor() + "§l" + type.getDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add("§7Tier: " + type.getTier());
        lore.add(" ");
        lore.add("§7Cost: §f" + cost + "x " + ChocolateUtils.getChocolate().getItemMeta().getDisplayName());
        lore.add(" ");
        lore.add("§eClick to Trade!");
        meta.setLore(lore);
        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    public static void open(Player p, SMPCore plugin) {
        p.openInventory(new KeyTradeMenu(plugin).getInventory());
    }

    @Override
    public Inventory getInventory() { return inv; }
}