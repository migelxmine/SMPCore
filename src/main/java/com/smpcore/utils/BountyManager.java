package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BountyManager implements Listener {

    private final SMPCore plugin;
    private final EconomyManager economyManager;
    private File bountiesFile;
    private FileConfiguration bountiesConfig;

    private static final double BASE_BOUNTY = 500.0;
    private static final double KILL_BOUNTY_INCREASE = 250.0;
    private static final double VICTIM_BOUNTY_PERCENTAGE = 0.10; 

    public BountyManager(SMPCore plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        setup();
    }

    public void setup() {
        bountiesFile = new File(plugin.getDataFolder(), "bounties.yml");
        if (!bountiesFile.exists()) {
            try {
                bountiesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create bounties.yml!");
            }
        }
        bountiesConfig = YamlConfiguration.loadConfiguration(bountiesFile);
    }

    public void saveBounties() {
        try {
            bountiesConfig.save(bountiesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save bounties.yml!");
        }
    }

    public double getBounty(UUID uuid) {
        return bountiesConfig.getDouble(uuid.toString(), BASE_BOUNTY);
    }

    public void setBounty(UUID uuid, double amount) {
        bountiesConfig.set(uuid.toString(), amount);
        saveBounties();
    }

    public void resetBounty(Player player) {
        setBounty(player.getUniqueId(), BASE_BOUNTY);
    }

    public void increaseBounty(Player killer, Player victim) {
        double killerBounty = getBounty(killer.getUniqueId());
        double victimBounty = getBounty(victim.getUniqueId());

        double newBounty = killerBounty + KILL_BOUNTY_INCREASE + (victimBounty * VICTIM_BOUNTY_PERCENTAGE);
        setBounty(killer.getUniqueId(), newBounty);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) {
            return;
        }

        double victimBounty = getBounty(victim.getUniqueId());

        economyManager.deposit(killer, victimBounty);
        increaseBounty(killer, victim);
        double newKillerBounty = getBounty(killer.getUniqueId());
        resetBounty(victim);

        Sounds.playSuccess(killer);
        killer.sendMessage(ChatColor.GREEN + "You claimed the bounty of $" + String.format("%,.2f", victimBounty) + " for killing " + victim.getName() + "!");
        killer.sendMessage(ChatColor.GOLD + "Your new bounty is now $" + String.format("%,.2f", newKillerBounty) + ".");

        victim.sendMessage(ChatColor.RED + "You were killed by " + killer.getName() + ", who claimed your bounty.");

        Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "BOUNTY CLAIMED: " +
                ChatColor.YELLOW + killer.getName() + " killed " + victim.getName() + " and earned " +
                ChatColor.GREEN + "$" + String.format("%,.2f", victimBounty) + "!");
    }
}