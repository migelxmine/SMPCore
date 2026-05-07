package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class StaffMenu {

    // ECRÃ 1: O Dashboard Principal
    public static void openMainMenu(Player p, SMPCore plugin) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lS T A F F   P A N E L");

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inv.setItem(11, ItemBuilder.of(Material.PAPER, "§e§lSERVER STATUS", "§7Click to view live", "§7server statistics."));
        inv.setItem(13, ItemBuilder.of(Material.PLAYER_HEAD, "§a§lMANAGE PLAYERS", "§7Click to view and manage", "§7all online players."));
        inv.setItem(15, ItemBuilder.of(Material.COMMAND_BLOCK, "§c§lGLOBAL ECONOMY", "§7Click to freeze the", "§7entire server economy."));

        p.openInventory(inv);
    }

    // ECRÃ 2: A Lista de Jogadores Online
    public static void openPlayerList(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§lO N L I N E   P L A Y E R S");

        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName("§a" + target.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to open the");
                lore.add("§7management panel for");
                lore.add("§7this player.");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(slot++, head);
        }

        p.openInventory(inv);
    }

    // ECRÃ 3: As Ações para um Jogador Específico
    public static void openPlayerActions(Player p, Player target, SMPCore plugin) {
        Inventory inv = Bukkit.createInventory(null, 36, "§8§lM A N A G E: §f" + target.getName());

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inv.setItem(10, ItemBuilder.of(Material.IRON_BARS, "§c§lARREST", "§7Teleports player to a", "§7bedrock cage in the sky."));
        inv.setItem(11, ItemBuilder.of(Material.SLIME_BALL, "§a§lRELEASE", "§7Releases player to spawn."));
        inv.setItem(12, ItemBuilder.of(Material.REDSTONE_BLOCK, "§4§lFAKE BAN", "§7Kicks the player with a", "§7permanent ban message."));

        boolean menuBanned = plugin.isMenuBanned(target.getUniqueId());
        String banName = menuBanned ? "§a§lUNBAN FROM MENUS" : "§c§lBAN FROM MENUS";

        inv.setItem(14, ItemBuilder.of(Material.BARRIER, banName, "§7Toggles the player's", "§7access to SMP menus."));
        inv.setItem(15, ItemBuilder.of(Material.ICE, "§b§lFREEZE ECONOMY", "§7Freezes their bank account."));
        inv.setItem(16, ItemBuilder.of(Material.RED_STAINED_GLASS, "§4§lBAN ECONOMY", "§7Permanent Economy Ban."));
        inv.setItem(31, ItemBuilder.of(Material.ARROW, "§c§lBACK", "§7Return to player list."));

        p.openInventory(inv);
    }
}