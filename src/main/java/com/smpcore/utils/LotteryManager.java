package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class LotteryManager {

    private final SMPCore plugin;
    private final EconomyManager eco;
    private final NamespacedKey ticketKey; 

    public LotteryManager(SMPCore plugin, EconomyManager eco) {
        this.plugin = plugin;
        this.eco = eco;
        this.ticketKey = new NamespacedKey(plugin, "lottery_valid");
    }

    public void buyTicket(Player p) {
        double cost = 500.0;
        if (!eco.has(p, cost)) {
            p.sendMessage(ChatColor.RED + "Tickets cost $500.");
            return;
        }

        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(ChatColor.RED + "Inventory full.");
            return;
        }

        eco.withdraw(p, cost);

        
        ItemStack ticket = new ItemStack(Material.PAPER);
        ItemMeta meta = ticket.getItemMeta();
        meta.setDisplayName("§6§lLOTTERY TICKET");

        
        
        meta.getPersistentDataContainer().set(ticketKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        

        ticket.setItemMeta(meta);
        p.getInventory().addItem(ticket);

        p.sendMessage(ChatColor.GREEN + "Ticket purchased! Click it in the validator to check.");
        Sounds.playSuccess(p);
    }

    public void validateTicket(Player p, ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return;

        ItemMeta meta = item.getItemMeta();
        boolean hasName = meta.hasDisplayName() && meta.getDisplayName().equals("§6§lLOTTERY TICKET");
        boolean hasTag = meta.getPersistentDataContainer().has(ticketKey, PersistentDataType.STRING);

        
        if (hasName && !hasTag) {
            
            p.sendMessage(ChatColor.RED + "§lFRAUD DETECTED!");
            Bukkit.getBanList(BanList.Type.NAME).addBan(
                    p.getName(),
                    "Lottery Fraud Attempt (Forged Ticket)",
                    new Date(System.currentTimeMillis() + (5 * 60 * 1000)), 
                    "Console"
            );
            p.kickPlayer("§c§lFRAUD DETECTED!\n§7Do not try to forge tickets.\n§cBanned for 5 minutes.");
            return;
        }

        if (hasTag) {
            
            p.getInventory().removeItem(item);

            double chance = new Random().nextDouble() * 100;
            if (chance < 5.0) { 
                double prize = 5000.0;
                eco.deposit(p, prize);
                p.sendMessage("§6§lWINNER! §aYou won $" + String.format("%,.0f", prize));
                Sounds.playSuccess(p);
                Bukkit.broadcastMessage("§e" + p.getName() + " won the Lottery!");
            } else {
                p.sendMessage("§cBetter luck next time.");
                Sounds.playError(p);
            }
        } else {
            p.sendMessage(ChatColor.RED + "This is not a lottery ticket.");
        }
    }
}