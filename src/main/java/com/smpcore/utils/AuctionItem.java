package com.smpcore.utils;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class AuctionItem {
    private final UUID id;
    private final UUID seller;
    private final ItemStack item;
    private final double price;
    private final long timestamp;

    public AuctionItem(UUID id, UUID seller, ItemStack item, double price, long timestamp) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.timestamp = timestamp;
    }

    public UUID getId() { return id; }
    public UUID getSeller() { return seller; }
    public ItemStack getItem() { return item; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
}