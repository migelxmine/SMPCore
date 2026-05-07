package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import com.smpcore.utils.TprManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TeleportMenu implements MigelSMPMenu {

    private Inventory inv;

    private TeleportMenu() {}

    public static void open(Player p, TprManager tprManager, LangManager lang) {
        TeleportMenu menu = new TeleportMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lᴛᴇʟᴇᴘᴏʀᴛs");

        
        menu.inv.setItem(11, ItemBuilder.of(Material.RED_BED, "§c§lʜᴏᴍᴇs", "§7Manage your homes."));

        
        menu.inv.setItem(13, ItemBuilder.of(Material.ENDER_EYE, "§a§lʀᴀɴᴅᴏᴍ ᴛᴘ", "§7Teleport to the wild."));

        
        boolean isTprOn = tprManager.isTprEnabled(p);
        String status = isTprOn ? "§a§lᴏɴ" : "§c§lᴏғғ";
        Material icon = isTprOn ? Material.LIME_DYE : Material.GRAY_DYE;

        menu.inv.setItem(15, ItemBuilder.of(icon, "§e§lᴛᴘ ʀᴇǫᴜᴇsᴛs: " + status, "§7Click to toggle."));

        
        MenuUtils.fillBorders(menu.inv, Material.GRAY_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);
        MenuHistory.setPrevious(p, "MainMenu");

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}