package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class EconomyControlManager {

    private final SMPCore plugin;
    private final HashSet<UUID> frozenPlayers = new HashSet<>();
    private final HashSet<UUID> bannedEconomyPlayers = new HashSet<>();
    private final Map<UUID, String> reasons = new HashMap<>();
    private boolean serverFrozen = false;

    public EconomyControlManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void setFrozen(UUID uuid, boolean frozen, String reason) {
        if (frozen) {
            frozenPlayers.add(uuid);
            if (reason != null) reasons.put(uuid, reason);
            plugin.getLogger().info("[Security] FREEZE PLAYER: " + uuid + " Reason: " + reason);
        } else {
            frozenPlayers.remove(uuid);
            reasons.remove(uuid);
            plugin.getLogger().info("[Security] UNFREEZE PLAYER: " + uuid);
        }
    }

    public boolean isFrozen(UUID uuid) {
        return serverFrozen || frozenPlayers.contains(uuid);
    }

    public void setServerFrozen(boolean frozen) {
        this.serverFrozen = frozen;
        plugin.getLogger().info("[Security] SERVER FREEZE: " + frozen);
    }

    public boolean isServerFrozen() {
        return serverFrozen;
    }

    public void setEconomyBanned(UUID uuid, boolean banned, String reason) {
        if (banned) {
            bannedEconomyPlayers.add(uuid);
            reasons.put(uuid, reason);
            plugin.getLogger().warning("[Security] ECONOMY BAN: " + uuid + " Reason: " + reason);
        } else {
            bannedEconomyPlayers.remove(uuid);
            reasons.remove(uuid);
            plugin.getLogger().info("[Security] ECONOMY UNBAN: " + uuid);
        }
    }

    public boolean isEconomyBanned(UUID uuid) {
        return bannedEconomyPlayers.contains(uuid);
    }

    public boolean canTransact(Player p) {
        if (serverFrozen) {
            p.sendMessage("§c§lERROR: §cThe server economy is currently frozen.");
            return false;
        }
        if (frozenPlayers.contains(p.getUniqueId())) {
            p.sendMessage("§c§lFROZEN: §cYour account is frozen. Reason: " + reasons.getOrDefault(p.getUniqueId(), "Under investigation"));
            return false;
        }
        if (bannedEconomyPlayers.contains(p.getUniqueId())) {
            p.sendMessage("§4§lBANNED: §cYou are permanently banned from using the economy.");
            return false;
        }
        return true;
    }
}