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

public class NetherShop implements MigelSMPMenu {
    private Inventory inv;
    private NetherShop() {}

    public static void open(Player p, PriceManager priceManager, LangManager lang) {
        NetherShop menu = new NetherShop();
        menu.inv = Bukkit.createInventory(menu, 27, lang.getMessage("menus.sub_shops.nether"));

        ItemStack[] itens = new ItemStack[]{
                MenuUtils.createShopItem(Material.BLAZE_ROD, "§eBlaze Rod", priceManager),
                MenuUtils.createShopItem(Material.LAVA_BUCKET, "§6Lava Bucket", priceManager),
                MenuUtils.createShopItem(Material.SPAWNER, "§5Spawner", priceManager),
                MenuUtils.createShopItem(Material.BONE_BLOCK, "§fBone Block", priceManager),
                MenuUtils.createShopItem(Material.NETHER_BRICK, "§4Nether Brick", priceManager),
                MenuUtils.createShopItem(Material.CRIMSON_STEM, "§cCrimson Stem", priceManager)
        };

        MenuUtils.placeOnMiddleRow(menu.inv, itens);
        MenuUtils.addBackButtonIfNotMain(menu.inv, false);
        MenuHistory.setPrevious(p, "MigelSMP - Shop");
        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}