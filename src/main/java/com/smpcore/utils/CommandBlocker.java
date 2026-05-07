package com.smpcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Arrays;
import java.util.List;

public class CommandBlocker implements Listener {

    
    
    private final List<String> allowedCommands = Arrays.asList(
            
            "menu", "settings", "help", "spawn", "warp",
            "rtp", "tpr", "pvphub", "smpstatus", "offend",

            
            "home", "sethome", "delhome", "homes",
            "tpa", "tpahere", "tpaccept", "tpdeny",

            
            "pay", "bal", "balance", "money",
            "shop", "sell", "ah", "auction",
            "order", "orders", "sm", "casino", "crypto",
            "vault", "insurance", "keys",

            
            "msg", "tell", "w", "r", "reply", "kill",

            
            "login", "register", "l", "changepassword"
    );

    
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent e) {
        
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("smpcore.admin")) return;

        
        e.getCommands().removeIf(cmd -> !allowedCommands.contains(cmd.toLowerCase()));
    }

    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        
        if (p.isOp() || p.hasPermission("smpcore.admin")) return;

        
        String message = e.getMessage().toLowerCase();
        String command = message.split(" ")[0].replace("/", "");

        
        if (!allowedCommands.contains(command) || message.contains(":")) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for help.");
        }
    }
}