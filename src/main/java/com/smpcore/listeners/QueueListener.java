package com.smpcore.listeners;

import com.smpcore.SMPCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

    private final SMPCore plugin;

    public QueueListener(SMPCore plugin) {
        this.plugin = plugin;
    }

    private boolean inQueue(Player p) {
        return plugin.getQueueManager() != null && plugin.getQueueManager().isInQueue(p);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (inQueue(e.getPlayer())) {
            
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) { if (inQueue(e.getPlayer())) e.setCancelled(true); }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) { if (inQueue(e.getPlayer())) e.setCancelled(true); }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) { if (inQueue(e.getPlayer())) e.setCancelled(true); }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && inQueue((Player) e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && inQueue((Player) e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (inQueue(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cCan't use chat");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (inQueue(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cCan't use command");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (inQueue(e.getPlayer())) {
            plugin.getQueueManager().removePlayer(e.getPlayer());
        }
    }
}