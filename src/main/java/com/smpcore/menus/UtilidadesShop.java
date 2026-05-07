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

public class UtilidadesShop implements MigelSMPMenu {
    private Inventory inv;
    private UtilidadesShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        UtilidadesShop menu = new UtilidadesShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.utils"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.ARMOR_STAND, "§eArmor Stand", priceManager),
                MenuUtils.createShopItem(Material.WHITE_BANNER, "§fWhite Banner", priceManager),
                MenuUtils.createShopItem(Material.OAK_BOAT, "§6Oak Boat", priceManager),
                MenuUtils.createShopItem(Material.ITEM_FRAME, "§eItem Frame", priceManager),
                MenuUtils.createShopItem(Material.COMPASS, "§cCompass", priceManager),
                MenuUtils.createShopItem(Material.SPYGLASS, "§eSpyglass", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}