package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionManager {

    private final SMPCore plugin;
    private File file;
    private FileConfiguration config;

    
    private final List<Region> regions = new ArrayList<>();

    
    private final Map<UUID, Location[]> selections = new HashMap<>();

    public RegionManager(SMPCore plugin) {
        this.plugin = plugin;
        loadRegions();
    }

    

    public void setPos1(Player p, Location loc) {
        Location[] locs = selections.getOrDefault(p.getUniqueId(), new Location[2]);
        locs[0] = loc;
        selections.put(p.getUniqueId(), locs);
        p.sendMessage(ChatColor.GREEN + "§lPos 1 set! §7(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
    }

    public void setPos2(Player p, Location loc) {
        Location[] locs = selections.getOrDefault(p.getUniqueId(), new Location[2]);
        locs[1] = loc;
        selections.put(p.getUniqueId(), locs);
        p.sendMessage(ChatColor.GREEN + "§lPos 2 set! §7(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
    }

    

    public boolean confirmClaim(Player p) {
        if (!selections.containsKey(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "You haven't selected any points yet.");
            return false;
        }

        Location[] locs = selections.get(p.getUniqueId());
        if (locs[0] == null || locs[1] == null) {
            p.sendMessage(ChatColor.RED + "You need to set both Pos 1 (Left Click) and Pos 2 (Right Click).");
            return false;
        }

        if (!locs[0].getWorld().equals(locs[1].getWorld())) {
            p.sendMessage(ChatColor.RED + "Points must be in the same world!");
            return false;
        }

        
        int minX = Math.min(locs[0].getBlockX(), locs[1].getBlockX());
        int maxX = Math.max(locs[0].getBlockX(), locs[1].getBlockX());
        int minY = Math.min(locs[0].getBlockY(), locs[1].getBlockY());
        int maxY = Math.max(locs[0].getBlockY(), locs[1].getBlockY());
        int minZ = Math.min(locs[0].getBlockZ(), locs[1].getBlockZ());
        int maxZ = Math.max(locs[0].getBlockZ(), locs[1].getBlockZ());

        
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        if (sizeX > 50 || sizeY > 50 || sizeZ > 50) {
            p.sendMessage(ChatColor.RED + "Selection too big! Max size is 50x50x50.");
            p.sendMessage(ChatColor.GRAY + "Your selection: " + sizeX + "x" + sizeY + "x" + sizeZ);
            return false;
        }

        
        Region newRegion = new Region(UUID.randomUUID(), p.getUniqueId(), locs[0].getWorld().getName(), minX, maxX, minY, maxY, minZ, maxZ);
        if (isOverlapping(newRegion)) {
            p.sendMessage(ChatColor.RED + "This selection overlaps with another player's region!");
            return false;
        }

        
        regions.add(newRegion);
        saveRegion(newRegion);
        selections.remove(p.getUniqueId()); 

        p.sendMessage(ChatColor.GREEN + "§lRegion Claimed Successfully!");
        p.sendMessage(ChatColor.YELLOW + "Your area is now protected.");
        return true;
    }

    

    public boolean canInteract(Player p, Location loc) {
        for (Region r : regions) {
            if (r.contains(loc)) {
                if (r.owner.equals(p.getUniqueId())) return true;
                if (p.hasPermission("smpcore.admin.bypass")) return true;
                if (p.isOp()) return true;

                p.sendMessage(ChatColor.RED + "This land is owned by " + Bukkit.getOfflinePlayer(r.owner).getName());
                return false;
            }
        }
        return true;
    }

    public boolean isClaimed(Location loc) {
        for (Region r : regions) {
            if (r.contains(loc)) return true;
        }
        return false;
    }

    

    private boolean isOverlapping(Region newR) {
        for (Region r : regions) {
            if (!r.world.equals(newR.world)) continue;
            
            if (newR.maxX >= r.minX && newR.minX <= r.maxX &&
                    newR.maxY >= r.minY && newR.minY <= r.maxY &&
                    newR.maxZ >= r.minZ && newR.minZ <= r.maxZ) {
                return true;
            }
        }
        return false;
    }

    private void loadRegions() {
        file = new File(plugin.getDataFolder(), "regions.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) {}
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("regions")) {
            for (String key : config.getConfigurationSection("regions").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    UUID owner = UUID.fromString(config.getString("regions." + key + ".owner"));
                    String world = config.getString("regions." + key + ".world");
                    int minX = config.getInt("regions." + key + ".minX");
                    int maxX = config.getInt("regions." + key + ".maxX");
                    int minY = config.getInt("regions." + key + ".minY");
                    int maxY = config.getInt("regions." + key + ".maxY");
                    int minZ = config.getInt("regions." + key + ".minZ");
                    int maxZ = config.getInt("regions." + key + ".maxZ");
                    regions.add(new Region(id, owner, world, minX, maxX, minY, maxY, minZ, maxZ));
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    private void saveRegion(Region r) {
        String path = "regions." + r.id.toString();
        config.set(path + ".owner", r.owner.toString());
        config.set(path + ".world", r.world);
        config.set(path + ".minX", r.minX);
        config.set(path + ".maxX", r.maxX);
        config.set(path + ".minY", r.minY);
        config.set(path + ".maxY", r.maxY);
        config.set(path + ".minZ", r.minZ);
        config.set(path + ".maxZ", r.maxZ);
        try { config.save(file); } catch (IOException e) {}
    }

    private static class Region {
        UUID id;
        UUID owner;
        String world;
        int minX, maxX, minY, maxY, minZ, maxZ;

        public Region(UUID id, UUID owner, String world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
            this.id = id;
            this.owner = owner;
            this.world = world;
            this.minX = minX; this.maxX = maxX;
            this.minY = minY; this.maxY = maxY;
            this.minZ = minZ; this.maxZ = maxZ;
        }

        public boolean contains(Location loc) {
            if (!loc.getWorld().getName().equals(world)) return false;
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
    }
}