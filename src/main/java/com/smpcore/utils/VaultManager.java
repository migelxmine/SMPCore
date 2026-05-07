package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VaultManager {
    private final SMPCore plugin;
    private final Map<UUID, Integer> unlockedSlots = new HashMap<>();

    public VaultManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public int getUnlockedCount(UUID uuid) {
        return unlockedSlots.getOrDefault(uuid, 0);
    }

    public void openVault(Player p) {
        
        Inventory inv = Bukkit.createInventory(null, 27, "§8Private Vault");
        int unlocked = getUnlockedCount(p.getUniqueId());

        
        File file = new File(plugin.getDataFolder(), "vaults/" + p.getUniqueId() + ".yml");
        if (file.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            for (int i = 0; i < unlocked; i++) {
                if (cfg.contains("slot." + i)) {
                    inv.setItem(i, cfg.getItemStack("slot." + i));
                }
            }
        }

        
        for (int i = unlocked; i < 27; i++) {
            double cost = 500.0 * (i + 1);
            inv.setItem(i, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, "§c§lLOCKED SLOT",
                    "§7Click to unlock for §a$" + cost));
        }

        p.openInventory(inv);
    }

    public void unlockNext(Player p, EconomyManager eco) {
        int current = getUnlockedCount(p.getUniqueId());
        if (current >= 27) return;

        double cost = 500.0 * (current + 1);

        if (eco.has(p, cost)) {
            eco.withdraw(p, cost);
            unlockedSlots.put(p.getUniqueId(), current + 1);
            p.sendMessage("§aSlot #" + (current + 1) + " unlocked!");
            openVault(p); 
        } else {
            p.sendMessage("§cYou need $" + cost + " for the next slot!");
        }
    }

    public void saveContents(Player p, Inventory inv) {
        File folder = new File(plugin.getDataFolder(), "vaults");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, p.getUniqueId() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();

        int unlocked = getUnlockedCount(p.getUniqueId());

        
        for (int i = 0; i < unlocked; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.RED_STAINED_GLASS_PANE) {
                cfg.set("slot." + i, item);
            }
        }

        try {
            cfg.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao salvar Vault de " + p.getName());
        }

    }
    public int getTotalVaultsCreated() {
        
        return 0;
    }
}