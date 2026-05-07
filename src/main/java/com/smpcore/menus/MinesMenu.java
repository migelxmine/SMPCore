package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MinesMenu implements MigelSMPMenu {

    private Inventory inv;

    private MinesMenu() {}

    public static void open(Player p, CasinoManager casino) {
        CasinoManager.MinesSession session = casino.getMinesSession(p);

        if (session == null || !session.active) {
            MinesSetupMenu.open(p, casino);
            return;
        }

        MinesMenu menu = new MinesMenu();
        menu.inv = Bukkit.createInventory(menu, 54, "§8§lＭＩＮＥＳ");

        
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§e§lYOUR STATS");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7Bet: §a$" + String.format("%,.0f", session.bet));
            lore.add("§7Mines: §c" + session.totalMines);
            lore.add("§7Multiplier: §6" + String.format("x%.2f", session.multiplier));
            meta.setLore(lore);
            skull.setItemMeta(meta);
        }
        menu.inv.setItem(4, skull);

        
        ItemStack border = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack pillar = ItemBuilder.of(Material.IRON_BARS, " "); 
        ItemStack unknown = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE, "§7§l???", "§eClick to Reveal");
        ItemStack diamond = ItemBuilder.of(Material.DIAMOND, "§9§lSAFE", "§aMultiplier Increased!");

        
        for (int i = 0; i < 54; i++) {
            if (i == 4) continue; 

            
            
            if (isPillarSlot(i)) {
                menu.inv.setItem(i, pillar);
                continue;
            }

            
            int gameIndex = getIndexFromSlot(i);

            if (gameIndex != -1) {
                
                if (session.clickedSlots.contains(gameIndex)) {
                    if (session.grid[gameIndex]) {
                        menu.inv.setItem(i, ItemBuilder.of(Material.TNT, "§c§lBOOM!"));
                    } else {
                        menu.inv.setItem(i, diamond);
                    }
                } else {
                    menu.inv.setItem(i, unknown);
                }
            } else {
                
                if (i != 49) {
                    menu.inv.setItem(i, border);
                }
            }
        }

        
        double winAmount = session.getCurrentWin();
        List<String> cashLore = new ArrayList<>();
        cashLore.add("§7Take your money and run.");
        cashLore.add(" ");
        cashLore.add("§7Current Win: §a$" + String.format("%,.0f", winAmount));
        cashLore.add(" ");
        cashLore.add("§eClick to Cashout!");

        
        if (session.clickedSlots.size() > 0) {
            menu.inv.setItem(49, ItemBuilder.of(Material.EMERALD_BLOCK, "§a§lCASHOUT", cashLore));
        } else {
            menu.inv.setItem(49, border);
        }

        p.openInventory(menu.inv);
    }

    public static void handleClick(Player p, int slot, CasinoManager casino, EconomyManager eco) {
        CasinoManager.MinesSession session = casino.getMinesSession(p);
        if (session == null || !session.active) {
            p.closeInventory();
            return;
        }

        
        if (slot == 49 && session.clickedSlots.size() > 0) {
            double win = session.getCurrentWin();
            eco.deposit(p, win);
            p.sendMessage("§a§lMINES! §7You cashed out §a$" + String.format("%,.0f", win));
            Sounds.playSuccess(p);

            session.active = false;
            casino.endMines(p);
            MinesSetupMenu.open(p, casino);
            return;
        }

        
        int index = getIndexFromSlot(slot);

        if (index != -1 && !session.clickedSlots.contains(index)) {
            
            if (session.grid[index]) {
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                p.sendMessage("§c§lBOOM! §7You hit a mine and lost $" + String.format("%,.0f", session.bet));
                p.getOpenInventory().setItem(slot, ItemBuilder.of(Material.TNT, "§c§lBOOM!"));

                session.active = false;
                casino.endMines(p);

                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"),
                        () -> MinesSetupMenu.open(p, casino), 30L);

            } else {
                
                session.increaseMultiplier(index);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

                
                if (session.clickedSlots.size() == (20 - session.totalMines)) {
                    double maxWin = session.getCurrentWin();
                    eco.deposit(p, maxWin);
                    p.sendTitle("§a§lMINES CLEARED!", "§7Won §a$" + String.format("%,.0f", maxWin), 10, 60, 20);
                    session.active = false;
                    casino.endMines(p);

                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"),
                            () -> MinesSetupMenu.open(p, casino), 60L);
                } else {
                    open(p, casino);
                }
            }
        }
    }

    
    private static boolean isPillarSlot(int slot) {
        int[] pillars = {10, 19, 28, 37, 16, 25, 34, 43};
        for (int i : pillars) {
            if (i == slot) return true;
        }
        return false;
    }

    
    
    private static int getIndexFromSlot(int slot) {
        int[] validSlots = {
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33,
                38, 39, 40, 41, 42
        };

        for (int i = 0; i < validSlots.length; i++) {
            if (validSlots[i] == slot) return i;
        }
        return -1;
    }

    @Override
    public Inventory getInventory() { return inv; }
}