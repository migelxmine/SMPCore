/*
 * Copyright (c) 2025 Miguel Martinho Morbey Rodrigues Moreira (MigelSMP/SMPCore)
 * All Rights Reserved.
 *
 * Este software é propriedade confidencial e proprietária de Miguel Martinho Morbey Rodrigues Moreira.
 * ("Informação Confidencial"). Você não deve divulgar tal Informação
 * Confidencial e deve usá-la apenas de acordo com os termos do
 * contrato de licença que você celebrou com Miguel Martinho Morbey Rodrigues Moreira.
 *
 * É ESTRITAMENTE PROIBIDO DESCOMPILAR, MODIFICAR OU REDISTRIBUIR ESTE SOFTWARE.
 */
package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import com.smpcore.utils.PriceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SetsMenu implements MigelSMPMenu {

    private Inventory inv;

    public static void open(Player p, PriceManager priceManager) {
        SetsMenu menu = new SetsMenu();
        menu.inv = Bukkit.createInventory(menu, 45, ChatColor.DARK_PURPLE + "Sets");

        ItemStack[] ironRow = new ItemStack[]{
                ItemBuilder.of(Material.IRON_INGOT, "§aBUY ALL - IRON"),
                MenuUtils.createShopItem(Material.IRON_HELMET, "§fIron Helmet", priceManager),
                MenuUtils.createShopItem(Material.IRON_CHESTPLATE, "§fIron Chestplate", priceManager),
                MenuUtils.createShopItem(Material.IRON_LEGGINGS, "§fIron Leggings", priceManager),
                MenuUtils.createShopItem(Material.IRON_BOOTS, "§fIron Boots", priceManager),
                MenuUtils.createShopItem(Material.IRON_SWORD, "§fIron Sword", priceManager),
                MenuUtils.createShopItem(Material.IRON_PICKAXE, "§fIron Pickaxe", priceManager),
                MenuUtils.createShopItem(Material.IRON_AXE, "§fIron Axe", priceManager),
                MenuUtils.createShopItem(Material.IRON_SHOVEL, "§fIron Shovel", priceManager)
        };
        ItemStack[] diamondRow = new ItemStack[]{
                ItemBuilder.of(Material.DIAMOND, "§aBUY ALL - DIAMOND"),
                MenuUtils.createShopItem(Material.DIAMOND_HELMET, "§9Diamond Helmet", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_CHESTPLATE, "§9Diamond Chestplate", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_LEGGINGS, "§9Diamond Leggings", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_BOOTS, "§9Diamond Boots", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_SWORD, "§9Diamond Sword", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_PICKAXE, "§9Diamond Pickaxe", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_AXE, "§9Diamond Axe", priceManager),
                MenuUtils.createShopItem(Material.DIAMOND_SHOVEL, "§9Diamond Shovel", priceManager)
        };
        ItemStack[] netherRow = new ItemStack[]{
                ItemBuilder.of(Material.NETHERITE_INGOT, "§aBUY ALL - NETHERITE"),
                MenuUtils.createShopItem(Material.NETHERITE_HELMET, "§8Netherite Helmet", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_CHESTPLATE, "§8Netherite Chestplate", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_LEGGINGS, "§8Netherite Leggings", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_BOOTS, "§8Netherite Boots", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_SWORD, "§8Netherite Sword", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_PICKAXE, "§8Netherite Pickaxe", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_AXE, "§8Netherite Axe", priceManager),
                MenuUtils.createShopItem(Material.NETHERITE_SHOVEL, "§8Netherite Shovel", priceManager)
        };

        int row1Start = 9, row2Start = 18, row3Start = 27;
        for (int i = 0; i < ironRow.length; i++) menu.inv.setItem(row1Start + i, ironRow[i]);
        for (int i = 0; i < diamondRow.length; i++) menu.inv.setItem(row2Start + i, diamondRow[i]);
        for (int i = 0; i < netherRow.length; i++) menu.inv.setItem(row3Start + i, netherRow[i]);

        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "GEAR SHOP");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}