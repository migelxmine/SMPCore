package com.smpcore.menus;

import com.smpcore.SMPCore;
import com.smpcore.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu {

    private static final String GUI_TITLE = "§8sᴇᴛᴛɪɴɢs";

    
    public static void open(Player p, SettingsManager settings) {
        
        Inventory inv = Bukkit.createInventory(null, 36, GUI_TITLE);

        
        inv.setItem(0, createItem(Material.OAK_SIGN, "§aᴘᴜʙʟɪᴄ ᴄʜᴀᴛ", "public_chat", p, settings));
        inv.setItem(1, createItem(Material.DARK_OAK_SIGN, "§aᴘʀɪᴠᴀᴛᴇ ᴍᴇssᴀɢᴇs", "private_messages", p, settings));
        inv.setItem(2, createItem(Material.WARPED_SIGN, "§aᴄʜᴀᴛ sᴇʀᴠᴇʀ ᴍᴇssᴀɢᴇs", "chat_server_msgs", p, settings));
        
        inv.setItem(3, createItem(Material.ACACIA_SIGN, "§aᴀᴜᴄᴛɪᴏɴ ᴀʟᴇʀᴛs", "auction_alerts", p, settings));

        
        
        inv.setItem(9, createItem(Material.TOTEM_OF_UNDYING, "§aᴛᴏᴛᴇᴍ ᴘᴀʀᴛɪᴄʟᴇs", "totem_particles", p, settings));
        inv.setItem(10, createItem(Material.TNT, "§aᴇxᴘʟᴏsɪᴏɴ ᴘᴀʀᴛɪᴄʟᴇs", "explosion_particles", p, settings));
        
        inv.setItem(11, createItem(Material.ZOMBIE_HEAD, "§aᴅɪsᴀʙʟᴇ ᴍᴏʙ sᴘᴀᴡɴs", "disable_mob_spawns", p, settings));

        
        inv.setItem(18, createItem(Material.PLAYER_HEAD, "§aᴘʟᴀʏᴇʀ ᴠɪsɪʙɪʟɪᴛʏ", "player_visibility", p, settings));
        inv.setItem(19, createItem(Material.LECTERN, "§asᴄᴏʀᴇʙᴏᴀʀᴅ", "scoreboard", p, settings));
        inv.setItem(20, createItem(Material.FEATHER, "§aᴛᴘᴀ ᴄᴏɴғɪʀᴍ ᴍᴇɴᴜ", "tpa_confirm_menu", p, settings));
        inv.setItem(21, createItem(Material.MUSIC_DISC_PIGSTEP, "§asᴏᴜɴᴅ ɴᴏᴛɪғɪᴄᴀɴᴛɪᴏɴs", "sound_notifications", p, settings));
        inv.setItem(22, createItem(Material.FILLED_MAP, "§aᴏʀᴅᴇʀ ɴᴏᴛɪғɪᴄᴀᴛɪᴏɴ", "order_notification", p, settings));
        

        
        
        inv.setItem(27, createItem(Material.ENDER_PEARL, "§aᴛᴘᴀ ʀᴇϙᴜᴇsᴛs", "tpa_requests", p, settings));
        
        inv.setItem(28, createItem(Material.EMERALD, "§aᴘᴀʏᴍᴇɴᴛs", "payments", p, settings));
        
        inv.setItem(29, createItem(Material.FIREWORK_ROCKET, "§aϙᴜɪᴇᴛ sᴘᴀᴡɴ", "quiet_spawn", p, settings));

        p.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name, String key, Player p, SettingsManager settings) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        boolean status = settings.getSetting(p, key);

        if (status) {
            lore.add("§fcurrent: §a§lON");
        } else {
            lore.add("§fcurrent: §c§lOFF");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}