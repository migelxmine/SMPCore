package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TpaManager {

    private final SMPCore plugin;

    private static class Request {
        UUID sender;
        boolean isHere;

        Request(UUID sender, boolean isHere) {
            this.sender = sender;
            this.isHere = isHere;
        }
    }

    private final HashMap<UUID, Request> pendingRequests = new HashMap<>();

    public TpaManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player target, boolean isHere) {

        if (plugin.getCombatManager() != null && plugin.getCombatManager().isInCombat(sender)) {
            sender.sendMessage("§cYou cannot send teleport requests while in combat!");
            Sounds.playError(sender);
            return;
        }

        if (!plugin.getSettingsManager().getSetting(target, "tpa_requests")) {
            sender.sendMessage("§cThis player has disabled teleport requests.");
            return;
        }

        pendingRequests.put(target.getUniqueId(), new Request(sender.getUniqueId(), isHere));

        String type = isHere ? "teleport you to them" : "teleport to you";

        sender.sendMessage("§eRequest sent to §f" + target.getName());

        target.sendMessage("§7--------------------------------");
        target.sendMessage("§9" + sender.getName() + " §fhas requested to §9" + type + "§f.");
        target.sendMessage("§fType §a/tpaccept §for §c/tpdeny");
        target.sendMessage("§7--------------------------------");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.containsKey(target.getUniqueId()) &&
                    pendingRequests.get(target.getUniqueId()).sender.equals(sender.getUniqueId())) {
                pendingRequests.remove(target.getUniqueId());
            }
        }, 1200L);
    }

    public void acceptRequest(Player target) {
        if (!pendingRequests.containsKey(target.getUniqueId())) {
            target.sendMessage("§cYou have no pending requests.");
            return;
        }

        Request req = pendingRequests.get(target.getUniqueId());
        Player sender = Bukkit.getPlayer(req.sender);

        if (sender != null && sender.isOnline()) {


            Player teleporter = req.isHere ? target : sender;


            if (plugin.getCombatManager() != null && plugin.getCombatManager().isInCombat(teleporter)) {
                teleporter.sendMessage("§cYou cannot teleport while in combat!");
                Sounds.playError(teleporter);
                return;
            }

            target.sendMessage("§aRequest accepted.");
            sender.sendMessage("§aRequest accepted by " + target.getName());

            if (req.isHere) {

                plugin.getTeleportManager().teleportWithWarmup(target, sender.getLocation());
            } else {

                plugin.getTeleportManager().teleportWithWarmup(sender, target.getLocation());
            }

        } else {
            target.sendMessage("§cPlayer is offline.");
        }

        pendingRequests.remove(target.getUniqueId());
    }

    public void denyRequest(Player target) {
        if (pendingRequests.remove(target.getUniqueId()) != null) {
            target.sendMessage("§cRequest denied.");
        } else {
            target.sendMessage("§cNo pending requests.");
        }
    }
}