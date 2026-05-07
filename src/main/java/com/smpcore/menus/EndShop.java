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

public class EndShop implements MigelSMPMenu {
    private Inventory inv;
    private EndShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        EndShop menu = new EndShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.end"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.ENDER_PEARL, "§2Ender Pearl", priceManager),
                MenuUtils.createShopItem(Material.END_STONE, "§eEnd Stone", priceManager),
                MenuUtils.createShopItem(Material.OBSIDIAN, "§5Obsidian", priceManager),
                MenuUtils.createShopItem(Material.ELYTRA, "§dElytra", priceManager),
                MenuUtils.createShopItem(Material.CHORUS_FRUIT, "§5Chorus Fruit", priceManager),
                MenuUtils.createShopItem(Material.PURPUR_BLOCK, "§dPurpur Block", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}