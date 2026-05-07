package com.smpcore.utils;

public enum AuctionCategory {
    ALL("All Items"),
    BLOCKS("Blocks"),
    GEAR("Weapons & Armor"),
    MISC("Miscellaneous");

    private final String name;

    AuctionCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AuctionCategory next() {
        
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}