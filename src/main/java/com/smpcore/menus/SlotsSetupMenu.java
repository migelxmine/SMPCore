package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack; 

import java.util.ArrayList;
import java.util.List;

public class SlotsSetupMenu implements MigelSMPMenu {

    private Inventory inv;

    private SlotsSetupMenu() {}

    public static void open(Player p, CasinoManager casino) {
        SlotsSetupMenu menu = new SlotsSetupMenu();
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lＳＬＯＴＳ ＬＯＢＢＹ");

        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        menu.inv.setItem(11, createMachineIcon(Material.EMERALD_BLOCK, "§a§lCLASSIC", "§7Low Volatility", "§eMax Win: 50x"));
        menu.inv.setItem(12, createMachineIcon(Material.MAGMA_BLOCK, "§c§lNETHER", "§7Medium Volatility", "§eMax Win: 100x"));
        menu.inv.setItem(13, createMachineIcon(Material.PRISMARINE, "§9§lAQUATIC", "§7Balanced", "§eMax Win: 75x"));
        menu.inv.setItem(14, createMachineIcon(Material.IRON_ORE, "§7§lMINING", "§7Consistent", "§eMax Win: 60x"));
        menu.inv.setItem(15, createMachineIcon(Material.BEACON, "§d§lCRYPTO", "§c§lHIGH RISK", "§eMax Win: 200x"));

        
        List<String> betLore = new ArrayList<>();
        betLore.add("§7Current Bet: §a$" + String.format("%,.0f", cfg.bet));
        betLore.add(" ");
        betLore.add("§eClick to Cycle:");
        betLore.add("§f1k ➡ 5k ➡ 10k ➡ 50k ➡ 100k");
        menu.inv.setItem(31, ItemBuilder.of(Material.GOLD_NUGGET, "§6§lBET AMOUNT", betLore));

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    private static ItemStack createMachineIcon(Material mat, String name, String type, String win) {
        List<String> lore = new ArrayList<>();
        lore.add(type);
        lore.add(win);
        lore.add(" ");
        lore.add("§eClick to Play ➡");
        return ItemBuilder.of(mat, name, lore);
    }

    public static void handleClick(Player p, int slot, String name, CasinoManager casino) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        if (slot == 31) {
            Sounds.playClick(p);
            if (cfg.bet == 1000) cfg.bet = 5000;
            else if (cfg.bet == 5000) cfg.bet = 10000;
            else if (cfg.bet == 10000) cfg.bet = 50000;
            else if (cfg.bet == 50000) cfg.bet = 100000;
            else cfg.bet = 1000;
            open(p, casino); 
        }

        
        else if (slot >= 11 && slot <= 15) {
            String theme = "CLASSIC"; 

            
            if (name.contains("NETHER")) theme = "NETHER";
            else if (name.contains("AQUATIC")) theme = "AQUATIC";
            else if (name.contains("MINING")) theme = "MINING";
            else if (name.contains("CRYPTO")) theme = "CRYPTO";

            
            SlotGameMenu.startGame(p, theme, casino);
        }
    }

    @Override
    public Inventory getInventory() { return inv; }
}