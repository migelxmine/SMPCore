package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class BankMenu implements MigelSMPMenu {

    private Inventory inv;

    private BankMenu() {}

    public static void open(Player p, LangManager lang, EconomyManager eco, SuperMoneyManager sm) {
        BankMenu menu = new BankMenu();
        
        menu.inv = Bukkit.createInventory(menu, 45, "§8§lᴘᴇʀsᴏɴᴀʟ ʙᴀɴᴋ");

        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§9§l" + p.getName() + "'s ᴀᴄᴄᴏᴜɴᴛ");

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7Current Balance:");
            lore.add("§f$" + String.format("%,.2f", eco.getBalance(p)));
            lore.add(" ");
            lore.add("§7Super Money:");
            lore.add("§d" + String.format("%,.2f", sm.getBalance(p)) + " SM");
            lore.add(" ");
            lore.add("§8§oYour financial status.");

            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        
        menu.inv.setItem(4, head);

        

        
        List<String> sellLore = new ArrayList<>();
        sellLore.add("§7Sell items to the server");
        sellLore.add("§7to earn money.");
        sellLore.add(" ");
        sellLore.add("§aClick to Open ➡");
        menu.inv.setItem(20, ItemBuilder.of(Material.CHEST, "§a§lsᴇʟʟ ɪᴛᴇᴍs", sellLore));

        
        List<String> cryptoLore = new ArrayList<>();
        cryptoLore.add("§7Invest in the market");
        cryptoLore.add("§7and grow your wealth.");
        cryptoLore.add(" ");
        cryptoLore.add("§6Click to Open ➡");
        menu.inv.setItem(24, ItemBuilder.of(Material.GOLD_BLOCK, "§6§lᴄʀʏᴘᴛᴏ ᴍᴀʀᴋᴇᴛ", cryptoLore));

        List<String> casinoLore = new ArrayList<>();
        casinoLore.add("§7Play Roulette and Mines.");
        casinoLore.add("§7Win big or lose it all.");
        casinoLore.add(" ");
        casinoLore.add("§dClick to Play ➡");
        menu.inv.setItem(22, ItemBuilder.of(Material.NOTE_BLOCK, "§d§lᴄᴀsɪɴᴏ", casinoLore));

        List<String> insLore = new ArrayList<>();
        insLore.add("§7Death & Bankruptcy Protection.");
        insLore.add(" ");
        insLore.add("§9Click to View Plans ➡");
        menu.inv.setItem(31, ItemBuilder.of(Material.SHIELD, "§9§lɪɴsᴜʀᴀɴᴄᴇ", insLore));

        
        MenuUtils.fillBorders(menu.inv, Material.CYAN_STAINED_GLASS_PANE); 

        MenuUtils.addBackButton(menu.inv); 
        MenuHistory.setPrevious(p, "MainMenu");

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}