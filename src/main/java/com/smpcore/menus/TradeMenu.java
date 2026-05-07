package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeMenu implements MigelSMPMenu {

    private Inventory inv;

    private TradeMenu() {}

    public static void open(Player p, CryptoManager crypto, String coinKey) {
        TradeMenu menu = new TradeMenu();
        String coinName = crypto.getCoinDisplayName(coinKey).toUpperCase();

        
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lᴛʀᴀᴅɪɴɢ: " + coinName);

        double price = crypto.getPrice(coinKey);
        double balance = crypto.getPlayerCoinBalance(p, coinKey);

        
        ItemStack greenGlass = ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, " ");
        ItemStack redGlass = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, " ");
        ItemStack blackGlass = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 45; i++) {
            int col = i % 9;
            if (col < 4) menu.inv.setItem(i, greenGlass);      
            else if (col > 4) menu.inv.setItem(i, redGlass);   
            else menu.inv.setItem(i, blackGlass);              
        }

        
        List<String> infoLore = new ArrayList<>();
        infoLore.add(" ");
        infoLore.add("§7Current Price: §6$" + String.format("%,.2f", price));
        infoLore.add("§7You Own: §e" + String.format("%.4f", balance));

        menu.inv.setItem(4, ItemBuilder.of(Material.PAPER, "§e§l" + coinName, infoLore));

        
        menu.inv.setItem(10, createTradeButton(Material.LIME_CONCRETE, "§a§lʙᴜʏ 1", price * 1, 1));
        menu.inv.setItem(19, createTradeButton(Material.LIME_CONCRETE, "§a§lʙᴜʏ 10", price * 10, 10));
        menu.inv.setItem(28, createTradeButton(Material.LIME_CONCRETE, "§a§lʙᴜʏ 64", price * 64, 64));

        
        menu.inv.setItem(16, createTradeButton(Material.RED_CONCRETE, "§c§lsᴇʟʟ 1", price * 1, 1));
        menu.inv.setItem(25, createTradeButton(Material.RED_CONCRETE, "§c§lsᴇʟʟ 10", price * 10, 10));
        menu.inv.setItem(34, createTradeButton(Material.RED_CONCRETE, "§c§lsᴇʟʟ 64", price * 64, 64));

        
        MenuUtils.addBackButton(menu.inv);

        

        p.openInventory(menu.inv);
    }

    private static ItemStack createTradeButton(Material mat, String title, double cost, int amount) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7Amount: §f" + amount);
        lore.add("§7Total Value: §6$" + String.format("%,.2f", cost));
        lore.add(" ");
        if (title.contains("ʙᴜʏ")) {
            lore.add("§eClick to Purchase");
        } else {
            lore.add("§eClick to Sell");
        }
        return ItemBuilder.of(mat, title, lore);
    }

    @Override
    public Inventory getInventory() { return inv; }
}