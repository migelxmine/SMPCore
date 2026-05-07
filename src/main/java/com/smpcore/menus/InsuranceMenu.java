package com.smpcore.menus;

import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InsuranceMenu implements MigelSMPMenu {

    private Inventory inv;

    private InsuranceMenu() {}

    public static void open(Player p, InsuranceManager manager) {
        InsuranceMenu menu = new InsuranceMenu();
        menu.inv = Bukkit.createInventory(menu, 27, "§8§lɪɴsᴜʀᴀɴᴄᴇ ᴀɢᴇɴᴛ");

        
        boolean isActive = manager.hasActiveDeathInsurance(p);
        Material deathMat = isActive ? Material.TOTEM_OF_UNDYING : Material.SKELETON_SKULL;

        List<String> dLore = new ArrayList<>();
        dLore.add("§7Keep Inventory on Death.");
        dLore.add("§7Valid for §f7 Days §7(Real Time).");
        dLore.add(" ");

        if (isActive) {
            dLore.add("§a§lACTIVE");
            dLore.add("§7Expires in: §e" + manager.getTimeLeft(p));
            dLore.add(" ");
            dLore.add("§7(Consumes on death)");
        } else {
            double cost = manager.getDeathPrice(p);
            dLore.add("§c§lINACTIVE");
            dLore.add("§7Your Dynamic Price:");
            dLore.add("§a$" + String.format("%,.0f", cost));
            dLore.add("§8(Base $1k + 5% Balance)");
            dLore.add(" ");
            dLore.add("§eClick to Purchase ➡");
        }

        menu.inv.setItem(11, ItemBuilder.of(deathMat, "§9§lDEATH INSURANCE", dLore));

        
        boolean hasBank = manager.hasBankruptcyInsurance(p);
        Material bankMat = hasBank ? Material.GOLD_BLOCK : Material.IRON_BARS;

        List<String> bLore = new ArrayList<>();
        bLore.add("§7Get $50,000 bailout if");
        bLore.add("§7balance drops below $100.");
        bLore.add(" ");

        if (hasBank) {
            bLore.add("§a§lACTIVE");
            bLore.add("§7You are covered.");
        } else {
            bLore.add("§c§lINACTIVE");
            bLore.add("§7Cost: §a$20,000");
            bLore.add(" ");
            bLore.add("§eClick to Purchase ➡");
        }

        menu.inv.setItem(15, ItemBuilder.of(bankMat, "§6§lBANKRUPTCY COVERAGE", bLore));

        MenuUtils.fillBorders(menu.inv, Material.BLUE_STAINED_GLASS_PANE);
        MenuUtils.addBackButton(menu.inv);

        p.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}