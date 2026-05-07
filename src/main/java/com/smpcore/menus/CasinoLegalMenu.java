package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CasinoLegalMenu implements MigelSMPMenu {

    private Inventory inv;

    private CasinoLegalMenu() {}

    public static void open(Player p) {
        CasinoLegalMenu menu = new CasinoLegalMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§l⚠️ ʟᴇɢᴀʟ ɴᴏᴛɪᴄᴇ ⚠️");

        
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Please read carefully:");
        infoLore.add(" ");
        infoLore.add("§71. This casino uses §eVirtual Currency §7only.");
        infoLore.add("§72. This currency bas §cNO Real World Value§7.");
        infoLore.add("§73. You cannot cash out for real money.");
        infoLore.add("§74. This is for entertainment purposes only.");
        infoLore.add(" ");
        infoLore.add("§eBy playing, you accept these terms.");

        menu.inv.setItem(13, ItemBuilder.of(Material.PAPER, "§e§lCASINO TERMS", infoLore));

        
        List<String> acceptLore = new ArrayList<>();
        acceptLore.add("§7I understand that this is");
        acceptLore.add("§7virtual money and a simulation.");
        acceptLore.add(" ");
        acceptLore.add("§aClick to Accept & Play");
        menu.inv.setItem(15, ItemBuilder.of(Material.LIME_CONCRETE, "§a§lI ACCEPT", acceptLore));

        
        List<String> denyLore = new ArrayList<>();
        denyLore.add("§7I do not wish to play.");
        denyLore.add(" ");
        denyLore.add("§cClick to Leave");
        menu.inv.setItem(11, ItemBuilder.of(Material.RED_CONCRETE, "§c§lI DECLINE", denyLore));

        
        MenuUtils.fillBorders(menu.inv, Material.RED_STAINED_GLASS_PANE);

        p.openInventory(menu.inv);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10f, 0.5f); 
    }

    @Override
    public Inventory getInventory() { return inv; }
}