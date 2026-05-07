package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.AuctionMenu;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AuctionCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final AuctionManager auctionManager;
    private final EconomyManager eco;
    private final LangManager lang;

    public AuctionCommand(SMPCore plugin, AuctionManager auctionManager, EconomyManager eco, LangManager lang) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
        this.eco = eco;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            AuctionMenu.open(p, auctionManager, AuctionSort.NEWEST, AuctionCategory.ALL, null);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                p.sendMessage("§cUsage: /ah sell <price>");
                return true;
            }

            ItemStack item = p.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                p.sendMessage("§cYou must hold an item in your main hand to sell it.");
                Sounds.playError(p);
                return true;
            }

            try {
                double price = Double.parseDouble(args[1]);
                if (price <= 0) {
                    p.sendMessage("§cThe price must be greater than 0.");
                    Sounds.playError(p);
                    return true;
                }

                openConfirmSellMenu(p, item, price);

            } catch (NumberFormatException e) {
                p.sendMessage("§cInvalid price! Please enter a valid number.");
                Sounds.playError(p);
            }
            return true;
        }

        String query = args[0];
        AuctionMenu.open(p, auctionManager, AuctionSort.NEWEST, AuctionCategory.ALL, query);
        return true;
    }

    private void openConfirmSellMenu(Player p, ItemStack item, double price) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lᴄᴏɴғɪʀᴍ: §a$" + String.format("%,.0f", price));

        MenuUtils.fillBorders(inv, Material.BLACK_STAINED_GLASS_PANE);


        inv.setItem(11, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, "§c§lᴄᴀɴᴄᴇʟ", "§7Cancel listing"));


        ItemStack display = item.clone();
        ItemMeta meta = display.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7Selling for: §a$" + String.format("%,.0f", price));
        meta.setLore(lore);
        display.setItemMeta(meta);
        inv.setItem(13, display);


        inv.setItem(15, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "§a§lᴄᴏɴғɪʀᴍ", "§7List item for $" + String.format("%,.0f", price)));

        p.openInventory(inv);
    }
}