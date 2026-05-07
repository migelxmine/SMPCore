package com.smpcore.menus;

import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MainMenu implements MigelSMPMenu {

    private Inventory inv;

    private MainMenu() {}

    public static void open(Player p, LangManager lang) {
        MainMenu menu = new MainMenu();
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lᴍᴀɪɴ ᴍᴇɴᴜ");

        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§7User: §f§n" + p.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§8Player Profile");
            lore.add(" ");
            lore.add("§7Welcome back!");
            lore.add("§7Select a module below.");
            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        menu.inv.setItem(4, head);

        

        
        menu.inv.setItem(10, ItemBuilder.of(Material.GOLD_INGOT, "§6§lʙᴀɴᴋ",
                "§8Economy & Crypto", " ", "§7Manage your accounts.", " ", "§eClick to Access ➡"));

        
        menu.inv.setItem(12, ItemBuilder.of(Material.EMERALD, "§a§lsʜᴏᴘ",
                "§8Server Market", " ", "§7Buy blocks and items.", " ", "§aClick to Browse ➡"));

        
        menu.inv.setItem(14, ItemBuilder.of(Material.GOLDEN_HORSE_ARMOR, "§e§lᴀᴜᴄᴛɪᴏɴ ʜᴏᴜsᴇ",
                "§8Player Marketplace", " ", "§7Trade with players.", " ", "§eClick to Open ➡"));

        
        menu.inv.setItem(16, ItemBuilder.of(Material.ENDER_PEARL, "§9§lᴛᴇʟᴇᴘᴏʀᴛs",
                "§8Travel System", " ", "§7Warps, Homes, RTP.", " ", "§9Click to Travel ➡"));


        

        
        List<String> vaultLore = new ArrayList<>();
        vaultLore.add("§8Personal Storage");
        vaultLore.add(" ");
        vaultLore.add("§7Your secure digital safe.");
        vaultLore.add("§7Upgradeable capacity.");
        vaultLore.add(" ");
        vaultLore.add("§5Click to Open ➡");
        menu.inv.setItem(19, ItemBuilder.of(Material.ENDER_CHEST, "§5§lᴠᴀᴜʟᴛ", vaultLore));

        
        menu.inv.setItem(21, ItemBuilder.of(Material.NETHERITE_SWORD, "§4§lᴘᴠᴘ ʜᴜʙ",
                "§8Combat Zone", " ", "§c⚔ Duels & Training", "§6☠ Bounties", " ", "§cClick to Fight ➡"));

        
        menu.inv.setItem(23, ItemBuilder.of(Material.COMPARATOR, "§7§lsᴇᴛᴛɪɴɢs",
                "§8Configuration", " ", "§7Toggle chats and sounds.", " ", "§7Click to Configure ➡"));

        
        menu.inv.setItem(25, ItemBuilder.of(Material.NETHER_STAR, "§d§lɢᴏᴅ ᴛᴏᴏʟs",
                "§8Premium Store", " ", "§7Super Money Items.", " ", "§dClick to View ➡"));

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);

        
        ItemStack gray = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE, " ");
        menu.inv.setItem(0, gray); menu.inv.setItem(8, gray);
        menu.inv.setItem(36, gray); menu.inv.setItem(44, gray);

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}