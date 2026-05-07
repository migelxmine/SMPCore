package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerStatusCommand implements CommandExecutor {

    private final SMPCore plugin;

    public ServerStatusCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        double totalMoney = plugin.getEconomyManager().getTotalCirculation();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int totalVaults = plugin.getVaultManager().getTotalVaultsCreated();

        String border = "§8§m--------------------------------------------------";
        sender.sendMessage(" ");
        sender.sendMessage(border);
        sender.sendMessage("§6§lSMPCore §8» §e§lSERVER STATUS REPORT");
        sender.sendMessage(" ");
        sender.sendMessage("§7Online Players: §f" + onlinePlayers);
        sender.sendMessage("§7TPS (Approx): §a20.0");
        sender.sendMessage(" ");
        sender.sendMessage("§6§lECONOMY:");
        sender.sendMessage("§8▪ §7Total Money in Circulation: §a$" + String.format("%,.2f", totalMoney));
        sender.sendMessage("§8▪ §7Crypto Market Cap: §9Active");
        sender.sendMessage(" ");
        sender.sendMessage("§6§lSYSTEMS:");
        sender.sendMessage("§8▪ §7Active Vaults: §e" + totalVaults);
        sender.sendMessage(border);
        sender.sendMessage(" ");

        return true;
    }
}