package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuctionManager {

    private final SMPCore plugin;
    private final List<AuctionItem> activeAuctions = new ArrayList<>();
    private File file;
    private FileConfiguration config;

    public AuctionManager(SMPCore plugin) {
        this.plugin = plugin;
        loadAuctions();
    }

    public void addAuction(UUID seller, ItemStack item, double price) {
        AuctionItem auction = new AuctionItem(UUID.randomUUID(), seller, item, price, System.currentTimeMillis());
        activeAuctions.add(auction);
        saveAuctions();
    }

    public void removeAuction(AuctionItem auction) {
        activeAuctions.remove(auction);
        saveAuctions();
    }

    public List<AuctionItem> getAuctions() {
        return activeAuctions;
    }

    
    public List<AuctionItem> getSortedAuctions(AuctionSort sort, AuctionCategory category, String query) {
        List<AuctionItem> filteredStream = new ArrayList<>(activeAuctions);

        
        if (category != AuctionCategory.ALL) {
            filteredStream = filteredStream.stream()
                    .filter(a -> isItemInCategory(a.getItem(), category))
                    .collect(Collectors.toList());
        }

        
        if (query != null && !query.isEmpty()) {
            String q = query.toLowerCase();
            filteredStream = filteredStream.stream()
                    .filter(a -> {
                        String type = a.getItem().getType().name().toLowerCase();
                        String display = a.getItem().hasItemMeta() && a.getItem().getItemMeta().hasDisplayName()
                                ? a.getItem().getItemMeta().getDisplayName().toLowerCase()
                                : "";
                        return type.contains(q) || display.contains(q);
                    })
                    .collect(Collectors.toList());
        }

        
        switch (sort) {
            case NEWEST:
                filteredStream.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case OLDEST:
                filteredStream.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
                break;
            case LOWEST_PRICE:
                filteredStream.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case HIGHEST_PRICE:
                filteredStream.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
        }

        return filteredStream;
    }

    
    private boolean isItemInCategory(ItemStack item, AuctionCategory category) {
        Material type = item.getType();
        String name = type.name();

        switch (category) {
            case BLOCKS:
                return type.isBlock();
            case GEAR:
                return name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_PICKAXE")
                        || name.endsWith("_SHOVEL") || name.endsWith("_HOE") || name.endsWith("_HELMET")
                        || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS")
                        || type == Material.BOW || type == Material.CROSSBOW || type == Material.SHIELD
                        || type == Material.TRIDENT || type == Material.ELYTRA;
            case MISC:
                return !type.isBlock() && !isItemInCategory(item, AuctionCategory.GEAR);
            default:
                return true;
        }
    }

    public AuctionItem getAuctionByUUID(UUID id) {
        for (AuctionItem auction : activeAuctions) {
            if (auction.getId().equals(id)) return auction;
        }
        return null;
    }

    private void loadAuctions() {
        file = new File(plugin.getDataFolder(), "auctions.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("auctions")) {
            for (String key : config.getConfigurationSection("auctions").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    UUID seller = UUID.fromString(config.getString("auctions." + key + ".seller"));
                    double price = config.getDouble("auctions." + key + ".price");
                    long time = config.getLong("auctions." + key + ".time");
                    ItemStack item = config.getItemStack("auctions." + key + ".item");

                    activeAuctions.add(new AuctionItem(id, seller, item, price, time));
                } catch (Exception e) {
                    plugin.getLogger().warning("Error loading auction " + key);
                }
            }
        }
    }

    public void saveAuctions() {
        if (file == null) return;
        config.set("auctions", null);

        for (AuctionItem auction : activeAuctions) {
            String path = "auctions." + auction.getId().toString();
            config.set(path + ".seller", auction.getSeller().toString());
            config.set(path + ".price", auction.getPrice());
            config.set(path + ".time", auction.getTimestamp());
            config.set(path + ".item", auction.getItem());
        }

        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}