package com.smpcore.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RegionListener implements Listener {

    private final RegionManager regionManager;

    public RegionListener(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        
        if (!regionManager.canInteract(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        
        if (!regionManager.canInteract(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        
        if (e.getClickedBlock() != null) {
            if (!regionManager.canInteract(e.getPlayer(), e.getClickedBlock().getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        
        e.blockList().removeIf(block -> regionManager.isClaimed(block.getLocation()));
    }
}