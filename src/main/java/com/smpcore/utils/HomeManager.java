/*
 * Copyright (c) 2025 Miguel Martinho Morbey Rodrigues Moreira (MigelSMP/SMPCore)
 * All Rights Reserved.
 */
package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;

public class HomeManager {

    private final SMPCore plugin;
    private File homesFile;
    private FileConfiguration homesConfig;

    public HomeManager(SMPCore plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml!");
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void saveHomes() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml!");
        }
    }

    
    public boolean setHome(Player player, int slot, Location location) {

        
        if (location.getWorld().getName().equals("SMP_Spawn")) {
            player.sendMessage("§c§lERROR: §cYou cannot set a home in the Spawn!");
            player.sendMessage("§7Please go to the survival world.");
            return false; 
        }

        String path = player.getUniqueId().toString() + "." + slot;
        homesConfig.set(path, location);
        saveHomes();
        return true; 
    }

    public Location getHome(Player player, int slot) {
        String path = player.getUniqueId().toString() + "." + slot;
        return homesConfig.getLocation(path);
    }

    public void deleteHome(Player player, int slot) {
        String path = player.getUniqueId().toString() + "." + slot;
        homesConfig.set(path, null);
        saveHomes();
    }
}