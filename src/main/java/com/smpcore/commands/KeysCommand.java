package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.KeysMenu;
import com.smpcore.menus.KeyTradeMenu;
import com.smpcore.utils.ChocolateUtils;
import com.smpcore.utils.KeyManager;
import com.smpcore.utils.KeyType;
import com.smpcore.utils.PurityUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KeysCommand implements CommandExecutor, TabCompleter {

    private final SMPCore plugin;
    private final KeyManager keyManager;

    public KeysCommand(SMPCore plugin) {
        this.plugin = plugin;
        this.keyManager = plugin.getKeyManager();
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        
        if (args.length == 0) {
            if (sender instanceof Player) KeysMenu.open((Player) sender, plugin);
            else sender.sendMessage("§cOnly players.");
            return true;
        }

        String sub = args[0].toLowerCase();

        
        if (sub.equals("trade")) {
            if (sender instanceof Player) KeyTradeMenu.open((Player) sender, plugin);
            return true;
        }

        
        if (sub.equals("wash")) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            ItemStack hand = p.getInventory().getItemInMainHand();

            if (!ChocolateUtils.isDirtyChocolate(hand)) {
                p.sendMessage("§cYou must hold a §4DIRTY CHOCOLATE BLOCK§c!");
                return true;
            }

            double cost = hand.getAmount() * 10000.0;
            if (plugin.getEconomyManager().getBalance(p) < cost) {
                p.sendMessage("§cNeed $" + String.format("%,.0f", cost));
                return true;
            }

            plugin.getEconomyManager().withdraw(p, cost);
            ItemStack clean = ChocolateUtils.getChocolate();
            clean.setAmount(hand.getAmount());
            p.getInventory().setItemInMainHand(clean);
            p.sendMessage("§9§lWASH COMPLETE! §7Paid §c$" + String.format("%,.0f", cost));
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_WATER_AMBIENT, 1f, 1f);
            return true;
        }

        
        if (!sender.hasPermission("smpcore.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        
        if (sub.equals("setpurity")) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            if (args.length < 2) {
                p.sendMessage("§cUsage: /keys setpurity <0-100>");
                return true;
            }

            try {
                double purity = Double.parseDouble(args[1]);
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand == null || hand.getType().isAir()) {
                    p.sendMessage("§cHold an item.");
                    return true;
                }
                ItemStack newItem = PurityUtils.setPurity(hand, purity);
                p.getInventory().setItemInMainHand(newItem);
                p.sendMessage("§aPurity set to " + purity + "%");
            } catch (Exception e) {
                p.sendMessage("§cError: " + e.getMessage());
            }
            return true;
        }

        
        if (sub.equals("give")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /keys give <player> <type> [amount]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            try {
                KeyType type = KeyType.valueOf(args[2].toUpperCase());
                int amount = (args.length >= 4) ? Integer.parseInt(args[3]) : 1;
                keyManager.giveKey(target, type, amount);
                sender.sendMessage("§aGave " + amount + " " + type.name() + " keys to " + target.getName());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid KeyType. Use TAB to see options.");
            }
            return true;
        }

        
        if (sub.equals("take")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /keys take <player> <type> [amount]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                try {
                    KeyType type = KeyType.valueOf(args[2].toUpperCase());
                    int amount = (args.length >= 4) ? Integer.parseInt(args[3]) : 1;
                    keyManager.takeKey(target, type, amount);
                    sender.sendMessage("§cTook keys.");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cInvalid KeyType.");
                }
            }
            return true;
        }

        
        if (sub.equals("chocolate")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /keys chocolate <player> [amount]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                int amount = (args.length >= 3) ? Integer.parseInt(args[2]) : 64;
                ItemStack c = ChocolateUtils.getChocolate();
                c.setAmount(amount);
                target.getInventory().addItem(c);
                sender.sendMessage("§aGave chocolate.");
            }
            return true;
        }

        if (sub.equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aConfig Reloaded.");
            return true;
        }

        return true;
    }

    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        
        if (args.length == 1) {
            
            completions.add("trade");
            completions.add("wash");

            
            if (sender.hasPermission("smpcore.admin")) {
                completions.add("give");
                completions.add("take");
                completions.add("chocolate");
                completions.add("setpurity");
                completions.add("reload");
            }
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        
        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            
            if (sub.equals("give") || sub.equals("take") || sub.equals("chocolate")) {
                return null; 
            }

            
            if (sub.equals("setpurity") && sender.hasPermission("smpcore.admin")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("50.0", "80.0", "95.0", "100.0"), new ArrayList<>());
            }
        }

        
        if (args.length == 3) {
            String sub = args[0].toLowerCase();

            if ((sub.equals("give") || sub.equals("take")) && sender.hasPermission("smpcore.admin")) {
                
                List<String> types = Arrays.stream(KeyType.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[2], types, new ArrayList<>());
            }

            if (sub.equals("chocolate") && sender.hasPermission("smpcore.admin")) {
                return Arrays.asList("1", "32", "64");
            }
        }

        
        if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if ((sub.equals("give") || sub.equals("take")) && sender.hasPermission("smpcore.admin")) {
                return Arrays.asList("1", "5", "10", "64");
            }
        }

        return Collections.emptyList();
    }
}