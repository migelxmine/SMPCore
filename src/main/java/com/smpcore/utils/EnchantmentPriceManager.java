package com.smpcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EnchantmentPriceManager {

    private final Map<Enchantment, Double> basePrices = new HashMap<>();
    private static final double LEVEL_MULTIPLIER = 2.5;

    public EnchantmentPriceManager(FileConfiguration config, Logger logger) {
        loadPrices(config, logger);
    }

    private void loadPrices(FileConfiguration config, Logger logger) {
        if (!config.isConfigurationSection("enchantment-prices")) {
            logger.warning("'enchantment-prices' section not found in config.yml!");
            return;
        }
        for (String enchantKey : config.getConfigurationSection("enchantment-prices").getKeys(false)) {
            Enchantment enchantment = Enchantment.getByName(enchantKey.toUpperCase());
            if (enchantment != null) {
                double basePrice = config.getDouble("enchantment-prices." + enchantKey);
                basePrices.put(enchantment, basePrice);
            } else {
                logger.warning("Enchantment '" + enchantKey + "' in config.yml is invalid.");
            }
        }
        logger.info("Loaded " + basePrices.size() + " enchantment base prices.");
    }

    public double getPrice(Enchantment enchantment, int level) {
        double basePrice = basePrices.getOrDefault(enchantment, -1.0);
        if (basePrice == -1.0) return -1.0;
        return basePrice * (level * LEVEL_MULTIPLIER);
    }

    public void buyEnchantment(Player player, Enchantment enchantment, int level, EconomyManager ecoManager, LangManager lang) {
        if (level <= 0 || level > enchantment.getMaxLevel()) {
            player.sendMessage(ChatColor.RED + "Invalid enchantment level.");
            Sounds.playError(player);
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(lang.getMessage("messages.inventory-full"));
            Sounds.playError(player);
            return;
        }

        double price = getPrice(enchantment, level);
        if (price == -1.0) {
            player.sendMessage(ChatColor.RED + "This enchantment is not for sale.");
            Sounds.playError(player);
            return;
        }

        if (!ecoManager.has(player, price)) {
            player.sendMessage(lang.getMessage("messages.insufficient-funds", "%amount%", String.format("%,.2f", price)));
            Sounds.playError(player);
            return;
        }

        ecoManager.withdraw(player, price);

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta != null) {
            meta.addStoredEnchant(enchantment, level, true);
            book.setItemMeta(meta);
        }

        player.getInventory().addItem(book);
        player.sendMessage(ChatColor.GREEN + "You purchased " + enchantment.getKey().getKey() + " " + level + " for $" + String.format("%,.2f", price));
        Sounds.playSuccess(player);
    }
}