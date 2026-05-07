package com.smpcore.minigames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class IceControlArenaBuilder {

    public static void buildArena(Location center) {
        World w = center.getWorld();
        if (w == null) return;

        int radius = 15;
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        // Constrói o chão e as paredes
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    // Chão (Mistura de Blue Ice e Packed Ice para escorregar ao máximo)
                    Material iceType = (Math.random() > 0.3) ? Material.BLUE_ICE : Material.PACKED_ICE;

                    // Adiciona buracos mortais aleatórios longe do centro
                    if (Math.random() < 0.04 && (Math.abs(x) > 4 || Math.abs(z) > 4)) {
                        iceType = Material.AIR;
                    }

                    w.getBlockAt(cx + x, cy, cz + z).setType(iceType);

                    // Paredes de vidro Premium nas bordas
                    if (x * x + z * z >= (radius - 1) * (radius - 1) && iceType != Material.AIR) {
                        w.getBlockAt(cx + x, cy + 1, cz + z).setType(Material.LIGHT_BLUE_STAINED_GLASS);
                        w.getBlockAt(cx + x, cy + 2, cz + z).setType(Material.LIGHT_BLUE_STAINED_GLASS);
                    } else {
                        // Limpa o ar para garantir que ninguém sufoca
                        w.getBlockAt(cx + x, cy + 1, cz + z).setType(Material.AIR);
                        w.getBlockAt(cx + x, cy + 2, cz + z).setType(Material.AIR);
                        w.getBlockAt(cx + x, cy + 3, cz + z).setType(Material.AIR);
                    }
                }
            }
        }
    }
}