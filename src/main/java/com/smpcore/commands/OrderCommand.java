package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.OrdersMenu;
import com.smpcore.utils.Sounds;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderCommand implements CommandExecutor {

    private final SMPCore plugin;

    public OrderCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;


        if (args.length == 0) {
            OrdersMenu.open(p, plugin.getOrderManager());
            return true;
        }


        if (args[0].equalsIgnoreCase("claim")) {
            plugin.getOrderManager().claimMail(p);
            return true;
        }


        if (args.length == 3) {
            try {
                Material mat = Material.matchMaterial(args[0].toUpperCase());
                if (mat == null) {
                    p.sendMessage("§cInvalid item material!");
                    Sounds.playError(p);
                    return true;
                }

                int amount = Integer.parseInt(args[1]);
                double price = Double.parseDouble(args[2]);

                if (amount <= 0 || price <= 0) {
                    p.sendMessage("§cAmount and price must be greater than 0.");
                    Sounds.playError(p);
                    return true;
                }

                if (plugin.getEconomyManager().has(p, price)) {
                    plugin.getEconomyManager().withdraw(p, price);
                    plugin.getOrderManager().createOrder(p, mat, amount, price);

                    p.sendMessage("§aOrder created! You paid §e$" + String.format("%,.2f", price) + " §afor §f" + amount + "x " + mat.name());
                    Sounds.playSuccess(p);
                } else {
                    p.sendMessage("§cInsufficient funds to create this order! You need §e$" + String.format("%,.2f", price));
                    Sounds.playError(p);
                }
            } catch (NumberFormatException e) {
                p.sendMessage("§cUsage: /order <item> <amount> <total_price>");
                Sounds.playError(p);
            }
            return true;
        }

        p.sendMessage("§cUsage: /order <item> <amount> <total_price> OR /order claim");
        return true;
    }
}