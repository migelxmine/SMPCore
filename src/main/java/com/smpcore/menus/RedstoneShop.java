package com.smpcore.menus;

import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import com.smpcore.utils.PriceManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RedstoneShop implements MigelSMPMenu {
    private Inventory inv;
    private RedstoneShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        RedstoneShop menu = new RedstoneShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.redstone"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.REDSTONE, "§cRedstone", priceManager),
                MenuUtils.createShopItem(Material.REDSTONE_TORCH, "§cRedstone Torch", priceManager),
                MenuUtils.createShopItem(Material.LEVER, "§7Lever", priceManager),
                MenuUtils.createShopItem(Material.REPEATER, "§7Repeater", priceManager),
                MenuUtils.createShopItem(Material.PISTON, "§7Piston", priceManager),
                MenuUtils.createShopItem(Material.OBSERVER, "§7Observer", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}