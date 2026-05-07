package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KillMenu implements MigelSMPMenu {

    private Inventory inv;

    private KillMenu() {}

    public static void open(Player p) {
        KillMenu menu = new KillMenu();

        menu.inv = Bukkit.createInventory(menu, 27, "§c§lᴄᴏɴғɪʀᴍ ᴅᴇᴀᴛʜ");


        menu.inv.setItem(11, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, "§c§lᴄᴀɴᴄᴇʟ", "§7Click to close."));


        menu.inv.setItem(13, ItemBuilder.of(Material.SKELETON_SKULL, "§4§lᴀʀᴇ ʏᴏᴜ sᴜʀᴇ?", "§7This action cannot be undone."));


        menu.inv.setItem(15, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "§a§lᴄᴏɴғɪʀᴍ", "§7Click to die."));

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}