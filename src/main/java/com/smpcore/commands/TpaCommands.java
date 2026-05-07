package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.utils.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommands implements CommandExecutor {

    private final SMPCore plugin;

    public TpaCommands(SMPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        TpaManager tpa = plugin.getTpaManager();

        
        if (label.equalsIgnoreCase("tpa")) {
            if (args.length < 1) return msg(p, "§cUsage: /tpa <player>");
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || target.equals(p)) return msg(p, "§cInvalid player.");
            tpa.sendRequest(p, target, false); 
            return true;
        }

        
        if (label.equalsIgnoreCase("tpahere")) {
            if (args.length < 1) return msg(p, "§cUsage: /tpahere <player>");
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || target.equals(p)) return msg(p, "§cInvalid player.");
            tpa.sendRequest(p, target, true); 
            return true;
        }

        
        if (label.equalsIgnoreCase("tpaccept")) {
            tpa.acceptRequest(p);
            return true;
        }

        
        if (label.equalsIgnoreCase("tpdeny")) {
            tpa.denyRequest(p);
            return true;
        }

        return true;
    }

    private boolean msg(Player p, String s) {
        p.sendMessage(s);
        return true;
    }
}