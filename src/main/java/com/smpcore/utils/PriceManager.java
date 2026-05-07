package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PriceManager {

    private final FileConfiguration config;
    private final Logger logger;

    
    private final Map<Material, Double> basePrices = new HashMap<>();
    
    private final Map<Material, Double> priceModifiers = new HashMap<>();

    private File dynamicFile;
    private FileConfiguration dynamicConfig;
    private final SMPCore plugin;

    
    private final double FLUCTUATION_PER_ITEM = 0.0005;
    private final double MIN_MULTIPLIER = 0.2;
    private final double MAX_MULTIPLIER = 5.0;

    public PriceManager(FileConfiguration config, Logger logger) {
        this.config = config;
        this.logger = logger;
        this.plugin = JavaPlugin.getPlugin(SMPCore.class);

        loadManualPrices(); 
        loadDynamicData();  
    }

    
    private void loadManualPrices() {
        if (!config.contains("item-prices")) return;
        for (String key : config.getConfigurationSection("item-prices").getKeys(false)) {
            try {
                Material mat = Material.valueOf(key.toUpperCase());
                double price = config.getDouble("item-prices." + key);
                basePrices.put(mat, price);
            } catch (IllegalArgumentException e) {
                
            }
        }
    }

    
    private void loadDynamicData() {
        dynamicFile = new File("plugins/SMPCore/dynamic_prices.yml");
        if (!dynamicFile.exists()) {
            try { dynamicFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dynamicConfig = YamlConfiguration.loadConfiguration(dynamicFile);

        for (String key : dynamicConfig.getKeys(false)) {
            try {
                Material mat = Material.valueOf(key);
                double modifier = dynamicConfig.getDouble(key);
                priceModifiers.put(mat, modifier);
            } catch (Exception ignored) {}
        }
    }

    public void saveDynamicData() {
        if (dynamicFile == null) return;
        for (Map.Entry<Material, Double> entry : priceModifiers.entrySet()) {
            dynamicConfig.set(entry.getKey().name(), entry.getValue());
        }
        try { dynamicConfig.save(dynamicFile); } catch (IOException e) { e.printStackTrace(); }
    }

    
    private double calculateAutoPrice(Material mat) {
        String name = mat.name();

        
        if (name.contains("ELYTRA") || name.contains("BEACON") || name.contains("NETHER_STAR") || name.contains("TOTEM")) return 2000.0;
        if (name.contains("NETHERITE")) return 500.0;
        if (name.contains("DRAGON") || name.contains("WITHER") || name.contains("HEAD")) return 300.0;

        
        if (name.contains("DIAMOND")) return 100.0;
        if (name.contains("EMERALD")) return 50.0;
        if (name.contains("GOLD")) return 30.0;
        if (name.contains("IRON")) return 15.0;

        
        if (name.contains("ENDER") || name.contains("BLAZE") || name.contains("GHAST") || name.contains("SHULKER")) return 25.0;
        if (name.contains("LAPIS") || name.contains("QUARTZ") || name.contains("AMETHYST")) return 10.0;
        if (name.contains("REDSTONE") || name.contains("GLOWSTONE") || name.contains("SLIME")) return 5.0;

        
        if (name.contains("COAL") || name.contains("COPPER") || name.contains("CHARCOAL")) return 4.0;
        if (name.contains("LOG") || name.contains("WOOD") || name.contains("PLANKS") || name.contains("WOOL")) return 2.0;

        
        if (mat.isEdible()) return 8.0;

        
        if (name.contains("STONE") || name.contains("COBBLE") || name.contains("DIRT") || name.contains("SAND") || name.contains("GRAVEL") || name.contains("GLASS")) return 0.5;
        if (name.contains("SEED") || name.contains("SAPLING") || name.contains("LEAVES")) return 0.2;

        
        if (mat.isBlock()) return 1.0;

        
        return 5.0;
    }

    

    public double getPrice(Material material) {
        
        double base;
        if (basePrices.containsKey(material)) {
            base = basePrices.get(material);
        } else {
            
            base = calculateAutoPrice(material);
            basePrices.put(material, base);
        }

        
        if (!plugin.getConfig().getBoolean("dynamic-pricing", true)) {
            return base;
        }

        double modifier = priceModifiers.getOrDefault(material, 1.0);
        return base * modifier;
    }

    public double getSellPrice(Material material) {
        return getPrice(material) * 0.8;
    }

    
    public boolean hasPrice(Material material) {
        if (material == Material.AIR || material == Material.BEDROCK || material == Material.BARRIER || material == Material.COMMAND_BLOCK) {
            return false;
        }
        
        return true;
    }

    public void adjustPrice(Material material, int amount, boolean isBuying) {
        if (!plugin.getConfig().getBoolean("dynamic-pricing", true)) return;

        double currentMod = priceModifiers.getOrDefault(material, 1.0);
        double change = amount * FLUCTUATION_PER_ITEM;

        if (isBuying) currentMod += change;
        else currentMod -= change;

        if (currentMod < MIN_MULTIPLIER) currentMod = MIN_MULTIPLIER;
        if (currentMod > MAX_MULTIPLIER) currentMod = MAX_MULTIPLIER;

        priceModifiers.put(material, currentMod);
        saveDynamicData();
    }

    public String getTrendSymbol(Material material) {
        if (!plugin.getConfig().getBoolean("dynamic-pricing", true)) return "";
        double mod = priceModifiers.getOrDefault(material, 1.0);
        if (mod > 1.05) return "§c📈";
        if (mod < 0.95) return "§a📉";
        return "§e=";
    }
}