package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DragonGameMenu implements MigelSMPMenu {

    private Inventory inv;
    private static final int[][] ROWS = {
            {45, 46, 47, 48, 49, 50, 51, 52, 53},
            {36, 37, 38, 39, 40, 41, 42, 43, 44},
            {27, 28, 29, 30, 31, 32, 33, 34, 35},
            {18, 19, 20, 21, 22, 23, 24, 25, 26},
            {9, 10, 11, 12, 13, 14, 15, 16, 17}
    };

    private DragonGameMenu() {}

    public static void open(Player p, CasinoManager casino) {
        CasinoManager.DragonGame game = casino.getDragonSession(p);
        if (game == null || !game.active) return;

        DragonGameMenu menu = new DragonGameMenu();
        menu.inv = Bukkit.createInventory(menu, 54, "§8§lＤＲＡＧＯＮ ＴＯＷＥＲ");

        for (int row = 0; row < 5; row++) {
            renderRow(menu.inv, row, game);
        }

        ItemStack glass = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) menu.inv.setItem(i, glass);

        double nextWin = game.bet * Math.pow(getMultiplierBase(game.difficulty), game.currentRow + 1);
        menu.inv.setItem(4, ItemBuilder.of(Material.DRAGON_BREATH,
                "§d§lCurrent Level: " + (game.currentRow + 1) + "/5",
                "§7Next Win: §a$" + String.format("%,.0f", nextWin)));

        if (game.currentRow > 0) {
            double currentWin = game.getCurrentWin();
            menu.inv.setItem(8, ItemBuilder.of(Material.EMERALD_BLOCK,
                    "§a§lCASHOUT",
                    "§7Take §a$" + String.format("%,.0f", currentWin),
                    "§eClick to Stop"));
        }

        p.openInventory(menu.inv);
    }

    private static void renderRow(Inventory inv, int row, CasinoManager.DragonGame game) {
        int[] slots = getSlotsForDifficulty(game.difficulty, row);
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            if (row < game.currentRow) {
                if (game.grid[row][i]) inv.setItem(slot, ItemBuilder.of(Material.DRAGON_EGG, "§a§lCLEARED"));
                else inv.setItem(slot, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE, "§8Empty"));
            } else if (row == game.currentRow) {
                inv.setItem(slot, ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE, "§d§l???", "§7Click to Reveal"));
            } else {
                inv.setItem(slot, ItemBuilder.of(Material.OBSIDIAN, "§8LOCKED"));
            }
        }
    }

    public static void handleClick(Player p, int slot, CasinoManager casino, EconomyManager eco) {
        CasinoManager.DragonGame game = casino.getDragonSession(p);
        if (game == null || !game.active) return;

        
        if (slot == 8 && game.currentRow > 0) {
            double win = game.getCurrentWin();
            eco.deposit(p, win);
            p.sendMessage("§a§lDRAGON! §7Cashed out: §a$" + String.format("%,.0f", win));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            game.active = false;

            
            DragonSetupMenu.open(p, casino);
            return;
        }

        int[] currentSlots = getSlotsForDifficulty(game.difficulty, game.currentRow);
        int colClicked = -1;
        for (int i = 0; i < currentSlots.length; i++) {
            if (currentSlots[i] == slot) {
                colClicked = i;
                break;
            }
        }

        if (colClicked != -1) {
            boolean isWin = game.grid[game.currentRow][colClicked];

            if (isWin) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                game.currentRow++;

                if (game.currentRow >= 5) {
                    
                    double maxWin = game.bet * Math.pow(getMultiplierBase(game.difficulty), 5);
                    eco.deposit(p, maxWin);
                    p.sendTitle("§d§lDRAGON MASTER!", "§7Won §a$" + String.format("%,.0f", maxWin), 10, 60, 20);
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    game.active = false;

                    
                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"),
                            () -> DragonSetupMenu.open(p, casino), 60L);
                } else {
                    open(p, casino);
                }
            } else {
                
                revealLoss(p, game, casino);
            }
        }
    }

    private static void revealLoss(Player p, CasinoManager.DragonGame game, CasinoManager casino) {
        Inventory inv = p.getOpenInventory().getTopInventory();
        int[] slots = getSlotsForDifficulty(game.difficulty, game.currentRow);

        for (int i = 0; i < slots.length; i++) {
            if (game.grid[game.currentRow][i]) {
                inv.setItem(slots[i], ItemBuilder.of(Material.DRAGON_EGG, "§aHere it was!"));
            } else {
                inv.setItem(slots[i], ItemBuilder.of(Material.MAGMA_BLOCK, "§c§lEXPLOSION"));
            }
        }

        p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        p.sendMessage("§c§lBURNED! §7You lost everything.");
        game.active = false;

        
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"),
                () -> DragonSetupMenu.open(p, casino), 40L);
    }

    private static int[] getSlotsForDifficulty(int diff, int row) {
        int[] rowSlots = ROWS[row];
        if (diff == 0) return new int[]{rowSlots[3], rowSlots[5]};
        if (diff == 1) return new int[]{rowSlots[2], rowSlots[4], rowSlots[6]};
        return new int[]{rowSlots[1], rowSlots[3], rowSlots[5], rowSlots[7]};
    }

    private static double getMultiplierBase(int diff) {
        return (diff == 0) ? 1.9 : (diff == 1) ? 2.9 : 3.8;
    }

    @Override
    public Inventory getInventory() { return inv; }
}