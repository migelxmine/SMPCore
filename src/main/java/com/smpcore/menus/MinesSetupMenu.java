package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType; 
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class MinesSetupMenu implements MigelSMPMenu {

    private Inventory inv;

    private MinesSetupMenu() {}

    public static void open(Player p, CasinoManager casino) {
        MinesSetupMenu menu = new MinesSetupMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lＭＩＮＥＳ ＬＯＢＢＹ");

        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        List<String> betLore = new ArrayList<>();
        betLore.add("§7Current Bet: §a$" + String.format("%,.0f", cfg.bet));
        betLore.add(" ");
        betLore.add("§eClick to Cycle");
        menu.inv.setItem(11, ItemBuilder.of(Material.GOLD_NUGGET, "§6§lBET AMOUNT", betLore));

        
        List<String> bombLore = new ArrayList<>();
        bombLore.add("§7Bombs: §c" + cfg.mines);
        bombLore.add(" ");
        
        bombLore.add("§aLeft-Click: §7Add (+)");
        bombLore.add("§cRight-Click: §7Remove (-)");
        menu.inv.setItem(13, ItemBuilder.of(Material.TNT, "§c§lBOMB COUNT", bombLore));

        
        List<String> startLore = new ArrayList<>();
        startLore.add("§7Start the game.");
        startLore.add("§7Grid Size: 20 Slots");
        startLore.add(" ");
        startLore.add("§aClick to Play ➡");
        menu.inv.setItem(15, ItemBuilder.of(Material.LIME_DYE, "§a§lSTART GAME", startLore));

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    
    public static void handleClick(Player p, int slot, ClickType click, CasinoManager casino) {
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
        else if (slot == 13) { 
            Sounds.playClick(p);

            if (click.isLeftClick()) {
                
                cfg.mines++;
                if (cfg.mines > 19) cfg.mines = 1; 
            }
            else if (click.isRightClick()) {
                
                cfg.mines--;
                if (cfg.mines < 1) cfg.mines = 19; 
            }

            open(p, casino);
        }
        else if (slot == 15) { 
            if (casino.getEconomy().has(p, cfg.bet)) {
                casino.getEconomy().withdraw(p, cfg.bet);
                casino.startMines(p);
                MinesMenu.open(p, casino);
            } else {
                p.sendMessage("§cInsufficient funds!");
                Sounds.playError(p);
            }
        }
    }

    @Override
    public Inventory getInventory() { return inv; }
}