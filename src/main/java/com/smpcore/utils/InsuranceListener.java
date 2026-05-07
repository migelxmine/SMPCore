package com.smpcore.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class InsuranceListener implements Listener {

    private final InsuranceManager manager;

    public InsuranceListener(InsuranceManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        
        if (manager.hasActiveDeathInsurance(e.getEntity())) {

            e.setKeepInventory(true);
            e.setKeepLevel(true);
            e.setDroppedExp(0);
            e.getDrops().clear();

            manager.useDeathInsurance(e.getEntity());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        
        manager.checkBankruptcy(e.getPlayer());
    }
}