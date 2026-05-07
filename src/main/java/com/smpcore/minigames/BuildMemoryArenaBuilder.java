package com.smpcore.minigames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class BuildMemoryArenaBuilder {

    public static void buildArena(Location center) {
        World w = center.getWorld();
        if (w == null) return;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        // 1. Limpar a área primeiro (uma caixa gigante de ar para apagar jogos anteriores e barreiras)
        for (int x = -30; x <= 30; x++) {
            for (int y = -5; y <= 20; y++) {
                for (int z = -30; z <= 30; z++) {
                    w.getBlockAt(cx + x, cy + y, cz + z).setType(Material.AIR);
                }
            }
        }

        // 2. Construir a Gaiola de Barreiras (Espaçosa para voar à vontade)
        int radius = 22; // As plataformas vão até ao 18, logo 22 dá imenso espaço
        int height = 15; // Teto alto
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= height; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // Coloca barreiras APENAS nas paredes laterais e no teto
                    if (Math.abs(x) == radius || Math.abs(z) == radius || y == height) {
                        w.getBlockAt(cx + x, cy + y, cz + z).setType(Material.BARRIER);
                    }
                }
            }
        }

        // 3. Palco Central (Onde aparece o desafio)
        buildPlatformWithOutline(w, cx, cy, cz, Material.SMOOTH_STONE);

        // 4. Plataformas dos Jogadores
        buildPlatformWithOutline(w, cx, cy, cz - 15, Material.QUARTZ_BLOCK); // Jogador 1 (Norte)
        buildPlatformWithOutline(w, cx, cy, cz + 15, Material.QUARTZ_BLOCK); // Jogador 2 (Sul)
        buildPlatformWithOutline(w, cx + 15, cy, cz, Material.QUARTZ_BLOCK); // Jogador 3 (Este)
    }

    private static void buildPlatformWithOutline(World w, int x, int y, int z, Material floorMat) {
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                if (Math.abs(i) == 3 || Math.abs(j) == 3) {
                    w.getBlockAt(x + i, y, z + j).setType(Material.GRASS_BLOCK);
                } else {
                    w.getBlockAt(x + i, y, z + j).setType(floorMat);
                }
                w.getBlockAt(x + i, y + 1, z + j).setType(Material.AIR);
                w.getBlockAt(x + i, y + 2, z + j).setType(Material.AIR);
            }
        }
    }
}