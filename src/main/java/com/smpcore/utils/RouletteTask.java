package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteTask extends BukkitRunnable {

    private final SMPCore plugin;
    private final Player player;
    private final KeyType type;
    private final Inventory inv;
    private final List<ItemStack> items = new ArrayList<>();
    private final Random random = new Random();

    private int ticks = 0;
    private double speed = 1.0; 
    private double slowDown = 0.15; 

    public RouletteTask(SMPCore plugin, Player player, KeyType type) {
        this.plugin = plugin;
        this.player = player;
        this.type = type;

        
        this.inv = Bukkit.createInventory(null, 27, "§8Roulette: " + type.getDisplayName());

        
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for (int i = 0; i < 9; i++) inv.setItem(i, glass);      
        for (int i = 18; i < 27; i++) inv.setItem(i, glass);    

        
        ItemStack pointer = new ItemStack(Material.HOPPER);
        ItemMeta pm = pointer.getItemMeta();
        pm.setDisplayName("§6⬇ WINNER ⬇");
        pointer.setItemMeta(pm);
        inv.setItem(4, pointer); 

        
        for (int i = 0; i < 50; i++) {
            items.add(KeyRewards.getRandomReward(type));
        }

        player.openInventory(inv);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        ticks++;

        
        
        if (ticks % (int) speed != 0) return;

        
        
        items.remove(0);
        items.add(KeyRewards.getRandomReward(type));

        
        for (int i = 0; i < 9; i++) {
            inv.setItem(9 + i, items.get(i));
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 2f);

        
        speed += slowDown;

        
        if (speed >= 10.0) { 
            this.cancel();
            finish();
        }
    }

    private void finish() {
        
        ItemStack winner = inv.getItem(13);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.sendMessage("§6§lCONGRATS! §eYou won " + winner.getItemMeta().getDisplayName() != null ? winner.getItemMeta().getDisplayName() : winner.getType().name());

        
        if (type == KeyType.BUSINESS) {
            
            double money = 1000 + random.nextInt(9000); 
            plugin.getEconomyManager().deposit(player, money);
            player.sendMessage("§a+ $" + String.format("%,.0f", money));
        } else {
            
            
            player.getInventory().addItem(winner);
        }

        
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(plugin, 40L);
    }
}