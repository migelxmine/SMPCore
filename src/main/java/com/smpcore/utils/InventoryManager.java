package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryManager {

    private final SMPCore plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public InventoryManager(SMPCore plugin) {
        this.plugin = plugin;
        loadDataFile();
    }

    private void loadDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveInventory(Player p) {
        String uuid = p.getUniqueId().toString();

        
        if (dataConfig.contains(uuid + ".contents")) {
            plugin.getLogger().warning("Inventory backup already exists for " + p.getName());
            return;
        }

        plugin.getLogger().info("Saving inventory for " + p.getName() + "...");

        dataConfig.set(uuid + ".contents", p.getInventory().getContents());
        dataConfig.set(uuid + ".armor", p.getInventory().getArmorContents());
        dataConfig.set(uuid + ".xp", p.getExp());
        dataConfig.set(uuid + ".level", p.getLevel());

        saveData();
    }

    @SuppressWarnings("unchecked")
    public void restoreInventory(Player p) {
        String uuid = p.getUniqueId().toString();

        if (!dataConfig.contains(uuid + ".contents")) {
            plugin.getLogger().warning("No inventory found to restore for " + p.getName());
            return;
        }

        plugin.getLogger().info("Restoring inventory for " + p.getName() + "...");

        try {
            
            List<ItemStack> contentList = (List<ItemStack>) dataConfig.getList(uuid + ".contents");
            if (contentList != null) {
                p.getInventory().setContents(contentList.toArray(new ItemStack[0]));
            }

            List<ItemStack> armorList = (List<ItemStack>) dataConfig.getList(uuid + ".armor");
            if (armorList != null) {
                p.getInventory().setArmorContents(armorList.toArray(new ItemStack[0]));
            }

            p.setExp((float) dataConfig.getDouble(uuid + ".xp"));
            p.setLevel(dataConfig.getInt(uuid + ".level"));

            
            dataConfig.set(uuid, null);
            saveData();

        } catch (Exception e) {
            p.sendMessage("§c§lERROR: §cCould not restore items! Contact an admin immediately.");
            plugin.getLogger().severe("Failed to restore inventory for " + p.getName());
            e.printStackTrace();
        }

    }
    public boolean hasStoredItems(Player p) {
        return dataConfig.contains(p.getUniqueId().toString() + ".contents");
    }
}