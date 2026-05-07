package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PayCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final EconomyManager eco;
    private final SettingsManager settingsManager;

    
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public PayCommand(SMPCore plugin) {
        this.plugin = plugin;
        this.eco = plugin.getEconomyManager();
        this.settingsManager = plugin.getSettingsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player p = (Player) sender;

        
        if (args.length < 2) {
            p.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.equals(p)) {
            p.sendMessage("§cYou cannot pay yourself.");
            return true;
        }

        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage("§cInvalid amount.");
            return true;
        }

        if (amount <= 0) {
            p.sendMessage("§cAmount must be positive.");
            return true;
        }

        
        if (eco.getBalance(p) < amount) {
            p.sendMessage("§cInsufficient funds.");
            return true;
        }

        
        
        if (!settingsManager.isPayEnabled(target)) {
            p.sendMessage("§cThis player has disabled payments!");
            return true;
        }

        
        eco.withdraw(p, amount);
        eco.deposit(target, amount);

        
        

        
        p.sendMessage("§7You paid §9" + target.getName() + " §a$" + formatMoney(amount) + "§7.");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        
        target.sendMessage("§9" + p.getName() + " §7paid you §a$" + formatMoney(amount) + "§7.");
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);

        return true;
    }

    
    private String formatMoney(double amount) {
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000);
        if (amount >= 1_000) return String.format("%.1fk", amount / 1_000);
        return df.format(amount);
    }
}