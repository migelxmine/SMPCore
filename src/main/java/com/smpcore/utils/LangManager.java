package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LangManager {

    private final SMPCore plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public LangManager(SMPCore plugin) {
        this.plugin = plugin;
        saveDefaultMessages();
        reload();
    }

    public void reload() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getConfig() {
        if (messagesConfig == null) {
            reload();
        }
        return messagesConfig;
    }

    
    
    public FileConfiguration getMessages() {
        return getConfig();
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    
    public String getMessage(String path) {
        String msg = getConfig().getString(path);
        if (msg == null) return "Message not found: " + path;

        if (msg.contains("%plugin_name%")) {
            msg = msg.replace("%plugin_name%", getConfig().getString("plugin-name", "SMPCore"));
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    
    
    
    public String getMessage(String path, String... replacements) {
        String msg = getMessage(path);

        
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = replacements[i];
                String value = replacements[i + 1];
                if (value != null) {
                    msg = msg.replace(placeholder, value);
                }
            }
        }
        return msg;
    }

    
    public List<String> getStringList(String path) {
        List<String> original = getConfig().getStringList(path);
        List<String> colored = new ArrayList<>();
        for (String s : original) {
            colored.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colored;
    }
}