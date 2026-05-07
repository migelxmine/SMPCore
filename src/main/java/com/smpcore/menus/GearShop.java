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
import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import com.smpcore.utils.PriceManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GearShop implements MigelSMPMenu {
    private Inventory inv;
    private GearShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        GearShop menu = new GearShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.gear"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.TOTEM_OF_UNDYING, "§9Totem of Undying", priceManager),
                MenuUtils.createShopItem(Material.GOLDEN_APPLE, "§eGolden Apple", priceManager),
                MenuUtils.createShopItem(Material.EXPERIENCE_BOTTLE, "§9XP Bottles", priceManager),
                ItemBuilder.of(Material.LEATHER_CHESTPLATE, "§dArmor Sets"),
                ItemBuilder.of(Material.ENCHANTED_BOOK, "§dEnchantments"),
                MenuUtils.createShopItem(Material.AMETHYST_SHARD, "§dAmethyst Shard", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}