package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionMenu implements MigelSMPMenu {

    private Inventory inv;

    private AuctionMenu() {}

    public static void open(Player p, AuctionManager manager, AuctionSort sort, AuctionCategory category, String query) {
        AuctionMenu menu = new AuctionMenu();

        
        
        String titlePart = (query == null) ? "" : ": " + query.toUpperCase();
        menu.inv = Bukkit.createInventory(menu, 54, "§8§lᴀʜ (" + sort.name() + ") [" + category.name() + "]" + titlePart);

        List<AuctionItem> items = manager.getSortedAuctions(sort, category, query);

        
        int slot = 0;
        for (AuctionItem auction : items) {
            if (slot >= 45) break;

            ItemStack displayItem = auction.getItem().clone();
            ItemMeta meta = displayItem.getItemMeta();

            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            String sellerName = Bukkit.getOfflinePlayer(auction.getSeller()).getName();

            lore.add(" ");
            lore.add("§7Seller: §9" + (sellerName != null ? sellerName : "Unknown"));
            lore.add("§7Price: §a$" + String.format("%,.2f", auction.getPrice()));

            if (auction.getSeller().equals(p.getUniqueId())) {
                lore.add(" ");
                lore.add("§cClick to CANCEL listing");
            } else {
                lore.add(" ");
                lore.add("§eClick to BUY");
            }

            lore.add("§0id:" + auction.getId().toString());

            meta.setLore(lore);
            displayItem.setItemMeta(meta);
            menu.inv.setItem(slot++, displayItem);
        }

        
        ItemStack glass = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) {
            menu.inv.setItem(i, glass);
        }

        

        
        menu.inv.setItem(49, ItemBuilder.of(Material.ANVIL, "§e§lʀᴇғʀᴇsʜ", "§7Click to update list"));

        
        List<String> catLore = new ArrayList<>();
        catLore.add("§7Current: §9" + category.getName());
        catLore.add(" ");
        catLore.add("§eClick to change category");
        menu.inv.setItem(51, ItemBuilder.of(Material.COMPARATOR, "§9§lᴄᴀᴛᴇɢᴏʀʏ", catLore));

        
        List<String> sortLore = new ArrayList<>();
        sortLore.add("§7Current: §f" + sort.getName());
        sortLore.add(" ");
        sortLore.add("§eClick to change sort order");
        menu.inv.setItem(53, ItemBuilder.of(Material.HOPPER, "§6§lsᴏʀᴛ ғɪʟᴛᴇʀ", sortLore));

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}