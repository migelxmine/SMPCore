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

public class OverworldShop implements MigelSMPMenu {
    private Inventory inv;
    private OverworldShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        OverworldShop menu = new OverworldShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.overworld"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.OAK_LOG, "§6Oak Log", priceManager),
                MenuUtils.createShopItem(Material.GRASS_BLOCK, "§aGrass Block", priceManager),
                MenuUtils.createShopItem(Material.COBBLESTONE, "§7Cobblestone", priceManager),
                MenuUtils.createShopItem(Material.ARMOR_STAND, "§eArmor Stand", priceManager),
                MenuUtils.createShopItem(Material.WHITE_BANNER, "§fWhite Banner", priceManager),
                MenuUtils.createShopItem(Material.OAK_BOAT, "§6Oak Boat", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}