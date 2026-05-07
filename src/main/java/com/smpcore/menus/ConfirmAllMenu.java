package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfirmAllMenu implements MigelSMPMenu {

    private Inventory inv;

    private ConfirmAllMenu() {}

    public static void open(Player p, Material material, EconomyManager eco, PriceManager priceManager, LangManager lang) {
        ConfirmAllMenu menu = new ConfirmAllMenu();

        
        String setName = getSetName(material);
        menu.inv = Bukkit.createInventory(menu, 27, "§8ᴄᴏɴꜰɪʀᴍ ꜱᴇᴛ: " + setName);

        MenuUtils.fillBorders(menu.inv, Material.GRAY_STAINED_GLASS_PANE);

        
        
        double totalCost = 0.0; 
        Material[] pieces = getArmorPieces(material);

        for (Material piece : pieces) {
            
            totalCost += priceManager.getPrice(piece);
        }

        
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7Includes full armor set:");
        lore.add("§7- Helmet");
        lore.add("§7- Chestplate");
        lore.add("§7- Leggings");
        lore.add("§7- Boots");
        lore.add(" ");
        lore.add("§7Total Cost: §e$" + String.format("%,.2f", totalCost));

        
        Material icon = pieces.length > 1 ? pieces[1] : material;

        menu.inv.setItem(13, ItemBuilder.of(icon, "§b§l" + setName + " SET", lore));


        menu.inv.setItem(11, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, "§c§lCANCEL"));


        menu.inv.setItem(15, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "§a§lCONFIRM", "§7Cost: $" + String.format("%,.2f", totalCost)));

        p.openInventory(menu.inv);

    }

    public static void handleClick(Player p, ItemStack clicked, EconomyManager eco, PriceManager priceManager, LangManager lang) {
        if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            p.closeInventory();
            return;
        }

        if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
            
            try {
                if (clicked.getItemMeta().getLore() == null) return;
                String loreLine = clicked.getItemMeta().getLore().get(0); 
                String costString = ChatColor.stripColor(loreLine).replace("Cost: $", "").replace(",", "");

                
                double cost = Double.parseDouble(costString);

                if (eco.has(p, cost)) {
                    
                    String title = ChatColor.stripColor(p.getOpenInventory().getTitle());
                    String setName = title.replace("Confirm Set: ", "");

                    Material baseMaterial = getMaterialFromSetName(setName);
                    Material[] pieces = getArmorPieces(baseMaterial);

                    
                    int emptySlots = 0;
                    for (ItemStack i : p.getInventory().getStorageContents()) {
                        if (i == null || i.getType() == Material.AIR) emptySlots++;
                    }

                    if (emptySlots >= pieces.length) {
                        eco.withdraw(p, cost);

                        for (Material piece : pieces) {
                            p.getInventory().addItem(new ItemStack(piece));
                            
                            priceManager.adjustPrice(piece, 1, true);
                        }

                        p.sendMessage(ChatColor.GREEN + "You bought the " + setName + " Set!");
                        Sounds.playSuccess(p);
                        p.closeInventory();
                    } else {
                        p.sendMessage(ChatColor.RED + "Not enough inventory space (Need 4 slots)!");
                        Sounds.playError(p);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Insufficient funds!");
                    Sounds.playError(p);
                }
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "An error occurred.");
                p.closeInventory();
            }
        }
    }

    

    private static String getSetName(Material mat) {
        if (mat == Material.IRON_INGOT) return "IRON";
        if (mat == Material.DIAMOND) return "DIAMOND";
        if (mat == Material.NETHERITE_INGOT) return "NETHERITE";
        if (mat == Material.GOLD_INGOT) return "GOLD";
        if (mat == Material.LEATHER) return "LEATHER";
        return "UNKNOWN";
    }

    private static Material getMaterialFromSetName(String name) {
        if (name.equals("IRON")) return Material.IRON_INGOT;
        if (name.equals("DIAMOND")) return Material.DIAMOND;
        if (name.equals("NETHERITE")) return Material.NETHERITE_INGOT;
        if (name.equals("GOLD")) return Material.GOLD_INGOT;
        if (name.equals("LEATHER")) return Material.LEATHER;
        return Material.IRON_INGOT; 
    }

    private static Material[] getArmorPieces(Material base) {
        String prefix = "";
        if (base == Material.IRON_INGOT) prefix = "IRON_";
        else if (base == Material.DIAMOND) prefix = "DIAMOND_";
        else if (base == Material.NETHERITE_INGOT) prefix = "NETHERITE_";
        else if (base == Material.GOLD_INGOT) prefix = "GOLDEN_";
        else if (base == Material.LEATHER) prefix = "LEATHER_";

        if (prefix.isEmpty()) return new Material[]{};

        return new Material[] {
                Material.valueOf(prefix + "HELMET"),
                Material.valueOf(prefix + "CHESTPLATE"),
                Material.valueOf(prefix + "LEGGINGS"),
                Material.valueOf(prefix + "BOOTS")
        };
    }

    @Override
    public Inventory getInventory() { return inv; }
}