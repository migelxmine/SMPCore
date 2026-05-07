package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class HomesMenu implements MigelSMPMenu {

    private Inventory inv;

    private HomesMenu() {}

    public static void open(Player p, HomeManager manager) {
        HomesMenu menu = new HomesMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lʜᴏᴍᴇ ᴍᴀɴᴀɢᴇʀ");

        
        SMPCore plugin = (SMPCore) Bukkit.getPluginManager().getPlugin("SMPCore");
        RankManager.SocialRank currentRank = plugin.getRankManager().getSocialRank(p);

        
        int maxHomes = 2; 
        if (currentRank == RankManager.SocialRank.EXCLUSIVE) maxHomes = 4;
        else if (currentRank == RankManager.SocialRank.VIP) maxHomes = 5; 

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§9§l" + p.getName() + "'s ʜᴏᴍᴇs");
            List<String> headLore = new ArrayList<>();
            headLore.add("§7Manage your personal waypoints.");
            headLore.add(" ");

            int used = 0;
            for(int i=1; i<=5; i++) if(manager.getHome(p, i) != null) used++;

            headLore.add("§7Status: §f" + used + "/" + (currentRank == RankManager.SocialRank.VIP ? "Unlimited" : maxHomes) + " §7homes set.");
            headLore.add("§7Rank: " + currentRank.getDisplay());
            meta.setLore(headLore);
            head.setItemMeta(meta);
        }
        menu.inv.setItem(4, head);

        int[] slots = {11, 12, 13, 14, 15};
        boolean inSpawn = p.getWorld().getName().equals("SMP_Spawn");

        for (int i = 0; i < slots.length; i++) {
            int homeNum = i + 1;
            int slot = slots[i];

            if (homeNum > maxHomes) {
                
                menu.inv.setItem(slot, ItemBuilder.of(Material.BARRIER, "§c§lＬＯＣＫＥＤ", "§7Subscribe to §6Exclusive §7or §aVIP", "§7to unlock more slots! (/ranks)"));
            } else {
                if (manager.getHome(p, homeNum) != null) {
                    List<String> lore = new ArrayList<>();
                    lore.add("§7Location set.");
                    lore.add(" ");
                    lore.add("§aLeft-Click §7to Teleport");
                    lore.add("§cRight-Click §7to Delete");

                    menu.inv.setItem(slot, ItemBuilder.of(Material.LIME_BED, "§a§lʜᴏᴍᴇ #" + homeNum, lore));
                } else {
                    List<String> lore = new ArrayList<>();
                    lore.add("§7Empty Slot.");
                    lore.add(" ");

                    if (inSpawn) {
                        lore.add("§c§lUNAVAILABLE");
                        lore.add("§cCannot set home in Spawn!");
                    } else {
                        lore.add("§7Stand at the location you want");
                        lore.add("§7and click to set this home.");
                        lore.add(" ");
                        lore.add("§eClick to Set ➡");
                    }

                    menu.inv.setItem(slot, ItemBuilder.of(Material.LIGHT_GRAY_BED, "§7§lᴇᴍᴘᴛʏ sʟᴏᴛ #" + homeNum, lore));
                }
            }
        }

        MenuUtils.fillBorders(menu.inv, Material.GRAY_STAINED_GLASS_PANE);
        MenuHistory.setPrevious(p, "TeleportMenu");

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}