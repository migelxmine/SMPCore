package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuBanCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final boolean isUnban;

    public MenuBanCommand(SMPCore plugin, boolean isUnban) {
        this.plugin = plugin;
        this.isUnban = isUnban;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /" + label + " <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (isUnban) {
            if (!plugin.isMenuBanned(target.getUniqueId())) {
                sender.sendMessage("§cPlayer " + args[0] + " is not menu-banned.");
                return true;
            }
            plugin.setMenuBanned(target.getUniqueId(), false);
            sender.sendMessage("§a§lSUCCESS: §fPlayer §e" + args[0] + " §fhas been unbanned from SMP Menus.");

            if (target.isOnline()) {
                target.getPlayer().sendMessage("§a§lACCESS RESTORED: §7You can now use the server menus again.");
            }
        } else {
            if (plugin.isMenuBanned(target.getUniqueId())) {
                sender.sendMessage("§cPlayer " + args[0] + " is already menu-banned.");
                return true;
            }
            plugin.setMenuBanned(target.getUniqueId(), true);
            sender.sendMessage("§c§lBANNED: §fPlayer §e" + args[0] + " §fhas been banned from SMP Menus.");

            if (target.isOnline()) {
                target.getPlayer().closeInventory();
                target.getPlayer().sendMessage("§4§lWARNING: §cAn administrator has banned you from using server menus.");
            }
        }

        return true;
    }
}