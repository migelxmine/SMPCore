package com.smpcore.commands;

import com.smpcore.SMPCore;
import com.smpcore.menus.ConfirmationMenu;
import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.PriceManager;
import com.smpcore.utils.Sounds;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand implements CommandExecutor {

    private final SMPCore plugin;
    private final PriceManager priceManager;
    private final EconomyManager economyManager;
    private final LangManager langManager;

    public BuyCommand(SMPCore plugin, PriceManager priceManager, EconomyManager economyManager, LangManager langManager) {
        this.plugin = plugin;
        this.priceManager = priceManager;
        this.economyManager = economyManager;
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(langManager.getMessage("messages.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(langManager.getMessage("commands.buy.usage"));
            return true;
        }

        String itemName = String.join("_", args).toUpperCase();
        Material material;
        try {
            material = Material.valueOf(itemName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(langManager.getMessage("commands.buy.item-not-found", "%item%", itemName));
            Sounds.playError(player);
            return true;
        }

        if (priceManager.hasPrice(material)) {
            Sounds.playClick(player);
            ConfirmationMenu cm = new ConfirmationMenu(priceManager, economyManager, langManager, material, null);
            plugin.getPendingConfirmations().put(player.getUniqueId(), cm);
            cm.open(player);
        } else {
            player.sendMessage(langManager.getMessage("commands.buy.not-for-sale", "%item%", material.name()));
            Sounds.playError(player);
        }
        return true;
    }
}