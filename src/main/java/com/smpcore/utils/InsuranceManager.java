package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InsuranceManager {

    private final SMPCore plugin;
    private final EconomyManager eco;
    private File file;
    private FileConfiguration config;

    
    private final double BASE_COST = 1000.0;
    private final double PERCENTAGE_COST = 0.05; 
    private final long DURATION_MS = 7L * 24 * 60 * 60 * 1000; 

    
    private final double BANKRUPTCY_COST = 20000.0;
    private final double BANKRUPTCY_THRESHOLD = 100.0;
    private final double BANKRUPTCY_PAYOUT = 50000.0;

    public InsuranceManager(SMPCore plugin, EconomyManager eco) {
        this.plugin = plugin;
        this.eco = eco;
        loadData();
    }

    

    public double getDeathPrice(Player p) {
        double balance = eco.getBalance(p);
        
        return BASE_COST + (balance * PERCENTAGE_COST);
    }

    

    public boolean hasActiveDeathInsurance(Player p) {
        long expiry = config.getLong("players." + p.getUniqueId() + ".death_expiry", 0);

        
        return expiry > System.currentTimeMillis();
    }

    public String getTimeLeft(Player p) {
        long expiry = config.getLong("players." + p.getUniqueId() + ".death_expiry", 0);
        long diff = expiry - System.currentTimeMillis();

        if (diff <= 0) return "Expired";

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;

        return days + "d " + hours + "h";
    }

    public void buyDeathInsurance(Player p) {
        if (hasActiveDeathInsurance(p)) {
            p.sendMessage(ChatColor.RED + "You already have an active policy for: " + getTimeLeft(p));
            return;
        }

        double cost = getDeathPrice(p);

        if (eco.has(p, cost)) {
            eco.withdraw(p, cost);

            
            long expiryDate = System.currentTimeMillis() + DURATION_MS;

            config.set("players." + p.getUniqueId() + ".death_expiry", expiryDate);
            saveData();

            p.sendMessage(ChatColor.GREEN + "Insurance Purchased for §67 Days§a!");
            p.sendMessage(ChatColor.GRAY + "Cost: $" + String.format("%,.0f", cost));
            Sounds.playSuccess(p);
        } else {
            p.sendMessage(ChatColor.RED + "Insufficient funds!");
            p.sendMessage(ChatColor.RED + "Cost for you: $" + String.format("%,.0f", cost));
            Sounds.playError(p);
        }
    }

    public void useDeathInsurance(Player p) {
        
        config.set("players." + p.getUniqueId() + ".death_expiry", 0);
        saveData();

        p.sendMessage(ChatColor.GOLD + "§lINSURANCE USED! §eYour items were saved.");
        Sounds.playAnvil(p);
    }

    

    public boolean hasBankruptcyInsurance(Player p) {
        return config.getBoolean("players." + p.getUniqueId() + ".bankruptcy", false);
    }

    public void buyBankruptcyInsurance(Player p) {
        if (hasBankruptcyInsurance(p)) {
            p.sendMessage(ChatColor.RED + "You already have Bankruptcy protection.");
            return;
        }
        if (eco.has(p, BANKRUPTCY_COST)) {
            eco.withdraw(p, BANKRUPTCY_COST);
            config.set("players." + p.getUniqueId() + ".bankruptcy", true);
            saveData();
            p.sendMessage(ChatColor.GREEN + "Bankruptcy Insurance Purchased!");
            Sounds.playSuccess(p);
        } else {
            p.sendMessage(ChatColor.RED + "Cost: $" + String.format("%,.0f", BANKRUPTCY_COST));
            Sounds.playError(p);
        }
    }

    public void checkBankruptcy(Player p) {
        if (!hasBankruptcyInsurance(p)) return;
        if (eco.getBalance(p) < BANKRUPTCY_THRESHOLD) {
            eco.deposit(p, BANKRUPTCY_PAYOUT);
            config.set("players." + p.getUniqueId() + ".bankruptcy", false);
            saveData();
            p.sendTitle("§6§lBAILOUT!", "§7Received §a$" + String.format("%,.0f", BANKRUPTCY_PAYOUT), 10, 60, 20);
            Sounds.playSuccess(p);
        }
    }

    private void loadData() {
        file = new File(plugin.getDataFolder(), "insurance.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) {}
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void saveData() {
        try { config.save(file); } catch (IOException e) {}
    }
}