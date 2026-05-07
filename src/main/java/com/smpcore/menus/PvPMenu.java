package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PvPMenu implements MigelSMPMenu {

    private Inventory inv;

    private PvPMenu() {}

    public static void open(Player p) {
        PvPMenu menu = new PvPMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§4§lᴘᴠᴘ ʜᴜʙ");

        
        List<String> duelLore = new ArrayList<>();
        duelLore.add("§7Queue for Crystal PvP.");
        duelLore.add("§7Fight real players.");
        duelLore.add(" ");
        duelLore.add("§eClick to Join Queue ➡");
        menu.inv.setItem(11, ItemBuilder.of(Material.NETHERITE_SWORD, "§c§l1v1 DUELS", duelLore));

        
        List<String> trainLore = new ArrayList<>();
        trainLore.add("§7Practice your combos.");
        trainLore.add("§7Fight a Bot.");
        trainLore.add(" ");
        trainLore.add("§9Click to Train ➡");
        menu.inv.setItem(13, ItemBuilder.of(Material.END_CRYSTAL, "§9§lTRAINING", trainLore));

        
        List<String> bountyLore = new ArrayList<>();
        bountyLore.add("§7Set a price on a head.");
        bountyLore.add("§7Hunt players for money.");
        bountyLore.add(" ");
        bountyLore.add("§6Click to View ➡");
        menu.inv.setItem(15, ItemBuilder.of(Material.SKELETON_SKULL, "§6§lBOUNTIES", bountyLore));

        
        MenuUtils.fillBorders(menu.inv, Material.RED_STAINED_GLASS_PANE);
        

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}