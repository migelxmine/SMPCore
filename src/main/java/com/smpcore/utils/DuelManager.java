package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DuelManager {

    private final SMPCore plugin;
    private final Set<UUID> duelQueue = new HashSet<>();

    
    private Location duelArena;
    private Location trainArena;

    public DuelManager(SMPCore plugin) {
        this.plugin = plugin;
        loadLocations();
    }

    

    public void joinQueue(Player p) {
        if (duelQueue.contains(p.getUniqueId())) {
            duelQueue.remove(p.getUniqueId());
            p.sendMessage("§cYou left the Duel Queue.");
            return;
        }

        duelQueue.add(p.getUniqueId());
        p.sendMessage("§aYou joined the Duel Queue! §7Waiting for opponent...");

        checkQueue();
    }

    private void checkQueue() {
        if (duelQueue.size() >= 2) {
            
            UUID[] players = duelQueue.toArray(new UUID[0]);
            Player p1 = Bukkit.getPlayer(players[0]);
            Player p2 = Bukkit.getPlayer(players[1]);

            if (p1 != null && p2 != null) {
                startDuel(p1, p2);
            }

            
            duelQueue.remove(players[0]);
            duelQueue.remove(players[1]);
        }
    }

    private void startDuel(Player p1, Player p2) {
        if (duelArena == null) {
            p1.sendMessage("§cDuel Arena not set! Contact Admin.");
            p2.sendMessage("§cDuel Arena not set! Contact Admin.");
            return;
        }

        p1.teleport(duelArena);
        p2.teleport(duelArena);

        giveCrystalKit(p1);
        giveCrystalKit(p2);

        p1.sendMessage("§c§lDUEL STARTED! §7Opponent: " + p2.getName());
        p2.sendMessage("§c§lDUEL STARTED! §7Opponent: " + p1.getName());

        Sounds.playAnvil(p1);
        Sounds.playAnvil(p2);
    }

    

    public void startTraining(Player p) {
        if (trainArena == null) {
            p.sendMessage("§cTrain Arena not set! Contact Admin.");
            return;
        }

        p.teleport(trainArena);
        giveCrystalKit(p);

        
        Zombie bot = (Zombie) p.getWorld().spawnEntity(trainArena.clone().add(3, 0, 0), EntityType.ZOMBIE);
        bot.setCustomName("§c§lPvP Bot v1");
        bot.setCustomNameVisible(true);
        bot.setAI(true); 

        
        bot.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        bot.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        bot.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        bot.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        bot.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

        p.sendMessage("§aTraining Started! §7Practice your crystals on the bot.");
    }

    

    private void giveCrystalKit(Player p) {
        p.getInventory().clear();
        p.getInventory().addItem(new ItemStack(Material.NETHERITE_SWORD));
        p.getInventory().addItem(new ItemStack(Material.END_CRYSTAL, 64));
        p.getInventory().addItem(new ItemStack(Material.OBSIDIAN, 64));
        p.getInventory().addItem(new ItemStack(Material.RESPAWN_ANCHOR, 64));
        p.getInventory().addItem(new ItemStack(Material.GLOWSTONE, 64));
        p.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
        
        p.getInventory().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));

        p.sendMessage("§9Crystal PvP Kit Received!");
    }

    
    public void setDuelSpawn(Location loc) {
        this.duelArena = loc;
        saveLocation("duel", loc);
    }

    public void setTrainSpawn(Location loc) {
        this.trainArena = loc;
        saveLocation("train", loc);
    }

    private void saveLocation(String name, Location loc) {
        plugin.getConfig().set("locations." + name + ".world", loc.getWorld().getName());
        plugin.getConfig().set("locations." + name + ".x", loc.getX());
        plugin.getConfig().set("locations." + name + ".y", loc.getY());
        plugin.getConfig().set("locations." + name + ".z", loc.getZ());
        plugin.getConfig().set("locations." + name + ".yaw", loc.getYaw());
        plugin.getConfig().set("locations." + name + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }

    private void loadLocations() {
        FileConfiguration cfg = plugin.getConfig();
        if (cfg.contains("locations.duel")) {
            duelArena = new Location(
                    Bukkit.getWorld(cfg.getString("locations.duel.world")),
                    cfg.getDouble("locations.duel.x"),
                    cfg.getDouble("locations.duel.y"),
                    cfg.getDouble("locations.duel.z"),
                    (float) cfg.getDouble("locations.duel.yaw"),
                    (float) cfg.getDouble("locations.duel.pitch")
            );
        }
        if (cfg.contains("locations.train")) {
            trainArena = new Location(
                    Bukkit.getWorld(cfg.getString("locations.train.world")),
                    cfg.getDouble("locations.train.x"),
                    cfg.getDouble("locations.train.y"),
                    cfg.getDouble("locations.train.z"),
                    (float) cfg.getDouble("locations.train.yaw"),
                    (float) cfg.getDouble("locations.train.pitch")
            );
        }
    }
}