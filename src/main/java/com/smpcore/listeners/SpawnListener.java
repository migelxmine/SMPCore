package com.smpcore.listeners;

import com.smpcore.SMPCore;
import com.smpcore.menus.CratesMainMenu;
import com.smpcore.utils.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;

public class SpawnListener implements Listener {

    private final SMPCore plugin;
    private final HashSet<UUID> buildModePlayers = new HashSet<>();

    public SpawnListener(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void toggleBuildMode(Player p) {
        if (buildModePlayers.contains(p.getUniqueId())) {
            buildModePlayers.remove(p.getUniqueId());
            p.sendMessage("§c§lBUILD: §cSpawn protection enabled (You can't build).");
        } else {
            buildModePlayers.add(p.getUniqueId());
            p.sendMessage("§a§lBUILD: §aSpawn protection disabled (You can build).");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();


        e.setJoinMessage(null);
        plugin.getQueueManager().addPlayer(p);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        String toWorld = p.getWorld().getName();
        String fromWorld = e.getFrom().getName();

        if (toWorld.equals("SMP_Spawn")) {
            setupFlight(p);
        }
        else if (fromWorld.equals("SMP_Spawn")) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }
    }

    private void setupFlight(Player p) {
        if (p.getWorld().getName().equals("SMP_Spawn")) {
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                p.setAllowFlight(true);
            }
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR
                && p.getWorld().getName().equals("SMP_Spawn")) {

            
            RankManager.SocialRank rank = plugin.getRankManager().getSocialRank(p);
            RankManager.StaffRole role = plugin.getRankManager().getStaffRole(p);

            
            if (rank == RankManager.SocialRank.VIP || role == RankManager.StaffRole.OWNER || role == RankManager.StaffRole.SR_ADMIN) {
                
                return;
            }

            
            e.setCancelled(true);
            p.setAllowFlight(false);
            p.setFlying(false);
            Vector jump = p.getLocation().getDirection().multiply(1.5).setY(1.0);
            p.setVelocity(jump);
            p.playSound(p.getLocation(), Sound.ENTITY_SLIME_JUMP, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().getName().equals("SMP_Spawn")
                && (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {

            if (p.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid() && !p.getAllowFlight()) {
                p.setAllowFlight(true);
            }
        }
    }

    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        
        if (!p.getWorld().getName().equals("SMP_Spawn")) return;

        
        if (buildModePlayers.contains(p.getUniqueId())) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();

            if (item == null) return;

            
            if (item.getType() == Material.COMPASS) {
                e.setCancelled(true);
                World survivalWorld = Bukkit.getWorld("world");
                if (survivalWorld != null) {
                    plugin.getTprManager().teleport(p, survivalWorld, 0.0);
                } else {
                    p.sendMessage("§cError: Survival world not found!");
                }
                return;
            }

            
            Material type = item.getType();
            if (type == Material.ARMOR_STAND ||
                    type == Material.ITEM_FRAME ||
                    type == Material.GLOW_ITEM_FRAME ||
                    type == Material.PAINTING ||
                    type == Material.END_CRYSTAL ||
                    type.name().contains("BOAT") ||
                    type.name().contains("MINECART") ||
                    type == Material.FLINT_AND_STEEL || 
                    type == Material.TNT) {

                e.setCancelled(true);
                p.sendMessage("§cYou cannot place that here!");
            }
        }
    }

    
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getPlayer().getWorld().getName().equals("SMP_Spawn") && !buildModePlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot place liquids here!");
        }
    }

    
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (e.getPlayer().getWorld().getName().equals("SMP_Spawn") && !buildModePlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractNPC(PlayerInteractEntityEvent e) {
        Entity clicked = e.getRightClicked();
        if (clicked.getScoreboardTags().contains("SMP_NPC")) {
            e.setCancelled(true);
            Player p = e.getPlayer();

            if (clicked.getScoreboardTags().contains("NPC_RTP")) {
                plugin.getTprManager().teleport(p, Bukkit.getWorld("world"), 0.0);
            }
            else if (clicked.getScoreboardTags().contains("NPC_CASINO")) plugin.getCasinoManager().openCasinoMenu(p);
            else if (clicked.getScoreboardTags().contains("NPC_AUCTION")) p.performCommand("ah");
            else if (clicked.getScoreboardTags().contains("NPC_KEYS")) CratesMainMenu.open(e.getPlayer(), plugin);
            else if (clicked.getScoreboardTags().contains("NPC_SUPERTOOLS")) p.sendMessage("§cTools shop coming soon!");

            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        }
        
        else if (e.getPlayer().getWorld().getName().equals("SMP_Spawn") && !buildModePlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getWorld().getName().equals("SMP_Spawn")) {
            e.setCancelled(true);

            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                Location spawn = (plugin.getSpawnGenManager().getSpawnWorld() != null)
                        ? plugin.getSpawnGenManager().getSpawnWorld().getSpawnLocation()
                        : Bukkit.getWorld("SMP_Spawn").getSpawnLocation();
                e.getEntity().teleport(spawn);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().getWorld().getName().equals("SMP_Spawn") && !buildModePlayers.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getWorld().getName().equals("SMP_Spawn") && !buildModePlayers.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.isAnchorSpawn() && !e.isBedSpawn()) {
            e.setRespawnLocation(plugin.getSpawnGenManager().getSpawnWorld().getSpawnLocation());
        }
    }
}