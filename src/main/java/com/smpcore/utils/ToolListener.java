package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ToolListener implements Listener {

    private final SMPCore plugin;
    private final RegionManager regionManager;

    
    private final List<Player> isDrilling = new ArrayList<>();

    public ToolListener(SMPCore plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();

        if (hand == null || !hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(hand.getItemMeta().getDisplayName());

        
        if (name.equals("CLAIM SHOVEL")) {
            
            e.setCancelled(true);

            
            if (p.isSneaking() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                if (regionManager.confirmClaim(p)) {
                    hand.setAmount(hand.getAmount() - 1); 
                    Sounds.playSuccess(p);
                } else {
                    Sounds.playError(p);
                }
                return;
            }

            
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                regionManager.setPos1(p, e.getClickedBlock().getLocation());
                Sounds.playClick(p);
            }

            
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                regionManager.setPos2(p, e.getClickedBlock().getLocation());
                Sounds.playClick(p);
                p.sendMessage(ChatColor.YELLOW + "Tip: Sneak + Right Click to confirm claim.");
            }
        }

        
        else if (name.contains("FASTY") || name.contains("SPEED")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 2)); 
                p.sendMessage(ChatColor.GREEN + "⚡ Fasty Speed Activated!");
                Sounds.playSuccess(p);

                
                
            }
        }
    }

    
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();

        if (isDrilling.contains(p)) return; 
        if (hand == null || !hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(hand.getItemMeta().getDisplayName());

        
        if (name.contains("DRILL")) {
            
            if (p.isSneaking()) return;

            Block center = e.getBlock();
            isDrilling.add(p); 

            
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        
                        if (x == 0 && y == 0 && z == 0) continue;

                        Block target = center.getRelative(x, y, z);

                        
                        
                        if (target.getType() == Material.BEDROCK || target.getType() == Material.AIR) continue;

                        
                        if (!regionManager.canInteract(p, target.getLocation())) continue;

                        
                        target.breakNaturally(hand);
                    }
                }
            }

            isDrilling.remove(p); 
        }
    }
}