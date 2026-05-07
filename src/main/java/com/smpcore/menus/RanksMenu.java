package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RanksMenu implements InventoryHolder {

    private final Inventory inv;

    public RanksMenu() {
        
        this.inv = Bukkit.createInventory(this, 54, "§8Ranks & Subscriptions");
    }

    public static void open(Player p, SMPCore plugin) {
        RanksMenu menu = new RanksMenu();
        RankManager rm = plugin.getRankManager();
        RankManager.SocialRank currentRank = rm.getSocialRank(p);
        boolean hasUsedTrial = rm.hasUsedTrial(p.getUniqueId());

        
        ItemStack glass = new ItemStack(Material.GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 54; i++) menu.getInventory().setItem(i, glass);

        
        
        

        
        ItemStack memberItem = new ItemStack(Material.IRON_BLOCK);
        ItemMeta memberMeta = memberItem.getItemMeta();
        if (memberMeta != null) {
            memberMeta.setDisplayName("§7§lMEMBER RANK");
            List<String> lore = new ArrayList<>();
            lore.add("§8Default Social Rank");
            lore.add(" ");
            lore.add("§fPerks:");
            lore.add("§8- §72 Home Slots");
            lore.add("§8- §7Normal Queue Priority");
            lore.add(" ");
            if (currentRank == RankManager.SocialRank.MEMBER) lore.add("§a▶ You are currently a Member.");
            else lore.add("§7Free forever.");
            memberMeta.setLore(lore);
            memberItem.setItemMeta(memberMeta);
        }
        menu.getInventory().setItem(11, memberItem);

        
        ItemStack exclusiveItem = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta exclusiveMeta = exclusiveItem.getItemMeta();
        if (exclusiveMeta != null) {
            exclusiveMeta.setDisplayName("§6§lEXCLUSIVE RANK");
            List<String> lore = new ArrayList<>();
            lore.add("§8Premium Social Rank");
            lore.add(" ");
            lore.add("§fPerks:");
            lore.add("§8- §74 Home Slots");
            lore.add("§8- §7High Queue Priority");
            lore.add("§8- §72x AFK Rewards");
            lore.add(" ");
            if (currentRank == RankManager.SocialRank.EXCLUSIVE) {
                long expiry = rm.getRankExpiry(p.getUniqueId());
                long hoursLeft = (expiry - System.currentTimeMillis()) / (1000 * 60 * 60);
                lore.add("§a▶ You currently have this rank.");
                lore.add("§7Expires in: §e" + hoursLeft + " hours");
            } else {
                lore.add("§e▶ Select a plan below!");
            }
            exclusiveMeta.setLore(lore);
            exclusiveItem.setItemMeta(exclusiveMeta);
        }
        menu.getInventory().setItem(13, exclusiveItem);

        
        ItemStack vipItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta vipMeta = vipItem.getItemMeta();
        if (vipMeta != null) {
            vipMeta.setDisplayName("§a§lVIP RANK");
            List<String> lore = new ArrayList<>();
            lore.add("§8Ultimate Social Rank");
            lore.add(" ");
            lore.add("§fPerks:");
            lore.add("§8- §7Full Home slots");
            lore.add("§8- §7Highest Queue Priority");
            lore.add("§8- §7Fly at Spawn");
            lore.add("§8- §74x AFK Rewards");
            lore.add(" ");
            if (currentRank == RankManager.SocialRank.VIP) {
                long expiry = rm.getRankExpiry(p.getUniqueId());
                long hoursLeft = (expiry - System.currentTimeMillis()) / (1000 * 60 * 60);
                lore.add("§a▶ You currently have this rank.");
                lore.add("§7Expires in: §e" + hoursLeft + " hours");
            } else {
                lore.add("§e▶ Select a plan below!");
            }
            vipMeta.setLore(lore);
            vipItem.setItemMeta(vipMeta);
        }
        menu.getInventory().setItem(15, vipItem);

        
        
        

        
        menu.getInventory().setItem(22, createPaper(
                hasUsedTrial ? "§c§lFree Trial Used" : "§e§l3-Day Free Trial",
                hasUsedTrial ? "§7You have already used your trial." : "§7Test the §6Exclusive §7rank for free!",
                hasUsedTrial ? " " : "§a▶ Click to activate!"
        ));

        
        menu.getInventory().setItem(31, createPaper(
                "§6§l7 Days Exclusive",
                "§7Subscribe for one week.",
                "§a▶ Buy on Discord Store"
        ));

        
        menu.getInventory().setItem(40, createPaper(
                "§6§l1 Month Exclusive",
                "§7Subscribe for a full month.",
                "§c§mOriginal Price§r §7| §a§l12% OFF!"
        ));


        
        
        

        
        menu.getInventory().setItem(24, createPaper(
                hasUsedTrial ? "§c§lFree Trial Used" : "§e§l3-Day Free Trial",
                hasUsedTrial ? "§7You have already used your trial." : "§7Test the §aVIP §7rank for free!",
                hasUsedTrial ? " " : "§a▶ Click to activate!"
        ));

        
        menu.getInventory().setItem(33, createPaper(
                "§a§l7 Days VIP",
                "§7Subscribe for one week.",
                "§a▶ Buy on Discord Store"
        ));

        
        menu.getInventory().setItem(42, createPaper(
                "§a§l1 Month VIP",
                "§7Subscribe for a full month.",
                "§c§mOriginal Price§r §7| §a§l15% OFF!"
        ));

        
        
        
        ItemStack staffItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta staffMeta = staffItem.getItemMeta();
        if (staffMeta != null) {
            staffMeta.setDisplayName("§d§lSTAFF APPLICATIONS");
            List<String> lore = new ArrayList<>();
            lore.add("§7Want to help the server?");
            lore.add(" ");
            lore.add("§fRoles Available:");
            lore.add("§8- §dEvent Host");
            lore.add("§8- §9Jr. Admin");
            lore.add(" ");
            lore.add("§e▶ Open a ticket on our Discord!");
            staffMeta.setLore(lore);
            staffItem.setItemMeta(staffMeta);
        }
        menu.getInventory().setItem(52, staffItem);

        p.openInventory(menu.getInventory());
    }

    private static ItemStack createPaper(String name, String lore1, String lore2) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(lore1);
            lore.add(" ");
            lore.add(lore2);
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        return paper;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}