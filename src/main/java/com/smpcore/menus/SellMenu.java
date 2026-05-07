package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.stream.Collectors;

public class SellMenu implements InventoryHolder {

    private Inventory inv;

    @Override
    public Inventory getInventory() { return inv; }

    public static void open(Player p, LangManager lang) {
        SellMenu menu = new SellMenu();
        menu.inv = Bukkit.createInventory(menu, 45, lang.getMessage("menus.sell.title"));

        for (int i = 36; i < 45; i++) {
            menu.inv.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        List<String> howToLore = lang.getMessages().getStringList("menus.sell.buttons.info").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());

        List<String> sellAllLore = lang.getMessages().getStringList("menus.sell.buttons.info-sell").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());

        menu.inv.setItem(39, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, lang.getMessage("menus.sell.buttons.cancel")));
        menu.inv.setItem(40, ItemBuilder.of(Material.HOPPER, lang.getMessage("menus.sell.buttons.how-to"), howToLore.toArray(new String[0])));
        menu.inv.setItem(41, ItemBuilder.of(Material.EMERALD, lang.getMessage("menus.sell.buttons.sell-all"), sellAllLore.toArray(new String[0])));

        p.openInventory(menu.inv);
    }
}