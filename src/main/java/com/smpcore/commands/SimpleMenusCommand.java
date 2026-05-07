package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SimpleMenusCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final String menuType;

    public SimpleMenusCommand(SMPCore plugin, String menuType) {
        this.plugin = plugin;
        this.menuType = menuType;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        switch (menuType) {
            case "ah":
                AuctionMenu.open(p, plugin.getAuctionManager(), com.smpcore.utils.AuctionSort.NEWEST, com.smpcore.utils.AuctionCategory.ALL, null);
                break;
            case "insurance":
                InsuranceMenu.open(p, plugin.getInsuranceManager());
                break;
            case "mshop":
                
                ShopMenu.open(p, plugin.getLangManager(), plugin.getEconomyManager());
                break;
            case "kill":
                KillMenu.open(p);
                break;
        }
        return true;
    }
}