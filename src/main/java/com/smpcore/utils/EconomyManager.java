/*
 * Copyright (c) 2025 Miguel Martinho Morbey Rodrigues Moreira (MigelSMP/SMPCore)
 * All Rights Reserved.
 *
 * Este software é propriedade confidencial e proprietária de Miguel Martinho Morbey Rodrigues Moreira.
 * ("Informação Confidencial"). Você não deve divulgar tal Informação
 * Confidencial e deve usá-la apenas de acordo com os termos do
 * contrato de licença que você celebrou com Miguel Martinho Morbey Rodrigues Moreira.
 *
 * É ESTRITAMENTE PROIBIDO DESCOMPILAR, MODIFICAR OU REDISTRIBUIR ESTE SOFTWARE.
 */
package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final SMPCore plugin;

    private final Map<UUID, Double> balances = new HashMap<>();

    public EconomyManager(SMPCore plugin) {
        this.plugin = plugin;
        loadBalances();
    }

    public double getBalance(OfflinePlayer player) {

        return balances.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        balances.put(player.getUniqueId(), amount);
        saveAsync();
    }

    public void deposit(OfflinePlayer player, double amount) {
        if (amount <= 0) return;
        setBalance(player, getBalance(player) + amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (amount <= 0) return false;
        double current = getBalance(player);

        if (current >= amount) {
            setBalance(player, current - amount);


            if (player.getName() != null) {
                Bukkit.getConsoleSender().sendMessage(
                        "§e[EconomyLog] §fPlayer §9" + player.getName() + " §fspent §c$" + String.format("%,.2f", amount)
                );
            }
            return true;
        }
        return false;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }


    private void saveAsync() {

        Map<UUID, Double> snapshot = new HashMap<>(balances);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration config = plugin.getConfig();
            for (Map.Entry<UUID, Double> entry : snapshot.entrySet()) {
                config.set("balances." + entry.getKey(), entry.getValue());
            }
            plugin.saveConfig();
        });
    }


    public void saveBalances() {
        FileConfiguration config = plugin.getConfig();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set("balances." + entry.getKey(), entry.getValue());
        }
        plugin.saveConfig();
        plugin.getLogger().info("Saved " + balances.size() + " balances to config.yml");
    }


    public void loadBalances() {
        FileConfiguration config = plugin.getConfig();
        balances.clear();
        if (config.isConfigurationSection("balances")) {
            for (String key : config.getConfigurationSection("balances").getKeys(false)) {
                try {
                    balances.put(UUID.fromString(key), config.getDouble("balances." + key));
                } catch (IllegalArgumentException ignored) {}
            }
            plugin.getLogger().info("Loaded " + balances.size() + " balances from config.yml into memory.");
        }
    }

    public double getTotalCirculation() {

        return balances.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}