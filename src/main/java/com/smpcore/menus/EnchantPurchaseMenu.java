package com.smpcore.menus;

import com.smpcore.utils.EnchantmentPriceManager;
import com.smpcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnchantPurchaseMenu implements MigelSMPMenu {

    private Inventory inv;

    public static void open(Player player, Enchantment enchantment, EnchantmentPriceManager priceManager) {
        EnchantPurchaseMenu menu = new EnchantPurchaseMenu();
        String title = ChatColor.DARK_BLUE + "Comprar - " + enchantment.getKey().getKey();
        
        if (title.length() > 32) title = title.substring(0, 32);

        menu.inv = Bukkit.createInventory(menu, 27, title);

        for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
            double price = priceManager.getPrice(enchantment, i);
            if (price < 0) continue;

            String romanLevel = toRoman(i);
            menu.inv.addItem(ItemBuilder.of(Material.ENCHANTED_BOOK,
                    "§aComprar Nível " + romanLevel,
                    "§7Preço: §e$" + String.format("%,.2f", price)));
        }
        player.openInventory(menu.inv);
    }

    private static String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);
        switch (number) {
            case 1: return "I"; case 2: return "II"; case 3: return "III";
            case 4: return "IV"; case 5: return "V";
            default: return String.valueOf(number);
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}