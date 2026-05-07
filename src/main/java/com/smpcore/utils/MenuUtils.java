package com.smpcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {

    /**
     * Preenche as bordas do menu com vidro colorido
     */
    public static void fillBorders(Inventory inv, Material borderMaterial) {
        ItemStack border = ItemBuilder.of(borderMaterial, " ");
        int size = inv.getSize();
        int rows = size / 9;

        
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }

        
        for (int i = size - 9; i < size; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }

        
        for (int i = 0; i < rows; i++) {
            int left = i * 9;
            int right = (i * 9) + 8;
            if (inv.getItem(left) == null) inv.setItem(left, border);
            if (inv.getItem(right) == null) inv.setItem(right, border);
        }
    }

    /**
     * Adiciona o botão de voltar (Barreira) no CANTO INFERIOR ESQUERDO.
     */
    public static void addBackButton(Inventory inv) {
        
        int slot = inv.getSize() - 9;
        inv.setItem(slot, ItemBuilder.of(Material.BARRIER, "§cʙᴀᴄᴋ"));
    }

    public static void addBackButtonIfNotMain(Inventory inv, boolean isMain) {
        if (!isMain) {
            addBackButton(inv);
        }
    }

    /**
     * Método para criar itens das lojas
     */
    public static ItemStack createShopItem(Material material, String name, PriceManager priceManager) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lore = new ArrayList<>();
        if (priceManager.hasPrice(material)) {
            double buyPrice = priceManager.getPrice(material);
            lore.add(" ");
            lore.add("§aBuy Price: §e$" + String.format("%,.2f", buyPrice));
            lore.add("§7Click to buy!");
        } else {
            lore.add(" ");
            lore.add("§cNot for sale");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Método para centrar itens no menu
     */
    public static void placeOnMiddleRow(Inventory inv, ItemStack[] items) {
        if (items == null || items.length == 0) return;

        int size = inv.getSize();
        int rows = size / 9;

        if (rows < 3) {
            for (int i = 0; i < items.length; i++) {
                if (i < size) inv.setItem(i, items[i]);
            }
            return;
        }

        int startSlot = 10;
        if (items.length <= 7) {
            int offset = (7 - items.length) / 2;
            startSlot += offset;
        }

        for (int i = 0; i < items.length; i++) {
            if (startSlot + i < 17) {
                inv.setItem(startSlot + i, items[i]);
            }
        }
    }
}