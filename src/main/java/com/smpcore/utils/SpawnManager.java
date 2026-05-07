package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class SpawnManager {

    private final SMPCore plugin;
    private Location spawnLocation;

    public SpawnManager(SMPCore plugin) {
        this.plugin = plugin;
        loadSpawn();
    }

    public void setSpawn(Location loc) {
        this.spawnLocation = loc;
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.getConfig().set("spawn.yaw", loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
    }

    public void teleport(Player p) {
        if (spawnLocation != null) {
            p.teleport(spawnLocation);
            p.sendMessage(ChatColor.GREEN + "Welcome to Spawn!");
            Sounds.playSuccess(p);
        } else {
           
        }
    }

    
    public void setupWorld(Player admin) {
        World w = admin.getWorld();

        
        w.setPVP(false);
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        w.setTime(6000); 
        w.setStorm(false);

        
        WorldBorder border = w.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(10000); 
        border.setDamageAmount(1.0);

        
        setSpawn(admin.getLocation());
        w.setSpawnLocation(admin.getLocation());

        admin.sendMessage(ChatColor.GREEN + "✔ World Setup Complete!");
        admin.sendMessage(ChatColor.GRAY + "- PvP: OFF | Mobs: OFF | Time: Frozen");
        admin.sendMessage(ChatColor.GRAY + "- Border: 10k x 10k");
        admin.sendMessage(ChatColor.GRAY + "- Spawn Point: SET");
    }

    private void loadSpawn() {
        if (plugin.getConfig().contains("spawn.world")) {
            spawnLocation = new Location(
                    Bukkit.getWorld(plugin.getConfig().getString("spawn.world")),
                    plugin.getConfig().getDouble("spawn.x"),
                    plugin.getConfig().getDouble("spawn.y"),
                    plugin.getConfig().getDouble("spawn.z"),
                    (float) plugin.getConfig().getDouble("spawn.yaw"),
                    (float) plugin.getConfig().getDouble("spawn.pitch")
            );
        }
    }
}