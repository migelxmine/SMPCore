package com.smpcore.utils;

public enum AuctionSort {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    LOWEST_PRICE("Lowest Price"),
    HIGHEST_PRICE("Highest Price");

    private final String name;

    AuctionSort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AuctionSort next() {
        
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}