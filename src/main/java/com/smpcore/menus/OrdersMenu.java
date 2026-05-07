package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OrdersMenu implements MigelSMPMenu {

    private Inventory inv;
    private OrdersMenu() {}

    public static void open(Player p, OrderManager orderManager) {
        OrdersMenu menu = new OrdersMenu();
        menu.inv = Bukkit.createInventory(menu, 54, "§8§lᴀᴄᴛɪᴠᴇ ᴏʀᴅᴇʀs");

        List<OrderManager.BuyOrder> orders = orderManager.getActiveOrders();

        int slot = 0;
        for (OrderManager.BuyOrder order : orders) {
            if (slot >= 45) break;

            
            

            ItemStack displayItem = new ItemStack(order.getMaterial());
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7Buyer: §f" + order.getBuyerName());
            lore.add("§7Wants: §e" + order.getAmount() + "x");
            lore.add("§7Total Payout: §a$" + String.format("%,.0f", order.getTotalPrice()));
            lore.add(" ");
            lore.add("§eClick to fill this order!");
            lore.add("§0id:" + order.getId().toString()); 

            if (order.getBuyer().equals(p.getUniqueId())) {
                lore.add("§cYou cannot fill your own order");
            } else {
                lore.add("§eClick to Sell items to this order");
            }

            
            
            lore.add("§0id:" + order.getId().toString());

            menu.inv.setItem(slot, ItemBuilder.of(order.getMaterial(), "§a§lBUY ORDER", lore));
            slot++;
        }

        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}