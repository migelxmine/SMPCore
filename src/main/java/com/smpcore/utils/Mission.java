package com.smpcore.utils;

import org.bukkit.Material;

public class Mission {
    private final String name;
    private final Material inputMaterial;
    private final int inputAmount;
    private final double minPurity; 
    private final int rewardChocolate;

    public Mission(String name, Material inputMaterial, int inputAmount, double minPurity, int rewardChocolate) {
        this.name = name;
        this.inputMaterial = inputMaterial;
        this.inputAmount = inputAmount;
        this.minPurity = minPurity;
        this.rewardChocolate = rewardChocolate;
    }

    
    public String getName() { return name; }
    public Material getInputMaterial() { return inputMaterial; }
    public int getInputAmount() { return inputAmount; }
    public double getMinPurity() { return minPurity; }
    public int getRewardChocolate() { return rewardChocolate; }
}