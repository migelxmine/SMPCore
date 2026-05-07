/*
 * Copyright (c) 2025 Miguel Martinho Morbey Rodrigues Moreira (MigelSMP/SMPCore)
 * All Rights Reserved.
 *
 * Este software é propriedade confidencial e proprietária de Miguel Martinho Morbey Rodrigues Moreira.
 * ("Informação Confidencial"). Você não deve divulgar tal Informação
 * Confidencial e deve usá-la apenas de acordo com os termos do
 * contrato de licença que você celebrou com Miguel Martinho Morbey Rodrigues Moreira.
 *
 * É ESTRITAMENTE PROIBIDO DESCOMPILAR, MODIFICAR OU REDISTRIBUIR ESTE SOFTWARE.
 */

package com.smpcore.menus;

import com.smpcore.utils.BountyManager;
import com.smpcore.utils.ItemBuilder;
import com.smpcore.utils.LangManager;
import com.smpcore.utils.MenuHistory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BountyMenu implements MigelSMPMenu {
    private Inventory inv;
    private static final int ITEMS_PER_PAGE = 45;

    private BountyMenu() {}

    public static void open(Player player, BountyManager bountyManager, int page, LangManager lang) {
        BountyMenu menu = new BountyMenu();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(player);
        int totalPages = (int) Math.ceil((double) onlinePlayers.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        String title = lang.getMessage("menus.bounties.title", "%page%", String.valueOf(page), "%totalPages%", String.valueOf(totalPages));
        menu.inv = Bukkit.createInventory(menu, 54, title);

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int playerIndex = startIndex + i;
            if (playerIndex < onlinePlayers.size()) {
                Player target = onlinePlayers.get(playerIndex);
                double bounty = bountyManager.getBounty(target.getUniqueId());
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(target);
                    meta.setDisplayName(ChatColor.YELLOW + target.getName());
                    List<String> lore = lang.getMessages().getStringList("menus.bounties.player-lore").stream()
                            .map(line -> ChatColor.translateAlternateColorCodes('&', line.replace("%bounty%", String.format("%,.2f", bounty))))
                            .collect(Collectors.toList());
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                }
                menu.inv.setItem(i, head);
            }
        }

        menu.inv.setItem(49, ItemBuilder.of(Material.BARRIER, lang.getMessage("menus.bounties.buttons.back")));
        MenuHistory.setPrevious(player, "MainMenu");
        if (page > 1) menu.inv.setItem(48, ItemBuilder.of(Material.ARROW, lang.getMessage("menus.bounties.buttons.previous_page")));
        if (page < totalPages) menu.inv.setItem(50, ItemBuilder.of(Material.ARROW, lang.getMessage("menus.bounties.buttons.next_page")));
        player.openInventory(menu.inv);
    }

    @Override
    public Inventory getInventory() { return inv; }
}