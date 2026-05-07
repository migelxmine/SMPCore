package com.smpcore.listeners;

import com.smpcore.SMPCore;
import com.smpcore.utils.ChocolateUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ChocolateListener implements Listener {

    private final SMPCore plugin;

    public ChocolateListener(SMPCore plugin) {
        this.plugin = plugin;
    }

    
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        
        if (ChocolateUtils.isChocolate(e.getItemInHand())) {
            
            e.getBlockPlaced().setMetadata("is_chocolate", new FixedMetadataValue(plugin, true));

            
            e.getPlayer().sendMessage("§c§l⚠ WARNING: §7You placed a §fChocolate Block§7!");
            e.getPlayer().sendMessage("§7If you break it, it will become §4DIRTY§7.");
        }
        
        else if (ChocolateUtils.isDirtyChocolate(e.getItemInHand())) {
            e.getBlockPlaced().setMetadata("is_dirty_chocolate", new FixedMetadataValue(plugin, true));
        }
    }

    
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Block b = e.getBlock();

        
        if (b.getType() != Material.BROWN_GLAZED_TERRACOTTA) return;
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return; 

        
        if (b.hasMetadata("is_chocolate")) {
            e.setDropItems(false); 

            
            b.getWorld().dropItem(b.getLocation(), ChocolateUtils.getDirtyChocolate());

            e.getPlayer().sendMessage("§4Oh no! The chocolate got dirty!");
            b.removeMetadata("is_chocolate", plugin); 
        }
        
        else if (b.hasMetadata("is_dirty_chocolate")) {
            e.setDropItems(false);
            b.getWorld().dropItem(b.getLocation(), ChocolateUtils.getDirtyChocolate());
            b.removeMetadata("is_dirty_chocolate", plugin);
        }
    }
}