package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DragonSetupMenu implements MigelSMPMenu {

    private Inventory inv;

    private DragonSetupMenu() {}

    public static void open(Player p, CasinoManager casino) {
        DragonSetupMenu menu = new DragonSetupMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lＤＲＡＧＯＮ ＴＯＷＥＲ");

        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        List<String> betLore = new ArrayList<>();
        betLore.add("§8Stake Amount");
        betLore.add(" ");
        betLore.add("§7Current Bet: §a$" + String.format("%,.0f", cfg.bet));
        betLore.add(" ");
        betLore.add("§eClick to Cycle");
        menu.inv.setItem(10, ItemBuilder.of(Material.GOLD_INGOT, "§6§lBET AMOUNT", betLore));

        

        
        List<String> easyLore = new ArrayList<>();
        easyLore.add("§8Safe Mode");
        easyLore.add(" ");
        easyLore.add("§7Columns: §f2 (50/50)");
        easyLore.add("§7Multiplier: §aNormal");
        easyLore.add(" ");
        easyLore.add("§aClick to Climb (Easy)");
        menu.inv.setItem(12, createGlowingItem(Material.EMERALD, "§a§lGOBLIN TOWER", easyLore));

        
        List<String> medLore = new ArrayList<>();
        medLore.add("§8Warrior Mode");
        medLore.add(" ");
        medLore.add("§7Columns: §f3 (1 in 3)");
        medLore.add("§7Multiplier: §6High");
        medLore.add(" ");
        medLore.add("§6Click to Climb (Medium)");
        menu.inv.setItem(14, createGlowingItem(Material.BLAZE_POWDER, "§6§lBLAZE TOWER", medLore));

        
        List<String> hardLore = new ArrayList<>();
        hardLore.add("§8God Mode");
        hardLore.add(" ");
        hardLore.add("§7Columns: §f4 (1 in 4)");
        hardLore.add("§7Multiplier: §c§lINSANE");
        hardLore.add(" ");
        hardLore.add("§cClick to Climb (Hard)");
        menu.inv.setItem(16, createGlowingItem(Material.END_CRYSTAL, "§5§lDRAGON TOWER", hardLore));

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);

        
        ItemStack purple = ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE, " ");
        menu.inv.setItem(0, purple); menu.inv.setItem(8, purple);
        menu.inv.setItem(18, purple); menu.inv.setItem(26, purple);

        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    
    private static ItemStack createGlowingItem(Material mat, String name, List<String> lore) {
        ItemStack item = ItemBuilder.of(mat, name, lore);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void handleClick(Player p, int slot, CasinoManager casino) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        if (slot == 10) { 
            Sounds.playClick(p);
            if (cfg.bet == 1000) cfg.bet = 5000;
            else if (cfg.bet == 5000) cfg.bet = 10000;
            else if (cfg.bet == 10000) cfg.bet = 50000;
            else if (cfg.bet == 50000) cfg.bet = 100000;
            else cfg.bet = 1000;
            open(p, casino);
        }
        else if (slot == 12) startGame(p, 0, casino); 
        else if (slot == 14) startGame(p, 1, casino); 
        else if (slot == 16) startGame(p, 2, casino); 
    }

    private static void startGame(Player p, int difficulty, CasinoManager casino) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        if (casino.getEconomy().has(p, cfg.bet)) {
            
            casino.getEconomy().withdraw(p, cfg.bet);

            
            casino.startDragonGame(p, difficulty);
            DragonGameMenu.open(p, casino);
        } else {
            p.sendMessage("§cInsufficient funds!");
            Sounds.playError(p);
        }
    }

    @Override
    public Inventory getInventory() { return inv; }
}