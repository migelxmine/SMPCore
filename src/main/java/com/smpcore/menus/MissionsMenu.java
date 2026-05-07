package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.Mission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MissionsMenu implements InventoryHolder {

    private final Inventory inv;

    public MissionsMenu(SMPCore plugin) {
        this.inv = Bukkit.createInventory(this, 45, "§8Active Missions");

        List<Mission> missions = plugin.getMissionManager().getMissions();

        
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

        for (int i = 0; i < missions.size(); i++) {
            if (i >= slots.length) break;

            Mission m = missions.get(i);
            inv.setItem(slots[i], createMissionBook(m));
        }

        
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < 45; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, glass);
        }
    }

    private ItemStack createMissionBook(Mission m) {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();

        
        meta.setDisplayName("§6§lCONTRACT: §e" + m.getInputAmount() + "x " + formatName(m.getInputMaterial()) + " §6➡ §f" + m.getRewardChocolate() + " Choco");

        
        List<String> lore = new ArrayList<>();
        lore.add("§8Mission ID: #" + m.getName().hashCode()); 
        lore.add(" ");
        lore.add("§7Client Request:");
        lore.add("§c▪ Item: §f" + formatName(m.getInputMaterial()));
        lore.add("§c▪ Amount: §f" + m.getInputAmount());
        lore.add("§c▪ Min. Purity: §e" + m.getMinPurity() + "%");
        lore.add(" ");
        lore.add("§7Reward:");
        lore.add("§a▪ " + m.getRewardChocolate() + " Chocolate Blocks");
        lore.add(" ");
        lore.add("§eClick to Accept & Trade!");

        meta.setLore(lore);

        
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        book.setItemMeta(meta);
        return book;
    }

    
    private String formatName(Material m) {
        String[] parts = m.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static void open(Player p, SMPCore plugin) {
        p.openInventory(new MissionsMenu(plugin).getInventory());
    }

    @Override
    public Inventory getInventory() { return inv; }
}