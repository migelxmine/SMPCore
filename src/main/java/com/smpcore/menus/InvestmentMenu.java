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

public class InvestmentMenu implements MigelSMPMenu {

    private Inventory inv;

    private InvestmentMenu() {}

    public static void open(Player p, CryptoManager crypto) {
        InvestmentMenu menu = new InvestmentMenu();
        
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lᴄʀʏᴘᴛᴏ ᴍᴀʀᴋᴇᴛ");

        
        
        double totalValue = 0;
        for (String key : crypto.getCoinNames()) {
            totalValue += (crypto.getPlayerCoinBalance(p, key) * crypto.getPrice(key));
        }

        List<String> portfolioLore = new ArrayList<>();
        portfolioLore.add("§7Total Asset Value:");
        portfolioLore.add("§9$" + String.format("%,.2f", totalValue));
        portfolioLore.add(" ");
        portfolioLore.add("§eThis is the combined value");
        portfolioLore.add("§eof all your crypto.");

        menu.inv.setItem(4, ItemBuilder.of(Material.ENDER_CHEST, "§d§lʏᴏᴜʀ ᴘᴏʀᴛғᴏʟɪᴏ", portfolioLore));

        
        
        int[] slots = {20, 22, 24};
        Material[] icons = {Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.AMETHYST_BLOCK}; 
        int index = 0;

        for (String coinKey : crypto.getCoinNames()) {
            if (index >= slots.length) break;

            String displayName = crypto.getCoinDisplayName(coinKey);
            double price = crypto.getPrice(coinKey);
            double playerBal = crypto.getPlayerCoinBalance(p, coinKey);

            Material icon = icons[index % icons.length]; 

            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6§l" + displayName.toUpperCase()); 

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7Current Price:");
            lore.add("§a$" + String.format("%,.2f", price));
            lore.add(" ");
            lore.add("§7You Own:");
            lore.add("§e" + String.format("%.4f", playerBal) + " " + displayName);
            lore.add("§7Value: §f$" + String.format("%,.2f", playerBal * price));
            lore.add(" ");
            lore.add("§eClick to Trade ➡");

            meta.setLore(lore);
            item.setItemMeta(meta);

            menu.inv.setItem(slots[index], item);
            index++;
        }

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);

        
        menu.inv.setItem(40, ItemBuilder.of(Material.SUNFLOWER, "§e§lʀᴇғʀᴇsʜ ᴍᴀʀᴋᴇᴛ", "§7Click to update prices."));

        
        
        MenuHistory.setPrevious(p, "BankMenu"); 

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}