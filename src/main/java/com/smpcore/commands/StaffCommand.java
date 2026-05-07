package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.StaffMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {
    private final SMPCore plugin;

    public StaffCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole cannot open this menu.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("smpcore.admin")) {
            p.sendMessage("§cYou do not have permission to access the Staff Panel.");
            return true;
        }

        StaffMenu.openMainMenu(p, plugin);
        return true;
    }
}