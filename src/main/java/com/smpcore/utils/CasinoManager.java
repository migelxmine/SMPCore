package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.entity.Player;

import java.util.*;

public class CasinoManager {

    private final SMPCore plugin;
    private final EconomyManager eco;

    private final Map<UUID, MinesConfig> playerConfigs = new HashMap<>();
    private final Map<UUID, MinesSession> minesSessions = new HashMap<>();
    private final Map<UUID, DragonGame> dragonSessions = new HashMap<>();
    private final List<UUID> acceptedTerms = new ArrayList<>();

    public boolean hasAcceptedTerms(Player p) {
        return acceptedTerms.contains(p.getUniqueId());
    }

    public void acceptTerms(Player p) {
        if (!acceptedTerms.contains(p.getUniqueId())) {
            acceptedTerms.add(p.getUniqueId());
        }
    }

    public CasinoManager(SMPCore plugin, EconomyManager eco) {
        this.plugin = plugin;
        this.eco = eco;
    }


    public EconomyManager getEconomy() { return eco; }

    public MinesConfig getConfig(Player p) {
        playerConfigs.putIfAbsent(p.getUniqueId(), new MinesConfig());
        return playerConfigs.get(p.getUniqueId());
    }

    public boolean shouldWin(Player p, double chancePercentage) {
        return new Random().nextDouble() * 100 < chancePercentage;
    }

    public void openCasinoMenu(org.bukkit.entity.Player p) {
        
        com.smpcore.menus.CasinoMenu.open(p);
    }

    public void registerWin(Player p) { }

    

    public MinesSession getMinesSession(Player p) { return minesSessions.get(p.getUniqueId()); }

    public void startMines(Player p) {
        MinesConfig cfg = getConfig(p);
        minesSessions.put(p.getUniqueId(), new MinesSession(cfg.bet, cfg.mines));
    }

    public void endMines(Player p) { minesSessions.remove(p.getUniqueId()); }

    public static class MinesConfig {
        public double bet = 1000.0;
        public int mines = 3;
    }

    public static class MinesSession {
        public double bet;
        public int totalMines;
        public Set<Integer> clickedSlots;
        public double multiplier;
        public boolean isDead;

        public boolean[] grid;
        public boolean[] revealed;
        public boolean active;

        
        private final int GRID_SIZE = 20;

        public MinesSession(double bet, int totalMines) {
            this.bet = bet;
            this.totalMines = totalMines;
            this.multiplier = 0.5; 

            this.clickedSlots = new HashSet<>();
            this.active = true;
            this.isDead = false;

            this.grid = new boolean[GRID_SIZE];
            this.revealed = new boolean[GRID_SIZE];
            generateGrid();
        }

        private void generateGrid() {
            int placed = 0;
            Random rand = new Random();
            while (placed < totalMines) {
                int slot = rand.nextInt(GRID_SIZE);
                if (!grid[slot]) {
                    grid[slot] = true;
                    placed++;
                }
            }
        }

        public double getCurrentWin() {
            return bet * multiplier;
        }

        public void increaseMultiplier(int slot) {
            this.clickedSlots.add(slot);
            calculateMultiplier();
        }

        
        public void increaseMultiplier() {
            calculateMultiplier();
        }

        private void calculateMultiplier() {
            int clicksCount = clickedSlots.size();
            this.multiplier = 1.0; 
            double houseEdge = 0.97; 

            for (int i = 0; i < clicksCount; i++) {
                int remainingSafe = (GRID_SIZE - totalMines) - i;
                int remainingTiles = GRID_SIZE - i;
                double probability = (double) remainingSafe / remainingTiles;
                this.multiplier = (this.multiplier / probability) * houseEdge;
            }
            
            
        }

        public double getRealWinChance() {
            int currentClicks = clickedSlots.size();
            int remainingSafe = (GRID_SIZE - totalMines) - currentClicks;
            int remainingTiles = GRID_SIZE - currentClicks;
            if (remainingTiles == 0) return 0.0;
            return ((double) remainingSafe / remainingTiles) * 100.0;
        }
    }

    

    public DragonGame getDragonSession(Player p) { return dragonSessions.get(p.getUniqueId()); }

    public void startDragonGame(Player p, int difficulty) {
        MinesConfig cfg = getConfig(p);
        dragonSessions.put(p.getUniqueId(), new DragonGame(cfg.bet, difficulty));
    }

    public void endDragonGame(Player p) { dragonSessions.remove(p.getUniqueId()); }

    public static class DragonGame {
        public double bet;
        public int difficulty;
        public int currentRow;
        public boolean[][] grid;
        public boolean active;

        public DragonGame(double bet, int difficulty) {
            this.bet = bet;
            this.difficulty = difficulty;
            this.currentRow = 0;
            this.active = true;
            generateGrid();
        }

        private void generateGrid() {
            int columns = (difficulty == 0) ? 2 : (difficulty == 1) ? 3 : 4;
            this.grid = new boolean[5][columns];
            Random rand = new Random();
            for (int row = 0; row < 5; row++) {
                int winningCol = rand.nextInt(columns);
                for (int col = 0; col < columns; col++) {
                    grid[row][col] = (col == winningCol);
                }
            }
        }

        public double getCurrentWin() {
            if (currentRow == 0) return bet;
            double base = (difficulty == 0) ? 1.9 : (difficulty == 1) ? 2.9 : 3.8;
            return bet * Math.pow(base, currentRow);
        }

        public void openCasinoMenu(org.bukkit.entity.Player p) {
            
            
            com.smpcore.menus.CasinoMenu.open(p);
        }


    }

}