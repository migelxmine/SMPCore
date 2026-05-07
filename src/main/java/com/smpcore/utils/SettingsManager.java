package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SettingsManager {

    private final SMPCore plugin;
    private File file;
    private FileConfiguration config;


    private boolean isSaving = false;

    public SettingsManager(SMPCore plugin) {
        this.plugin = plugin;
        setup();
    }

    public boolean isPayEnabled(Player p) {
        return getSetting(p, "payments");
    }

    private void setup() {
        file = new File(plugin.getDataFolder(), "settings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

                plugin.getLogger().severe("[SettingsManager] Failed to create settings.yml!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {

        if (isSaving) return;
        isSaving = true;


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("[SettingsManager] Failed to save settings.yml!");
            } finally {
                isSaving = false;
            }
        });
    }

    public boolean getSetting(Player p, String key) {

        return config.getBoolean(p.getUniqueId() + "." + key, true);
    }

    public boolean toggleSetting(Player p, String key) {
        boolean current = getSetting(p, key);
        boolean newState = !current;


        config.set(p.getUniqueId() + "." + key, newState);


        save();

        return newState;
    }
}