package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteMenu implements MigelSMPMenu {

    private Inventory inv;

    private RouletteMenu() {}

    
    public static void open(Player p, CasinoManager casino) {
        RouletteMenu menu = new RouletteMenu();
        CasinoManager.MinesConfig cfg = casino.getConfig(p);
        double bet = cfg.bet;

        menu.inv = Bukkit.createInventory(menu, 27, "§8§lʀᴏᴜʟᴇᴛᴛᴇ: $" + String.format("%,.0f", bet));

        
        menu.inv.setItem(11, createBetButton(Material.RED_WOOL, "§c§lRED", "2x", bet, bet * 2));
        menu.inv.setItem(13, createBetButton(Material.LIME_WOOL, "§a§lGREEN", "14x (Jackpot)", bet, bet * 14));
        menu.inv.setItem(15, createBetButton(Material.BLACK_WOOL, "§8§lBLACK", "2x", bet, bet * 2));

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv); 

        p.openInventory(menu.inv);
    }

    private static ItemStack createBetButton(Material mat, String name, String mult, double cost, double win) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Payout: " + mult);
        lore.add(" ");
        lore.add("§7Bet: §c$" + String.format("%,.0f", cost));
        lore.add("§7Win: §a$" + String.format("%,.0f", win));
        lore.add(" ");
        lore.add("§eClick to Spin!");
        return ItemBuilder.of(mat, name, lore);
    }

    

    public static void startSpin(Player p, String betColor, CasinoManager casino, EconomyManager eco) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);
        double currentBet = cfg.bet;

        if (!eco.has(p, currentBet)) {
            p.sendMessage("§cNot enough money!");
            return;
        }
        eco.withdraw(p, currentBet);

        
        Inventory spinInv = Bukkit.createInventory(null, 27, "§0§lʀᴏʟʟɪɴɢ...");
        p.openInventory(spinInv);

        
        ItemStack pointer = ItemBuilder.of(Material.HOPPER, "§6§l⬇ WINNER ⬇");
        spinInv.setItem(4, pointer);

        for (int i = 0; i < 27; i++) {
            if (i >= 9 && i <= 17) continue;
            if (i == 4) continue;
            spinInv.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        List<ItemStack> sequence = generateSequence();

        new BukkitRunnable() {
            int index = 0;
            int totalSpins = new Random().nextInt(10) + 30;

            @Override
            public void run() {
                for (int i = 0; i < 9; i++) {
                    spinInv.setItem(9 + i, sequence.get((index + i) % sequence.size()));
                }

                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 2f);
                index++;

                if (index >= totalSpins) {
                    this.cancel();
                    ItemStack winnerItem = spinInv.getItem(13);
                    checkWin(p, winnerItem, betColor, casino, eco, currentBet);
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("SMPCore"), 0L, 2L);
    }

    private static void checkWin(Player p, ItemStack winnerItem, String betColor, CasinoManager casino, EconomyManager eco, double betAmount) {
        String winColor = "";
        Material mat = winnerItem.getType();

        if (mat == Material.RED_WOOL) winColor = "RED";
        else if (mat == Material.LIME_WOOL) winColor = "GREEN";
        else if (mat == Material.BLACK_WOOL) winColor = "BLACK";

        final String finalColor = winColor;
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"), () -> {
            boolean won = false;
            double payout = 0;

            if (betColor.contains(finalColor)) {
                won = true;
                if (finalColor.equals("GREEN")) payout = betAmount * 14;
                else payout = betAmount * 2;
            }

            if (won) {
                casino.registerWin(p);
                eco.deposit(p, payout);
                p.sendMessage("§a§lWIN! §7You won §a$" + String.format("%,.0f", payout));
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            } else {
                p.sendMessage("§c§lLOST! §7It landed on " + finalColor);
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }

            
            RouletteSetupMenu.open(p, casino);

        }, 20L); 
    }

    private static List<ItemStack> generateSequence() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (i % 15 == 0) items.add(ItemBuilder.of(Material.LIME_WOOL, "§a§lGREEN"));
            else if (i % 2 == 0) items.add(ItemBuilder.of(Material.RED_WOOL, "§c§lRED"));
            else items.add(ItemBuilder.of(Material.BLACK_WOOL, "§8§lBLACK"));
        }
        return items;
    }

    @Override
    public Inventory getInventory() { return inv; }
}