package com.smpcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum KeyType {
    NORMAL("Normal Key", ChatColor.GREEN, Material.TRIPWIRE_HOOK, 1),
    RARE("Rare Key", ChatColor.BLUE, Material.TRIPWIRE_HOOK, 2),
    LEGENDARY("Legendary Key", ChatColor.GOLD, Material.TRIPWIRE_HOOK, 3),
    XTREME("Xtreme Key", ChatColor.RED, Material.TRIPWIRE_HOOK, 4),
    PRIME("Prime Key", ChatColor.LIGHT_PURPLE, Material.NETHER_STAR, 5),
    BUSINESS("Business Key", ChatColor.DARK_GREEN, Material.EMERALD, 6); 

    private final String displayName;
    private final ChatColor color;
    private final Material material;
    private final int tier;

    KeyType(String displayName, ChatColor color, Material material, int tier) {
        this.displayName = displayName;
        this.color = color;
        this.material = material;
        this.tier = tier;
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public Material getMaterial() { return material; }
    public int getTier() { return tier; }
}