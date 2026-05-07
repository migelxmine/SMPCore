package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.SettingsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

    private final SMPCore plugin;

    public SettingsCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can open settings.");
            return true;
        }

        Player p = (Player) sender;

        
        
        SettingsMenu.open(p, plugin.getSettingsManager());

        return true;
    }
}