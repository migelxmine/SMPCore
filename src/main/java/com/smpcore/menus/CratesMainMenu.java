package com.smpcore.menus;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CratesMainMenu implements InventoryHolder {

    private final Inventory inv;

    public CratesMainMenu(SMPCore plugin) {
        
        String title = "§6§l★ CRATES & KEYS ★";
        this.inv = Bukkit.createInventory(this, 27, title);

        
        ItemStack purple = createDecor(Material.PURPLE_STAINED_GLASS_PANE);
        ItemStack black = createDecor(Material.BLACK_STAINED_GLASS_PANE);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, black);
        }
        
        int[] purpleSlots = {0, 8, 9, 17, 18, 26};
        for (int i : purpleSlots) inv.setItem(i, purple);

        
        
        ItemStack trade = createButton(Material.GOLD_BLOCK,
                "§6§lT§e§lR§6§lA§e§lD§6§lE",
                "§7Exchange Chocolate for Keys",
                "§eClick to open Shop");
        inv.setItem(11, trade);

        
        
        ItemStack keys = createButton(Material.TRIPWIRE_HOOK,
                "§a§lM§2§lY §a§lK§2§lE§a§lY§2§lS",
                "§7View and open your Keys",
                "§aClick to play Roulette");
        inv.setItem(13, keys);

        
        
        ItemStack missions = createButton(Material.BOOK,
                "§9§lM§3§lI§9§lS§3§lS§9§lI§3§lO§9§lN§3§lS",
                "§7Complete tasks to earn",
                "§f§lCHOCOLATE BLOCKS", 
                "§c(Coming Soon...)");
        inv.setItem(15, missions);
    }

    
    private ItemStack createButton(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(" "); 
        for (String line : loreLines) {
            lore.add(line);
        }
        lore.add(" ");

        
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); 

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDecor(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static void open(Player p, SMPCore plugin) {
        p.openInventory(new CratesMainMenu(plugin).getInventory());
    }

    @Override
    public Inventory getInventory() { return inv; }
}