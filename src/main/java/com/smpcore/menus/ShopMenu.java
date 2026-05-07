package com.smpcore.menus;

import com.smpcore.utils.EconomyManager;
import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuHistory;
import com.smpcore.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu implements MigelSMPMenu {

    private Inventory inv;

    private ShopMenu() {}

    public static void open(Player p, LangManager lang, EconomyManager eco) {
        ShopMenu menu = new ShopMenu();
        
        menu.inv = Bukkit.createInventory(menu, 54, "§8§lᴍᴀʀᴋᴇᴛᴘʟᴀᴄᴇ");

        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§9§l" + p.getName());
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7Your Balance:");
            lore.add("§a$" + String.format("%,.2f", eco.getBalance(p)));
            lore.add(" ");
            lore.add("§eSelect a category to shop.");
            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        menu.inv.setItem(4, head);

        

        
        menu.inv.setItem(20, ItemBuilder.of(Material.GRASS_BLOCK, "§a§lᴏᴠᴇʀᴡᴏʀʟᴅ",
                "§7Blocks, Woods, Nature items.", " ", "§eClick to Browse ➡"));

        
        menu.inv.setItem(22, ItemBuilder.of(Material.NETHERRACK, "§c§lɴᴇᴛʜᴇʀ",
                "§7Netherrack, Quartz, Glowstone.", " ", "§eClick to Browse ➡"));

        
        menu.inv.setItem(24, ItemBuilder.of(Material.END_STONE, "§5§lᴛʜᴇ ᴇɴᴅ",
                "§7End Stone, Purpur, Rods.", " ", "§eClick to Browse ➡"));

        

        
        menu.inv.setItem(29, ItemBuilder.of(Material.DIAMOND_CHESTPLATE, "§9§lɢᴇᴀʀ & ᴀʀᴍᴏʀ",
                "§7Weapons, Tools, Protection.", " ", "§eClick to Browse ➡"));

        
        menu.inv.setItem(30, ItemBuilder.of(Material.REDSTONE, "§4§lʀᴇᴅsᴛᴏɴᴇ",
                "§7Mechanisms and logic.", " ", "§eClick to Browse ➡"));

        
        menu.inv.setItem(32, ItemBuilder.of(Material.HOPPER, "§7§lᴜᴛɪʟɪᴛɪᴇs",
                "§7Buckets, Rails, Misc.", " ", "§eClick to Browse ➡"));

        
        menu.inv.setItem(33, ItemBuilder.of(Material.GOLDEN_CARROT, "§6§lғᴏᴏᴅ & ғᴀʀᴍɪɴɢ",
                "§7Stay fed and healthy.", " ", "§eClick to Browse ➡"));

        
        MenuUtils.fillBorders(menu.inv, Material.BLACK_STAINED_GLASS_PANE);
        
        MenuHistory.setPrevious(p, "MainMenu");

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}