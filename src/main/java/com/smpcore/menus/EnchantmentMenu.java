/*
 * Copyright (c) 2025 [Seu Nome Aqui] (SMPCore)
 * All Rights Reserved.
 */
package com.smpcore.menus;

import com.smpcore.utils.EnchantmentPriceManager;
import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.MenuHistory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class EnchantmentMenu implements MigelSMPMenu {

    private Inventory inv;
    private static final Map<String, Object[]> SECTIONS = new LinkedHashMap<>();

    
    static {
        SECTIONS.put("§9Armor", new Object[]{Material.DIAMOND_CHESTPLATE, Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_FALL, Enchantment.OXYGEN,
                Enchantment.WATER_WORKER, Enchantment.THORNS, Enchantment.DEPTH_STRIDER,
                Enchantment.FROST_WALKER, Enchantment.SOUL_SPEED, Enchantment.SWIFT_SNEAK,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§cSword", new Object[]{Material.DIAMOND_SWORD, Arrays.asList(
                Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS,
                Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.SWEEPING_EDGE,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§7Tools", new Object[]{Material.DIAMOND_PICKAXE, Arrays.asList(
                Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.LOOT_BONUS_BLOCKS,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§6Battle Axe", new Object[]{Material.DIAMOND_AXE, Arrays.asList(
                Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§dBow", new Object[]{Material.BOW, Arrays.asList(
                Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_FIRE, Enchantment.ARROW_INFINITE,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§9Trident", new Object[]{Material.TRIDENT, Arrays.asList(
                Enchantment.IMPALING, Enchantment.LOYALTY, Enchantment.RIPTIDE, Enchantment.CHANNELING,
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
        SECTIONS.put("§fElytra", new Object[]{Material.ELYTRA, Arrays.asList(
                Enchantment.DURABILITY, Enchantment.MENDING
        )});
    }

    
    private EnchantmentMenu() {}

    public static void open(Player player, int page, EnchantmentPriceManager priceManager) {
        EnchantmentMenu menu = new EnchantmentMenu();
        List<ItemStack> itemsToShow = new ArrayList<>();
        ItemStack divider = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE, " ");

        for (Map.Entry<String, Object[]> entry : SECTIONS.entrySet()) {
            String sectionName = entry.getKey();
            Material iconMaterial = (Material) entry.getValue()[0];
            List<Enchantment> enchants = (List<Enchantment>) entry.getValue()[1];

            itemsToShow.add(ItemBuilder.of(iconMaterial, sectionName));
            for (Enchantment enchant : enchants) {
                itemsToShow.add(createEnchantedBook(enchant, priceManager));
            }

            int itemsOnCurrentLine = itemsToShow.size() % 9;
            if (itemsOnCurrentLine != 0) {
                int paddingNeeded = 9 - itemsOnCurrentLine;
                for (int i = 0; i < paddingNeeded; i++) {
                    itemsToShow.add(divider);
                }
            }
        }

        int itemsPerPage = 36;
        int totalPages = (int) Math.ceil((double) itemsToShow.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        String title = String.format("%sLoja de Encantamentos - Pág %d/%d", ChatColor.DARK_PURPLE, page, totalPages);
        menu.inv = Bukkit.createInventory(menu, 54, title);

        for (int i = 0; i < 9; i++) menu.inv.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " "));

        ItemStack bottomSeparator = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) menu.inv.setItem(i, bottomSeparator);
        menu.inv.setItem(49, ItemBuilder.of(Material.BARRIER, "§cBack"));
        MenuHistory.setPrevious(player, "EnchantmentMenu");
        if (page > 1) menu.inv.setItem(48, ItemBuilder.of(Material.ARROW, "§aPrevious Page"));
        if (page < totalPages) menu.inv.setItem(50, ItemBuilder.of(Material.ARROW, "§aNext Page"));

        int startIndex = (page - 1) * itemsPerPage;
        for (int i = 0; i < itemsPerPage; i++) {
            int itemIndex = startIndex + i;
            if (itemIndex < itemsToShow.size()) {
                menu.inv.setItem(9 + i, itemsToShow.get(itemIndex));
            }
        }

        player.openInventory(menu.inv);
    }

    private static ItemStack createEnchantedBook(Enchantment enchantment, EnchantmentPriceManager priceManager) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta != null) {
            meta.addStoredEnchant(enchantment, 1, false);
            List<String> lore = new ArrayList<>();
            for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                double price = priceManager.getPrice(enchantment, i);
                String formattedPrice = (price < 0) ? "§cNot for sale" : "§a$" + String.format("%,.2f", price);
                lore.add(ChatColor.GOLD + "Level " + toRoman(i) + ": " + formattedPrice);
            }
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Click to select level");
            meta.setLore(lore);
            book.setItemMeta(meta);
        }
        return book;
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
    public Inventory getInventory() { return inv; }
}