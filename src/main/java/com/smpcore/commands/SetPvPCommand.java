package com.smpcore.commands;

import com.smpcore.utils.DuelManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetPvPCommand implements CommandExecutor {

    private final DuelManager duelManager;

    public SetPvPCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.isOp()) {
            p.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("duel")) {
                duelManager.setDuelSpawn(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Duel Arena Spawn set successfully!");
            } else if (args[0].equalsIgnoreCase("train")) {
                duelManager.setTrainSpawn(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Training Arena Spawn set successfully!");
            } else {
                p.sendMessage(ChatColor.RED + "Usage: /setpvp <duel|train>");
            }
        } else {
            p.sendMessage(ChatColor.RED + "Usage: /setpvp <duel|train>");
        }

        return true;
    }
}