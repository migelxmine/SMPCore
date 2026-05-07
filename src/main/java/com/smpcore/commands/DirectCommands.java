package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.*;
import com.smpcore.utils.CryptoManager;
import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.HomeManager;
import com.smpcore.utils.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DirectCommands implements CommandExecutor {

    private final SMPCore plugin;
    private final LangManager langManager;
    private final EconomyManager economyManager;
    private final CryptoManager cryptoManager;
    private final HomeManager homeManager;

    
    public DirectCommands(SMPCore plugin, LangManager langManager, EconomyManager economyManager,
                          CryptoManager cryptoManager, HomeManager homeManager) {
        this.plugin = plugin;
        this.langManager = langManager;
        this.economyManager = economyManager;
        this.cryptoManager = cryptoManager;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player p = (Player) sender;

        switch (label.toLowerCase()) {
            case "sell":
                
                SellMenu.open(p, langManager);
                break;

            case "shop":
                
                ShopMenu.open(p, langManager, economyManager);
                break;

            case "casino":
                
                CasinoLegalMenu.open(p);
                break;

            case "crypto":
                
                InvestmentMenu.open(p, cryptoManager);
                break;

            case "rtp":
                
                TPRandomMenu.open(p);
                break;

            case "pvphub":
                
                PvPMenu.open(p);
                break;

            case "homes":
                
                HomesMenu.open(p, homeManager);
                break;

            case "ranks":

                RanksMenu.open(p, plugin);
                break;

            case "rank":

                RanksMenu.open(p, plugin);
                break;

            default:
                p.sendMessage("§cUnknown menu command.");
                break;
        }
        return true;
    }
}