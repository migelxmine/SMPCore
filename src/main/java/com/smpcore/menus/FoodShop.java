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

public class FoodShop implements MigelSMPMenu {
    private Inventory inv;
    private FoodShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        FoodShop menu = new FoodShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.food"));

        ItemStack[] foods = new ItemStack[]{
                MenuUtils.createShopItem(Material.COOKED_BEEF, "§aCooked Beef", priceManager),
                MenuUtils.createShopItem(Material.COOKED_PORKCHOP, "§aCooked Porkchop", priceManager),
                MenuUtils.createShopItem(Material.BREAD, "§aBread", priceManager),
                MenuUtils.createShopItem(Material.BAKED_POTATO, "§aBaked Potato", priceManager),
                MenuUtils.createShopItem(Material.GOLDEN_CARROT, "§eGolden Carrot", priceManager),
                MenuUtils.createShopItem(Material.MELON, "§aMelon", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, foods);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}