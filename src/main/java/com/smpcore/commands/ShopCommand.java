package com.smpcore.commands;

import com.smpcore.menus.ShopMenu;
import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {

    private final LangManager lang;
    private final EconomyManager eco; 

    public ShopCommand(LangManager lang, EconomyManager eco) {
        this.lang = lang;
        this.eco = eco;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            
            ShopMenu.open(p, lang, eco);
            return true;
        }
        return false;
    }
}