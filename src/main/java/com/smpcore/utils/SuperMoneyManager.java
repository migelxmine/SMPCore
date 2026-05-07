package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SuperMoneyManager {

    private final SMPCore plugin;
    private File file;
    private FileConfiguration config;

    public SuperMoneyManager(SMPCore plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "supermoney.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(Player p) {
        return config.getDouble("players." + p.getUniqueId(), 0.0);
    }

    public void setBalance(Player p, double amount) {
        config.set("players." + p.getUniqueId(), amount);
        save();
    }

    public void deposit(Player p, double amount) {
        double current = getBalance(p);
        setBalance(p, current + amount);
    }

    public void withdraw(Player p, double amount) {
        double current = getBalance(p);
        setBalance(p, Math.max(0, current - amount));
    }

    

    public boolean has(Player p, double amount) {
        return getBalance(p) >= amount;
    }

    public void removeBalance(Player p, double amount) {
        withdraw(p, amount);
    }
}