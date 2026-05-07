package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MissionManager {

    private final SMPCore plugin;
    private final List<Mission> activeMissions = new ArrayList<>();

    public MissionManager(SMPCore plugin) {
        this.plugin = plugin;
        loadMissions();
    }

    private void loadMissions() {
        
        

        activeMissions.add(new Mission("Novice Gold Trade", Material.GOLD_INGOT, 10, 50.0, 5));
        activeMissions.add(new Mission("Emerald Supply", Material.EMERALD, 5, 70.0, 15));
        activeMissions.add(new Mission("The King's Diamonds", Material.DIAMOND, 3, 90.0, 30));
        activeMissions.add(new Mission("Netherite Request", Material.NETHERITE_INGOT, 1, 95.0, 50));
        activeMissions.add(new Mission("Bulk Iron", Material.IRON_INGOT, 64, 40.0, 10));
    }

    public List<Mission> getMissions() {
        return activeMissions;
    }

    public void tryCompleteMission(Player p, Mission mission) {
        
        int foundAmount = 0;
        List<ItemStack> validItems = new ArrayList<>();

        
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == mission.getInputMaterial()) {

                
                double itemPurity = PurityUtils.getPurity(item);

                
                if (itemPurity >= mission.getMinPurity()) {
                    foundAmount += item.getAmount();
                    validItems.add(item);
                }
            }
        }

        
        if (foundAmount >= mission.getInputAmount()) {

            
            int leftToRemove = mission.getInputAmount();

            for (ItemStack is : validItems) {
                if (leftToRemove <= 0) break;

                if (is.getAmount() <= leftToRemove) {
                    leftToRemove -= is.getAmount();
                    p.getInventory().removeItem(is); 
                } else {
                    is.setAmount(is.getAmount() - leftToRemove); 
                    leftToRemove = 0;
                }
            }

            
            p.updateInventory();

            
            ItemStack reward = ChocolateUtils.getChocolate();
            reward.setAmount(mission.getRewardChocolate());
            p.getInventory().addItem(reward);

            p.sendMessage("§a§lMISSION COMPLETE!");
            p.sendMessage("§7Traded items with §e" + mission.getMinPurity() + "%+ purity§7.");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

        } else {
            
            p.sendMessage("§cRequirement not met!");
            p.sendMessage("§7You need §f" + mission.getInputAmount() + "x " + mission.getInputMaterial().name());
            p.sendMessage("§7with §e" + mission.getMinPurity() + "% purity §7or higher.");

            if (foundAmount > 0) {
                p.sendMessage("§8(You only have " + foundAmount + " valid items)");
            } else {
                p.sendMessage("§8(Normal ores don't count. Use the Jeweler first!)");
            }
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
}