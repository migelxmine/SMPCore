package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationMenu implements MigelSMPMenu {

    private Inventory inv;
    private final Material material;
    private final double unitPrice;
    private final PriceManager priceManager;
    private final EconomyManager eco;
    private final LangManager lang;

    public ConfirmationMenu(PriceManager priceManager, EconomyManager eco, LangManager lang, Material material, String categoryName) {
        this.priceManager = priceManager;
        this.eco = eco;
        this.lang = lang;
        this.material = material;
        
        this.unitPrice = priceManager.getPrice(material);
        this.inv = Bukkit.createInventory(this, 27, "Confirm: " + ChatColor.stripColor(categoryName));
        setupMenu();
    }

    private void setupMenu() {
        MenuUtils.fillBorders(inv, Material.GRAY_STAINED_GLASS_PANE);

        ItemStack item = new ItemStack(material);
        String trend = priceManager.getTrendSymbol(material);

        List<String> infoLore = new ArrayList<>();
        infoLore.add(" ");
        infoLore.add("§7Current Price: §e$" + String.format("%,.2f", unitPrice) + " " + trend);
        if (trend.contains("📈")) infoLore.add("§c(High Demand)");
        if (trend.contains("📉")) infoLore.add("§a(Low Price!)");

        inv.setItem(13, ItemBuilder.of(material, "§b§l" + material.name(), infoLore));


        inv.setItem(11, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, "§c§lᴄᴀɴᴄᴇʟ"));


        inv.setItem(14, createBuyButton(1));
        inv.setItem(15, createBuyButton(16));
        inv.setItem(16, createBuyButton(64));
    }

    private ItemStack createBuyButton(int amount) {
        double total = unitPrice * amount;
        return ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE,
                "§a§lʙᴜʏ " + amount,
                "§7Cost: §e$" + String.format("%,.2f", total));
    }

    public void open(Player p) {
        p.openInventory(inv);
    }

    public boolean handleClick(Player p, ItemStack clicked) {
        if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            p.closeInventory();
            return true;
        }

        if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            int amount = Integer.parseInt(name.replace("BUY ", "").replace("ʙᴜʏ ", ""));
            double cost = unitPrice * amount;

            if (eco.has(p, cost)) {
                if (hasSpace(p, amount)) {
                    eco.withdraw(p, cost);
                    p.getInventory().addItem(new ItemStack(material, amount));

                    
                    
                    priceManager.adjustPrice(material, amount, true);

                    p.sendMessage(ChatColor.GREEN + "Bought " + amount + "x " + material.name());
                    Sounds.playSuccess(p);
                    p.closeInventory();
                    return true;
                } else {
                    p.sendMessage(ChatColor.RED + "Inventory full!");
                    Sounds.playError(p);
                }
            } else {
                p.sendMessage(ChatColor.RED + "Insufficient funds!");
                Sounds.playError(p);
            }
        }
        return false;
    }

    private boolean hasSpace(Player p, int amount) {
        int freeSpace = 0;
        for (ItemStack i : p.getInventory().getStorageContents()) {
            if (i == null || i.getType() == Material.AIR) {
                freeSpace += material.getMaxStackSize();
            } else if (i.getType() == material && i.getAmount() < material.getMaxStackSize()) {
                freeSpace += material.getMaxStackSize() - i.getAmount();
            }
        }
        return freeSpace >= amount;
    }

    @Override
    public Inventory getInventory() { return inv; }
}