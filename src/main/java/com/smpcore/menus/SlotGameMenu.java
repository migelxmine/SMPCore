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

public class SlotGameMenu implements MigelSMPMenu {

    private Inventory inv;

    
    
    private static final int[] REEL_1 = {2, 11, 20};
    private static final int[] REEL_2 = {4, 13, 22};
    private static final int[] REEL_3 = {6, 15, 24};
    
    private static final int[] PAYLINE = {11, 13, 15};

    private SlotGameMenu() {}

    public static void startGame(Player p, String theme, CasinoManager casino) {
        CasinoManager.MinesConfig cfg = casino.getConfig(p);

        
        if (!casino.getEconomy().has(p, cfg.bet)) {
            p.sendMessage("§cNot enough money!");
            return;
        }
        casino.getEconomy().withdraw(p, cfg.bet);

        SlotGameMenu menu = new SlotGameMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8ꜱʟᴏᴛ: §l" + theme);

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        menu.inv.setItem(10, ItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE, "§e➡"));
        menu.inv.setItem(12, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        menu.inv.setItem(14, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        menu.inv.setItem(16, ItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE, "§e⬅"));

        p.openInventory(menu.inv);

        
        
        
        boolean willWin = casino.shouldWin(p, 35.0);

        
        SlotSymbol[] finalResult = calculateResult(theme, willWin);

        
        startSpinAnimation(p, menu.inv, theme, finalResult, cfg.bet, casino);
    }

    private static void startSpinAnimation(Player p, Inventory inv, String theme, SlotSymbol[] result, double bet, CasinoManager casino) {
        SMPCore plugin = (SMPCore) Bukkit.getPluginManager().getPlugin("SMPCore");
        List<SlotSymbol> themeSymbols = getSymbolsForTheme(theme);
        Random rand = new Random();

        new BukkitRunnable() {
            int ticks = 0;
            
            int stop1 = 20;
            int stop2 = 35;
            int stop3 = 50;

            @Override
            public void run() {
                
                if (!p.getOpenInventory().getTitle().equals(inv.getViewers().get(0).getOpenInventory().getTitle())) {
                    this.cancel();
                    return;
                }

                
                if (ticks % 3 == 0 && ticks < stop3) {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.2f, 2f);
                }

                
                if (ticks < stop1) {
                    spinReel(inv, REEL_1, themeSymbols, rand);
                } else if (ticks == stop1) {
                    setReelResult(inv, REEL_1, result[0], themeSymbols, rand);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                }

                
                if (ticks < stop2) {
                    spinReel(inv, REEL_2, themeSymbols, rand);
                } else if (ticks == stop2) {
                    setReelResult(inv, REEL_2, result[1], themeSymbols, rand);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                }

                
                if (ticks < stop3) {
                    spinReel(inv, REEL_3, themeSymbols, rand);
                } else if (ticks == stop3) {
                    setReelResult(inv, REEL_3, result[2], themeSymbols, rand);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

                    
                    this.cancel();
                    checkPayout(p, result, bet, casino, inv);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    
    private static void spinReel(Inventory inv, int[] slots, List<SlotSymbol> symbols, Random rand) {
        for (int slot : slots) {
            inv.setItem(slot, symbols.get(rand.nextInt(symbols.size())).getItem());
        }
    }

    
    private static void setReelResult(Inventory inv, int[] slots, SlotSymbol target, List<SlotSymbol> symbols, Random rand) {
        inv.setItem(slots[0], symbols.get(rand.nextInt(symbols.size())).getItem()); 
        inv.setItem(slots[1], target.getItem()); 
        inv.setItem(slots[2], symbols.get(rand.nextInt(symbols.size())).getItem()); 
    }

    private static void checkPayout(Player p, SlotSymbol[] res, double bet, CasinoManager casino, Inventory inv) {
        
        if (res[0] == res[1] && res[1] == res[2] && res[0].getMultiplier() > 0) {
            double multiplier = res[0].getMultiplier();
            double payout = bet * multiplier;

            casino.registerWin(p);
            casino.getEconomy().deposit(p, payout);

            p.sendTitle("§6§lJACKPOT!", "§7Won: §a$" + String.format("%,.0f", payout), 5, 40, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            p.sendMessage("§a§lSLOTS! §7You won $" + String.format("%,.0f", payout));

            
            winEffect(inv);

        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }

        
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SMPCore"),
                () -> SlotsSetupMenu.open(p, casino), 40L);
    }

    private static void winEffect(Inventory inv) {
        inv.setItem(10, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "§a§l$$$"));
        inv.setItem(16, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "§a§l$$$"));
    }

    

    private static SlotSymbol[] calculateResult(String theme, boolean win) {
        List<SlotSymbol> symbols = getSymbolsForTheme(theme);
        SlotSymbol[] res = new SlotSymbol[3];
        Random rand = new Random();

        if (win) {
            
            
            SlotSymbol winner = pickWeightedSymbol(symbols, rand);
            res[0] = winner;
            res[1] = winner;
            res[2] = winner;
        } else {
            
            res[0] = symbols.get(rand.nextInt(symbols.size()));
            res[1] = symbols.get(rand.nextInt(symbols.size()));
            do {
                res[2] = symbols.get(rand.nextInt(symbols.size()));
            } while (res[0] == res[1] && res[1] == res[2]); 
        }
        return res;
    }

    private static SlotSymbol pickWeightedSymbol(List<SlotSymbol> symbols, Random rand) {
        
        int roll = rand.nextInt(100);
        if (roll < 50) return symbols.get(1); 
        if (roll < 80) return symbols.get(2); 
        if (roll < 95) return symbols.get(3); 
        return symbols.get(4); 
    }

    private static List<SlotSymbol> getSymbolsForTheme(String theme) {
        List<SlotSymbol> list = new ArrayList<>();
        if (theme.equals("CLASSIC")) {
            list.add(SlotSymbol.C_TRASH); list.add(SlotSymbol.C_LOW); list.add(SlotSymbol.C_MED); list.add(SlotSymbol.C_HIGH); list.add(SlotSymbol.C_JACKPOT);
        } else if (theme.equals("NETHER")) {
            list.add(SlotSymbol.N_TRASH); list.add(SlotSymbol.N_LOW); list.add(SlotSymbol.N_MED); list.add(SlotSymbol.N_HIGH); list.add(SlotSymbol.N_JACKPOT);
        } else if (theme.equals("AQUATIC")) {
            list.add(SlotSymbol.A_TRASH); list.add(SlotSymbol.A_LOW); list.add(SlotSymbol.A_MED); list.add(SlotSymbol.A_HIGH); list.add(SlotSymbol.A_JACKPOT);
        } else if (theme.equals("MINING")) {
            list.add(SlotSymbol.M_TRASH); list.add(SlotSymbol.M_LOW); list.add(SlotSymbol.M_MED); list.add(SlotSymbol.M_HIGH); list.add(SlotSymbol.M_JACKPOT);
        } else { 
            list.add(SlotSymbol.K_TRASH); list.add(SlotSymbol.K_LOW); list.add(SlotSymbol.K_MED); list.add(SlotSymbol.K_HIGH); list.add(SlotSymbol.K_JACKPOT);
        }
        return list;
    }

    @Override
    public Inventory getInventory() { return inv; }
}