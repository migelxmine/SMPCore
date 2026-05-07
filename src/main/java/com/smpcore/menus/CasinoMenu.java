package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CasinoMenu implements MigelSMPMenu {

    private Inventory inv;

    private CasinoMenu() {}

    public static void open(Player p) {
        CasinoMenu menu = new CasinoMenu();
        
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lᴄᴀsɪɴᴏ ʜᴜʙ");

        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§6§l" + p.getName());
            List<String> headLore = new ArrayList<>();
            headLore.add("§7Feeling lucky today?");
            headLore.add(" ");
            headLore.add("§7Remember: The house");
            headLore.add("§7always wins... eventually.");
            meta.setLore(headLore);
            head.setItemMeta(meta);
        }
        menu.inv.setItem(4, head);

        

        
        List<String> rouletteLore = new ArrayList<>();
        rouletteLore.add("§8High Stakes Wheel");
        rouletteLore.add(" ");
        rouletteLore.add("§7Pick a color and pray.");
        rouletteLore.add("§7Fast paced action.");
        rouletteLore.add(" ");
        rouletteLore.add("§cRed (2x) §8| §0Black (2x) §8| §aGreen (14x)");
        rouletteLore.add(" ");
        rouletteLore.add("§eClick to Spin ➡");
        menu.inv.setItem(19, ItemBuilder.of(Material.MAGMA_CREAM, "§c§lʀᴏᴜʟᴇᴛᴛᴇ", rouletteLore));

        
        List<String> slotsLore = new ArrayList<>();
        slotsLore.add("§8The One-Armed Bandit");
        slotsLore.add(" ");
        slotsLore.add("§7Spin the reels across");
        slotsLore.add("§65 Unique Machines§7.");
        slotsLore.add(" ");
        slotsLore.add("§dClassic, Nether, Aquatic,");
        slotsLore.add("§dMining & Crypto themes.");
        slotsLore.add(" ");
        slotsLore.add("§6Click to Play ➡");
        menu.inv.setItem(21, ItemBuilder.of(Material.ENCHANTED_GOLDEN_APPLE, "§6§lＳＬＯＴＳ", slotsLore));

        
        List<String> minesLore = new ArrayList<>();
        minesLore.add("§8Strategic Betting");
        minesLore.add(" ");
        minesLore.add("§7Avoid the bombs.");
        minesLore.add("§7Cash out anytime.");
        minesLore.add(" ");
        minesLore.add("§7Multiplier increases with");
        minesLore.add("§7every safe click.");
        minesLore.add(" ");
        minesLore.add("§cClick to Defuse ➡");
        menu.inv.setItem(23, ItemBuilder.of(Material.TNT, "§4§lᴍɪɴᴇs", minesLore));

        
        List<String> lotLore = new ArrayList<>();
        lotLore.add("§8Instant Scratch Card");
        lotLore.add(" ");
        lotLore.add("§7Buy a ticket (§a$500§7).");
        lotLore.add("§7Check for a match.");
        lotLore.add(" ");
        lotLore.add("§9Anti-Fraud Protected");
        lotLore.add(" ");
        lotLore.add("§9Click to Buy ➡");
        menu.inv.setItem(25, ItemBuilder.of(Material.PAPER, "§9§lʟᴏᴛᴛᴇʀʏ", lotLore));

        List<String> dragonLore = new ArrayList<>();
        dragonLore.add("§8Climb the Tower");
        dragonLore.add(" ");
        dragonLore.add("§7Avoid the fire.");
        dragonLore.add("§7Reach the top.");
        dragonLore.add(" ");
        dragonLore.add("§dClick to Climb ➡");
        menu.inv.setItem(22, ItemBuilder.of(Material.DRAGON_HEAD, "§d§lᴅʀᴀɢᴏɴ ᴛᴏᴡᴇʀ", dragonLore));

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);

        
        ItemStack deco = ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE, " ");
        menu.inv.setItem(10, deco); menu.inv.setItem(16, deco);
        menu.inv.setItem(28, deco); menu.inv.setItem(34, deco);

        

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}