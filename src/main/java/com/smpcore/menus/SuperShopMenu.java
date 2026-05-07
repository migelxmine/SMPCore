package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.SuperMoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperShopMenu implements Listener {

    private final SMPCore plugin;
    private static final String GUI_TITLE = "§d§lɢᴏᴅ ᴛᴏᴏʟs §7(SM Shop)";

    public SuperShopMenu(SMPCore plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin); 
    }

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        
        List<String> drillLore = Arrays.asList(
                "§7Mines a 3x3 area.",
                "§7Best for strip mining.",
                " ",
                "§7Price: §d50 SM",
                " ",
                "§eClick to Buy ➡"
        );
        inv.setItem(11, createItem(Material.NETHERITE_PICKAXE, "§6§lᴅʀɪʟʟ 3x3", drillLore, true));

        
        List<String> fastyLore = Arrays.asList(
                "§7Gives Speed III for 30s.",
                "§7Gotta go fast!",
                " ",
                "§7Price: §d100 SM",
                " ",
                "§eClick to Buy ➡"
        );
        inv.setItem(13, createItem(Material.SUGAR, "§9§lғᴀsᴛʏ", fastyLore, true));

        
        List<String> shovelLore = Arrays.asList(
                "§7Protect your base.",
                "§7Creates a §650x50x50 §7claim.",
                " ",
                "§7Usage:",
                "§71. L-Click Pos1, R-Click Pos2",
                "§72. Shift+R-Click to Claim",
                " ",
                "§7Price: §d1,000 SM",
                " ",
                "§eClick to Buy ➡"
        );
        inv.setItem(15, createItem(Material.GOLDEN_SHOVEL, "§e§lCLAIM SHOVEL", shovelLore, true));

        
        ItemStack border = createItem(Material.PURPLE_STAINED_GLASS_PANE, " ", null, false);
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }

        p.openInventory(inv);
    }

    
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getType() == Material.PURPLE_STAINED_GLASS_PANE) return;

        Player p = (Player) e.getWhoClicked();
        SuperMoneyManager sm = plugin.getSuperMoneyManager();
        int slot = e.getSlot();
        double price = 0;
        ItemStack itemToGive = null;

        
        if (slot == 11) { 
            price = 50.0;
            itemToGive = new ItemStack(Material.NETHERITE_PICKAXE);
            
        } else if (slot == 13) { 
            price = 100.0;
            itemToGive = new ItemStack(Material.SUGAR);
        } else if (slot == 15) { 
            price = 1000.0;
            itemToGive = new ItemStack(Material.GOLDEN_SHOVEL);
        }

        if (itemToGive != null) {
            if (sm.getBalance(p) >= price) {
                if (p.getInventory().firstEmpty() == -1) {
                    p.sendMessage("§cInventory full!");
                    return;
                }

                sm.withdraw(p, price);

                
                ItemMeta meta = itemToGive.getItemMeta();
                if (slot == 11) meta.setDisplayName("§6§lDrill 3x3");
                if (slot == 13) meta.setDisplayName("§9§lFasty");
                if (slot == 15) meta.setDisplayName("§e§lClaim Shovel");
                itemToGive.setItemMeta(meta);

                p.getInventory().addItem(itemToGive);
                p.sendMessage("§aPurchase successful! §7(-" + price + " SM)");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            } else {
                p.sendMessage("§cInsufficient Super Money! You need §d" + price + " SM");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
        }
    }

    
    private static ItemStack createItem(Material mat, String name, List<String> lore, boolean glow) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }
}