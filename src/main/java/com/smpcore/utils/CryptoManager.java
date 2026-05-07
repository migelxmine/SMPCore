package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class CryptoManager {

    private final SMPCore plugin;
    private final Random random = new Random();
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    
    private final Map<String, Double> prices = new HashMap<>();
    private final Map<String, Double> previousPrices = new HashMap<>();

    
    private File file;
    private FileConfiguration config;

    
    public CryptoManager(SMPCore plugin, LangManager langManager) {
        this.plugin = plugin;
        setupData(); 
        setupCoins();
        startMarketSimulation(); 
    }

    
    private void setupData() {
        file = new File(plugin.getDataFolder(), "crypto.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveData() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void setupCoins() {
        
        prices.put("BTC", 45000.00);
        prices.put("ETH", 3200.00);
        prices.put("SMP", 10.00);
        prices.put("DOGE", 0.25);

        previousPrices.putAll(prices);
    }

    private void startMarketSimulation() {
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updatePrices();

            
            for (Player p : Bukkit.getOnlinePlayers()) {
                InventoryView view = p.getOpenInventory();
                if (view.getTitle().contains("CRYPTO") || view.getTitle().contains("Investment")) {
                    
                    try {
                        
                        
                        
                        
                    } catch (Exception ignored) {}
                }
            }
        }, 20L, 20L);
    }

    private void updatePrices() {
        for (String coin : prices.keySet()) {
            double current = prices.get(coin);
            previousPrices.put(coin, current);

            
            double change = (random.nextDouble() - 0.5) * 0.04;
            double newPrice = current + (current * change);

            if (newPrice < 0.01) newPrice = 0.01; 

            prices.put(coin, newPrice);
        }
    }

    

    
    public Set<String> getCoinNames() {
        return prices.keySet();
    }

    
    public String getCoinDisplayName(String ticker) {
        switch (ticker.toUpperCase()) {
            case "BTC": return "Bitcoin";
            case "ETH": return "Ethereum";
            case "SMP": return "MigelCoin";
            case "DOGE": return "Dogecoin";
            default: return ticker;
        }
    }

    
    public static String getCryptoName() {
        return "MigelCoin";
    }

    
    public double getPlayerCoinBalance(Player p, String ticker) {
        return config.getDouble(p.getUniqueId() + "." + ticker.toUpperCase(), 0.0);
    }

    public void setPlayerCoinBalance(Player p, String ticker, double amount) {
        config.set(p.getUniqueId() + "." + ticker.toUpperCase(), amount);
        saveData();
    }

    
    public void buyCoins(Player p, String ticker, int amount, EconomyManager eco) {
        double price = getPrice(ticker);
        double totalCost = price * amount;

        if (eco.getBalance(p) >= totalCost) {
            eco.withdraw(p, totalCost);

            double currentBalance = getPlayerCoinBalance(p, ticker);
            setPlayerCoinBalance(p, ticker, currentBalance + amount);

            p.sendMessage("§aYou bought " + amount + " " + ticker + " for $" + df.format(totalCost));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        } else {
            p.sendMessage("§cInsufficient funds! Cost: $" + df.format(totalCost));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    
    public void sellCoins(Player p, String ticker, int amount, EconomyManager eco) {
        double currentBalance = getPlayerCoinBalance(p, ticker);

        if (currentBalance >= amount) {
            double price = getPrice(ticker);
            double totalValue = price * amount;

            setPlayerCoinBalance(p, ticker, currentBalance - amount);
            eco.deposit(p, totalValue);

            p.sendMessage("§aYou sold " + amount + " " + ticker + " for $" + df.format(totalValue));
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        } else {
            p.sendMessage("§cYou don't have enough " + ticker + " to sell.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    

    public double getPrice(String coin) {
        return prices.getOrDefault(coin, 0.0);
    }

    public String getFormattedPrice(String coin) {
        return "$" + df.format(getPrice(coin));
    }

    public String getTrendArrow(String coin) {
        double current = prices.get(coin);
        double old = previousPrices.getOrDefault(coin, current);
        if (current > old) return "§a▲";
        if (current < old) return "§c▼";
        return "§7=";
    }
}