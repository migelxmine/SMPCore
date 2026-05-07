package com.smpcore.commands;

import com.smpcore.utils.LangManager;
import com.smpcore.utils.Sounds;
import com.smpcore.utils.TprManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TprCommand implements CommandExecutor {

    private final TprManager tprManager;
    private final LangManager langManager;

    public TprCommand(TprManager tprManager, LangManager langManager) {
        this.tprManager = tprManager;
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(langManager.getMessage("messages.player-only"));
            return true;
        }

        Player requester = (Player) sender;
        if (args.length != 1) {
            
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            requester.sendMessage(langManager.getMessage("messages.player-not-found", "%player%", args[0]));
            Sounds.playError(requester);
            return true;
        }
        if (requester.equals(target)) {
            requester.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
            Sounds.playError(requester);
            return true;
        }
        if (tprManager.isTprEnabled(target)) {
            requester.teleport(target.getLocation());
            requester.sendMessage(ChatColor.GREEN + "Successfully teleported to " + target.getName() + ".");
            Sounds.playSuccess(requester);
            target.sendMessage(ChatColor.AQUA + requester.getName() + " teleported to you.");
        } else {
            requester.sendMessage(ChatColor.RED + "Player " + target.getName() + " does not have teleport requests enabled.");
            Sounds.playError(requester);
        }
        return true;
    }
}