package com.smpcore.commands;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OffendCommand implements CommandExecutor {

    private final SMPCore plugin;

    public OffendCommand(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cPermission denied.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /offend <arrest|release|fakeban> <player>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        switch (action) {
            case "arrest":
                Location loc = target.getLocation().add(0, 100, 0);
                loc.getBlock().setType(Material.BEDROCK);
                target.teleport(loc.add(0, 1, 0));

                target.sendMessage("§c§lPOLICE: §cYou are under arrest!");
                sender.sendMessage("§aPlayer arrested high in the sky.");
                break;

            case "fakeban":
                String reason = "§cYou have been permanently banned from this server.\n§7Reason: §fSecurity Violation #9921\n§7Appeal at discord.gg/yourdiscord";
                target.kickPlayer(reason);
                sender.sendMessage("§aFake ban executed. They probably had a heart attack.");
                break;

            case "release":
                target.performCommand("spawn");
                target.sendMessage("§a§lPOLICE: §aYou have been released. Behave.");
                sender.sendMessage("§aPlayer released.");
                break;
            default:
                sender.sendMessage("§cUnknown action. Use arrest, release, or fakeban.");
        }

        return true;
    }
}