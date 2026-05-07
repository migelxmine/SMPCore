package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BuildMemoryMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Build Memory - Mode");

        // Vidro de fundo premium
        ItemStack glass = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        // Modo Solo (Treino)
        ItemStack soloBtn = ItemBuilder.of(Material.FEATHER,
                "§a§lSOLO TRAINING",
                "§7Play alone to practice your memory.",
                "",
                "§8» No rewards.",
                "§eClick to start instantly!");
        inv.setItem(11, soloBtn);

        // Modo Competição (Ranked)
        ItemStack rankedBtn = ItemBuilder.of(Material.DIAMOND_SWORD,
                "§c§lCOMPETITION §7(3 Players)",
                "§7Play against 2 other players.",
                "§7The fastest and most accurate wins!",
                "",
                "§8» §aRewards: §fSuper Money & Coins",
                "§eClick to join the queue!");
        inv.setItem(15, rankedBtn);

        p.openInventory(inv);
    }
}