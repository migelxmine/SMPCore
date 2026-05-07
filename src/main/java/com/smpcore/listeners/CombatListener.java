package com.smpcore.listeners;

import com.smpcore.SMPCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {

    private final SMPCore plugin;

    public CombatListener(SMPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {

            
            plugin.getCombatManager().tagPlayer(victim);
            plugin.getCombatManager().tagPlayer(attacker);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        
        plugin.getCombatManager().removeCombat(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        
        if (plugin.getCombatManager().isInCombat(p)) {
            p.setHealth(0.0);
            plugin.getLogger().info(p.getName() + " was killed for combat logging.");
            plugin.getCombatManager().removeCombat(p.getUniqueId());
        }
    }
}