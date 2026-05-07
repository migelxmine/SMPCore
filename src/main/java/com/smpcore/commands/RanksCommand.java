package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.RanksMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    private final SMPCore plugin;

    public RanksCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only command!");
            return true;
        }

        Player p = (Player) sender;
        RanksMenu.open(p, plugin);
        return true;
    }
}