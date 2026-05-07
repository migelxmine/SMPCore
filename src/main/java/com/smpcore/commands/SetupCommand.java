package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    private final SMPCore plugin;

    public SetupCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;


        if (args.length == 2 && args[0].equalsIgnoreCase("finish")) {
            try {
                int colorId = Integer.parseInt(args[1]);
                plugin.getSetupManager().finishSetup(p, colorId);
            } catch (NumberFormatException ignored) {}
            return true;
        }


        plugin.getSetupManager().startSetup(p);
        return true;
    }
}