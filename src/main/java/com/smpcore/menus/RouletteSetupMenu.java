package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RouletteSetupMenu implements MigelSMPMenu {

    private Inventory inv;

    private RouletteSetupMenu() {}

    public static void open(Player p, CasinoManager casino) {
        RouletteSetupMenu menu = new RouletteSetupMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lʀᴏᴜʟᴇᴛᴛᴇ ʟᴏʙʙʏ");

        CasinoManager.MinesConfig cfg = casino.getConfig(p); 

        
        List<String> betLore = new ArrayList<>();
        betLore.add("§7Current Bet: §a$" + String.format("%,.0f", cfg.bet));
        betLore.add(" ");
        betLore.add("§eClick to Cycle:");
        betLore.add("§f1k ➡ 5k ➡ 10k ➡ 50k ➡ 100k");
        menu.inv.setItem(11, ItemBuilder.of(Material.GOLD_NUGGET, "§6§lBET AMOUNT", betLore));

        
        List<String> startLore = new ArrayList<>();
        startLore.add("§7Enter the Table.");
        startLore.add(" ");
        startLore.add("§7Bet: §f$" + String.format("%,.0f", cfg.bet));
        startLore.add(" ");
        startLore.add("§aClick to Play ➡");
        menu.inv.setItem(15, ItemBuilder.of(Material.MAGMA_CREAM, "§a§lSTART GAME", startLore));

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    public static void handleClick(Player p, int slot, CasinoManager casino) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        if (slot == 11) {
            Sounds.playClick(p);
            if (cfg.bet == 1000) cfg.bet = 5000;
            else if (cfg.bet == 5000) cfg.bet = 10000;
            else if (cfg.bet == 10000) cfg.bet = 50000;
            else if (cfg.bet == 50000) cfg.bet = 100000;
            else cfg.bet = 1000;
            open(p, casino); 
        }

        
        else if (slot == 15) {
            
            if (!casino.getEconomy().has(p, cfg.bet)) {
                p.sendMessage("§cNot enough money for this bet size!");
                Sounds.playError(p);
                return;
            }
            
            RouletteMenu.open(p, casino);
        }
    }

    @Override
    public Inventory getInventory() { return inv; }
}