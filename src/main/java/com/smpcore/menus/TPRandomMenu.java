package com.smpcore.menus;

import com.smpcore.utils.MenuUtils;
import com.smpcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class TPRandomMenu implements MigelSMPMenu {

    private Inventory inv;

    private TPRandomMenu() {}

    public static void open(Player p) {
        TPRandomMenu menu = new TPRandomMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ");

        
        List<String> overLore = new ArrayList<>();
        overLore.add("§7Explore the surface.");
        overLore.add("§aClick to Warp ➡");
        menu.inv.setItem(11, ItemBuilder.of(Material.GRASS_BLOCK, "§2§lᴏᴠᴇʀᴡᴏʀʟᴅ", overLore));

        
        List<String> netherLore = new ArrayList<>();
        netherLore.add("§7The fiery underworld.");
        netherLore.add("§cClick to Warp ➡");
        menu.inv.setItem(13, ItemBuilder.of(Material.NETHERRACK, "§c§lᴛʜᴇ ɴᴇᴛʜᴇʀ", netherLore));

        
        List<String> endLore = new ArrayList<>();
        endLore.add("§7The void dimension.");
        endLore.add("§5Click to Warp ➡");
        menu.inv.setItem(15, ItemBuilder.of(Material.END_STONE, "§5§lᴛʜᴇ ᴇɴᴅ", endLore));

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}