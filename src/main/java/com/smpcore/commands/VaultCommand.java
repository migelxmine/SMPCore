package com.smpcore.commands;

import com.smpcore.utils.VaultManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VaultCommand implements CommandExecutor {

    private final VaultManager vaultManager;

    public VaultCommand(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have vaults.");
            return true;
        }
        vaultManager.openVault((Player) sender);
        return true;
    }
}