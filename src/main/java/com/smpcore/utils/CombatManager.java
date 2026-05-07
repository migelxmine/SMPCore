package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final SMPCore plugin;
    private final Map<UUID, Integer> combatLog = new HashMap<>();
    private final Map<UUID, BukkitRunnable> combatTasks = new HashMap<>();

    public CombatManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void tagPlayer(Player p) {
        UUID uuid = p.getUniqueId();


        combatLog.put(uuid, 20);


        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) {
                    removeCombat(uuid);
                    this.cancel();
                    return;
                }

                int timeLeft = combatLog.getOrDefault(uuid, 0);
                if (timeLeft <= 0) {
                    removeCombat(uuid);
                    p.sendActionBar("§aYou are no longer in combat.");
                    this.cancel();
                    return;
                }


                p.sendActionBar("§cCombat: §f" + timeLeft);
                combatLog.put(uuid, timeLeft - 1);
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
        combatTasks.put(uuid, task);
    }

    public boolean isInCombat(Player p) {
        return combatLog.containsKey(p.getUniqueId()) && combatLog.get(p.getUniqueId()) > 0;
    }

    public void removeCombat(UUID uuid) {
        combatLog.remove(uuid);
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
        }
    }
}