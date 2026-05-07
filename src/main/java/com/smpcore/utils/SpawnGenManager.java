package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

public class SpawnGenManager {

    private final SMPCore plugin;
    private World spawnWorld;
    private World lobbyWorld;
    private static final String SPAWN_WORLD_NAME = "SMP_Spawn";
    private static final String LOBBY_WORLD_NAME = "SMP_Lobby";

    public SpawnGenManager(SMPCore plugin) {
        this.plugin = plugin;

        // 1. Extrai os mapas do ZIP (se não existirem)
        MapInstaller.installMaps(plugin);

        // 2. Carrega os mundos para a RAM
        loadSpawnWorld();
        loadLobbyWorld();
    }

    private void loadSpawnWorld() {
        plugin.getLogger().info("§e[SpawnGen] Loading world: " + SPAWN_WORLD_NAME + "...");
        this.spawnWorld = Bukkit.getWorld(SPAWN_WORLD_NAME);

        if (this.spawnWorld == null) {
            WorldCreator creator = new WorldCreator(SPAWN_WORLD_NAME);
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);
            creator.generator(new VoidChunkGenerator());
            this.spawnWorld = Bukkit.createWorld(creator);
        }

        if (this.spawnWorld != null) {
            setupWorldSettings(this.spawnWorld, 895, 167, 736);
        }
    }

    private void loadLobbyWorld() {
        plugin.getLogger().info("§e[SpawnGen] Loading world: " + LOBBY_WORLD_NAME + "...");
        this.lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);

        if (this.lobbyWorld == null) {
            WorldCreator creator = new WorldCreator(LOBBY_WORLD_NAME);
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);
            creator.generator(new VoidChunkGenerator());
            this.lobbyWorld = Bukkit.createWorld(creator);
        }

        if (this.lobbyWorld != null) {
            // As tuas coordenadas de Spawn do Lobby: 15, 3, -13
            setupWorldSettings(this.lobbyWorld, 15, 3, -13);
        }
    }

    public void setupSpawnWorld() {
        if (spawnWorld == null) loadSpawnWorld();
        setupWorldSettings(this.spawnWorld, 895, 167, 736);
    }

    public World getSpawnWorld() {
        if (spawnWorld == null) loadSpawnWorld();
        return spawnWorld;
    }

    public World getLobbyWorld() {
        if (lobbyWorld == null) loadLobbyWorld();
        return lobbyWorld;
    }

    private void setupWorldSettings(World world, int x, int y, int z) {
        if (world == null) return;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setTime(6000);
        world.setStorm(false);
        world.setSpawnLocation(x, y, z);
    }

    public void teleportToSpawn(Player p) {
        if (spawnWorld != null) {
            Location loc = new Location(spawnWorld, 895.5, 167, 736.5, 90, 0);
            p.teleport(loc);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        } else {
            p.sendMessage("§cSpawn world is still loading...");
            loadSpawnWorld();
        }
    }

    public void placeHead(Location loc, SkinData skin, BlockFace rotation) {
        if (loc.getWorld() == null) return;
        Block block = loc.getBlock();
        block.setType(Material.PLAYER_HEAD);

        if (block.getBlockData() instanceof Rotatable) {
            Rotatable data = (Rotatable) block.getBlockData();
            data.setRotation(rotation);
            block.setBlockData(data);
        }

        if (block.getState() instanceof Skull) {
            Skull skull = (Skull) block.getState();
            skull.setOwnerProfile(skin.getProfile());
            skull.update(true);
        }
    }
}