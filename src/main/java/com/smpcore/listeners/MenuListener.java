package com.smpcore.listeners;

import com.smpcore.SMPCore;
import com.smpcore.menus.*;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MenuListener implements Listener {

    private final SMPCore plugin;

    
    private final PriceManager priceManager;
    private final EconomyManager economyManager;
    private final HomeManager homeManager;
    private final SellManager sellManager;
    private final CryptoManager cryptoManager;
    private final TprManager tprManager;
    private final BountyManager bountyManager;
    private final SettingsManager settingsManager;
    private final EnchantmentPriceManager enchantmentPriceManager;
    private final LangManager langManager;
    private final SuperMoneyManager superMoneyManager;
    private final CustomItemManager customItemManager;
    private final AuctionManager auctionManager;
    private final OrderManager orderManager;

    private final Map<UUID, Long> lastClick = new HashMap<>();
    private static final long CLICK_DELAY_MS = 250;

    public MenuListener(SMPCore plugin, PriceManager priceManager, EconomyManager economyManager,
                        HomeManager homeManager, SellManager sellManager, CryptoManager cryptoManager,
                        TprManager tprManager, BountyManager bountyManager, SettingsManager settingsManager,
                        EnchantmentPriceManager enchantmentPriceManager, LangManager langManager,
                        SuperMoneyManager superMoneyManager, CustomItemManager customItemManager,
                        AuctionManager auctionManager, OrderManager orderManager) {
        this.plugin = plugin;
        this.priceManager = priceManager;
        this.economyManager = economyManager;
        this.homeManager = homeManager;
        this.sellManager = sellManager;
        this.cryptoManager = cryptoManager;
        this.tprManager = tprManager;
        this.bountyManager = bountyManager;
        this.settingsManager = settingsManager;
        this.enchantmentPriceManager = enchantmentPriceManager;
        this.langManager = langManager;
        this.superMoneyManager = superMoneyManager;
        this.customItemManager = customItemManager;
        this.auctionManager = auctionManager;
        this.orderManager = orderManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();

        long now = System.currentTimeMillis();
        if (lastClick.containsKey(p.getUniqueId())) {
            if (now - lastClick.get(p.getUniqueId()) < CLICK_DELAY_MS) {
                e.setCancelled(true);
                return;
            }
        }
        lastClick.put(p.getUniqueId(), now);

        Inventory topInv = e.getView().getTopInventory();
        InventoryHolder holder = topInv.getHolder();
        String title = e.getView().getTitle();
        ItemStack clickedItem = e.getCurrentItem();

        
        if (title.equals("§8sᴇᴛᴛɪɴɢs")) {
            if (e.getClickedInventory() != null && !e.getClickedInventory().equals(topInv)) {
                if (e.isShiftClick()) e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String key = getSettingKey(e.getSlot());
            if (key != null) {
                settingsManager.toggleSetting(p, key);
                Sounds.playClick(p);
                SettingsMenu.open(p, settingsManager);
            }
            return;
        }

        if (e.getView().getTitle().equals("§8Build Memory - Mode")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            if (e.getCurrentItem().getType() == org.bukkit.Material.FEATHER) {
                p.closeInventory();
                plugin.getMinigameManager().joinBuildMemory(p, false); // Solo Mode
            } else if (e.getCurrentItem().getType() == org.bukkit.Material.DIAMOND_SWORD) {
                p.closeInventory();
                plugin.getMinigameManager().joinBuildMemory(p, true); // Ranked Mode
            }
            return;
        }

        if (title.equals("§8§lS T A F F   P A N E L")) {
            e.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (name.contains("SERVER STATUS")) {
                p.closeInventory();
                p.performCommand("smpstatus");
            } else if (name.contains("MANAGE PLAYERS")) {
                Sounds.playClick(p);
                com.smpcore.menus.StaffMenu.openPlayerList(p);
            } else if (name.contains("GLOBAL ECONOMY")) {
                p.closeInventory();
                p.performCommand("economy freeze server Staff Panel Request");
            }
            return;
        }

        if (title.equals("§8§lO N L I N E   P L A Y E R S")) {
            e.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) return;

            String targetName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                Sounds.playClick(p);
                com.smpcore.menus.StaffMenu.openPlayerActions(p, target, plugin);
            } else {
                p.sendMessage("§cPlayer is no longer online.");
                p.closeInventory();
            }
            return;
        }

        if (title.startsWith("§8§lM A N A G E:")) {
            e.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String targetName = title.replace("§8§lM A N A G E: §f", "");
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                p.sendMessage("§cPlayer is no longer online.");
                p.closeInventory();
                return;
            }

            Material type = clickedItem.getType();
            if (type == Material.IRON_BARS) {
                p.performCommand("offend arrest " + targetName);
                p.closeInventory();
            } else if (type == Material.SLIME_BALL) {
                p.performCommand("offend release " + targetName);
                p.closeInventory();
            } else if (type == Material.REDSTONE_BLOCK) {
                p.performCommand("offend fakeban " + targetName);
                p.closeInventory();
            } else if (type == Material.BARRIER) {
                p.performCommand(plugin.isMenuBanned(target.getUniqueId()) ? "menuunban " + targetName : "menuban " + targetName);
                com.smpcore.menus.StaffMenu.openPlayerActions(p, target, plugin);
            } else if (type == Material.ICE) {
                p.performCommand("economy freeze " + targetName + " Staff Panel");
                p.closeInventory();
            } else if (type == Material.RED_STAINED_GLASS) {
                p.performCommand("economy ban " + targetName + " Staff Panel");
                p.closeInventory();
            } else if (type == Material.ARROW) {
                Sounds.playClick(p);
                com.smpcore.menus.StaffMenu.openPlayerList(p);
            }
            return;
        }

        
        if (holder instanceof KeysMenu) {
            handleKeysMenu(e, p, clickedItem);
            return;
        }
        else if (holder instanceof JewelerMenu) {
            handleJewelerMenu(e, p, e.getClickedInventory(), e.getSlot(), clickedItem, topInv);
            return;
        }
        else if (holder instanceof KeyTradeMenu) {
            handleKeyTradeMenu(e, p, clickedItem);
            return;
        }
        else if (holder instanceof com.smpcore.menus.CratesMainMenu) {
            handleCratesMainMenu(e, p, clickedItem);
            return;
        }
        else if (holder instanceof com.smpcore.menus.MissionsMenu) {
            handleMissionsMenu(e, p, clickedItem);
            return;
        }
        else if (holder instanceof RanksMenu) {
            e.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (e.getSlot() == 22 || e.getSlot() == 24) {
                if (!plugin.getRankManager().hasUsedTrial(p.getUniqueId())) {
                    RankManager.SocialRank targetRank = (e.getSlot() == 24) ? RankManager.SocialRank.VIP : RankManager.SocialRank.EXCLUSIVE;
                    plugin.getRankManager().startTrial(p, targetRank);
                    p.closeInventory();
                } else {
                    p.sendMessage("§cYou have already used your Free Trial.");
                    Sounds.playError(p);
                }
            }
            else if (e.getSlot() == 31 || e.getSlot() == 40 || e.getSlot() == 33 || e.getSlot() == 42) {
                p.closeInventory();
                p.sendMessage("§aTo purchase this subscription, please visit our store or open a ticket on Discord!");
                Sounds.playClick(p);
            }
            else if (e.getSlot() == 49) {
                p.closeInventory();
                p.sendMessage("§dTo apply for Staff, please join our Discord and open a ticket!");
                Sounds.playClick(p);
            }
            return;
        }

        if (title.startsWith("§8Roulette:")) {
            e.setCancelled(true);
            return;
        }

        if (holder instanceof MigelSMPMenu || isSystemMenu(title, holder)) {
            boolean isTopInv = e.getClickedInventory().equals(topInv);
            if (isTopInv || e.isShiftClick()) {
                e.setCancelled(true);
            } else {
                e.setCancelled(false);
                return;
            }
        }

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (isBackButton(clickedItem)) {
            Sounds.playClick(p);
            handleBackNavigation(p, holder, title);
            return;
        }




        if (title.startsWith("§8§lᴄᴏɴғɪʀᴍ: §a$")) {
            e.setCancelled(true);

            if (e.getSlot() == 15) {
                String priceStr = ChatColor.stripColor(title).replace("ᴄᴏɴғɪʀᴍ: $", "").replace(",", "").trim();
                try {
                    double price = Double.parseDouble(priceStr);

                    ItemStack displayItem = topInv.getItem(13);
                    if (displayItem == null) return;

                    ItemStack originalItem = displayItem.clone();
                    ItemMeta meta = originalItem.getItemMeta();
                    if (meta != null && meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        if (lore.size() >= 2) {
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                        }
                        meta.setLore(lore.isEmpty() ? null : lore);
                        originalItem.setItemMeta(meta);
                    }

                    ItemStack handItem = p.getInventory().getItemInMainHand();
                    if (handItem == null || handItem.getType() == Material.AIR || !handItem.isSimilar(originalItem) || handItem.getAmount() != originalItem.getAmount()) {
                        p.sendMessage("§cYou must hold the exact item in your main hand to confirm!");
                        Sounds.playError(p);
                        p.closeInventory();
                        return;
                    }

                    p.getInventory().setItemInMainHand(null);

                    plugin.getAuctionManager().addAuction(p.getUniqueId(), originalItem, price);
                    p.sendMessage("§aItem successfully listed on the Auction House for §e$" + String.format("%,.0f", price) + "§a!");
                    Sounds.playSuccess(p);
                    p.closeInventory();

                } catch (Exception ex) {
                    p.sendMessage("§cError processing the price.");
                    p.closeInventory();
                }
            }
            else if (e.getSlot() == 11) {
                p.sendMessage("§cListing cancelled.");
                Sounds.playError(p);
                p.closeInventory();
            }
            return;
        }
        

        if (holder == null) return;

        try {
            if (holder instanceof TPRandomMenu) handleRtpMenu(p, clickedItem);
            else if (holder instanceof TeleportMenu) handleTeleportClick(p, e.getSlot());
            else if (holder instanceof OrdersMenu) handleOrdersMenu(p, clickedItem);
            else if (holder instanceof MainMenu) handleMainMenu(p, clickedItem);
            else if (holder instanceof BankMenu) handleBankMenu(p, clickedItem, title);
            else if (holder instanceof PvPMenu) handlePvPMenu(p, clickedItem);
            else if (holder instanceof CasinoLegalMenu) handleCasinoLegal(p, clickedItem);
            else if (holder instanceof CasinoMenu) handleCasinoMenu(p, clickedItem);

            else if (holder instanceof RouletteSetupMenu) RouletteSetupMenu.handleClick(p, e.getSlot(), plugin.getCasinoManager());
            else if (holder instanceof MinesSetupMenu) MinesSetupMenu.handleClick(p, e.getSlot(), e.getClick(), plugin.getCasinoManager());
            else if (holder instanceof SlotsSetupMenu) SlotsSetupMenu.handleClick(p, e.getSlot(), ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()), plugin.getCasinoManager());
            else if (holder instanceof DragonSetupMenu) DragonSetupMenu.handleClick(p, e.getSlot(), plugin.getCasinoManager());
            else if (holder instanceof RouletteMenu) handleRouletteGame(p, clickedItem);
            else if (holder instanceof MinesMenu) MinesMenu.handleClick(p, e.getSlot(), plugin.getCasinoManager(), economyManager);
            else if (holder instanceof DragonGameMenu) DragonGameMenu.handleClick(p, e.getSlot(), plugin.getCasinoManager(), economyManager);

            else if (holder instanceof InsuranceMenu) handleInsuranceMenu(p, clickedItem);
            else if (holder instanceof AuctionMenu) handleAuctionMenu(p, clickedItem, title);
            else if (holder instanceof ConfirmAllMenu) ConfirmAllMenu.handleClick(p, clickedItem, economyManager, priceManager, langManager);
            else if (holder instanceof TeleportMenu) handleTeleportMenu(p, clickedItem);
            else if (holder instanceof InvestmentMenu) handleInvestmentMenu(p, clickedItem);
            else if (holder instanceof TradeMenu) handleTradeMenu(p, title, clickedItem);
            else if (holder instanceof ShopMenu) handleShopCategories(p, clickedItem);
            else if (isShopCategory(holder)) handleShopPurchase(p, clickedItem, title);
            else if (holder instanceof GearShop) handleGearShop(p, clickedItem, title);
            else if (holder instanceof SuperShopMenu) handleSuperShop(p, clickedItem);
            else if (holder instanceof EnchantmentMenu || holder instanceof EnchantPurchaseMenu) handleEnchantments(p, holder, clickedItem, title);
            else if (holder instanceof BountyMenu) handleBountyMenu(p, clickedItem, title);
            else if (holder instanceof HomesMenu) handleHomesMenu(p, e.getSlot(), clickedItem, e.isRightClick());
            else if (holder instanceof SetsMenu) handleSetsMenu(p, clickedItem);
            else if (holder instanceof KillMenu) handleKillMenu(p, e.getSlot());
            else if (holder instanceof SellMenu) handleSellMenu(p, e, topInv);

            if (plugin.getPendingConfirmations().containsKey(p.getUniqueId())) {
                ConfirmationMenu cm = plugin.getPendingConfirmations().get(p.getUniqueId());
                if (cm.handleClick(p, clickedItem)) {
                    plugin.getPendingConfirmations().remove(p.getUniqueId());
                }
            }

        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Error in MenuListener: ", ex);
            p.closeInventory();
            p.sendMessage("§cAn error occurred interacting with the menu.");
        }
    }

    private void handleRtpMenu(Player p, ItemStack clickedItem) {
        org.bukkit.World targetWorld = null;
        if (clickedItem.getType() == Material.GRASS_BLOCK) targetWorld = Bukkit.getWorld("world");
        else if (clickedItem.getType() == Material.NETHERRACK) targetWorld = Bukkit.getWorld("world_nether");
        else if (clickedItem.getType() == Material.END_STONE) targetWorld = Bukkit.getWorld("world_the_end");

        if (targetWorld != null) {
            p.closeInventory();
            plugin.getTprManager().teleport(p, targetWorld, 0.0);
        }
    }
    private void handleKillMenu(Player p, int slot) {
        if (slot == 15) {
            p.setHealth(0);
            p.closeInventory();
            p.sendMessage("§c§lYOU DIED §7(Suicide)");
        } else if (slot == 11) {
            p.closeInventory();
        }
    }

    private void handleSellMenu(Player p, InventoryClickEvent e, Inventory topInv) {
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(topInv)) {

            if (e.getSlot() >= 36) {
                e.setCancelled(true);

                if (e.getSlot() == 39) {
                    p.closeInventory();
                } else if (e.getSlot() == 41) {
                    sellManager.addIntentionalClose(p.getUniqueId());
                    sellManager.processSell(p, topInv);
                }
            }
        }
    }

    private void handleKeysMenu(InventoryClickEvent e, Player p, ItemStack clickedItem) {
        e.setCancelled(true);
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.TRIPWIRE_HOOK || clickedItem.getType() == Material.NETHER_STAR || clickedItem.getType() == Material.EMERALD) {
            String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            KeyType selectedType = null;

            for (KeyType k : KeyType.values()) {
                if (name.contains(ChatColor.stripColor(k.getDisplayName()))) {
                    selectedType = k;
                    break;
                }
            }

            if (selectedType != null) {
                if (plugin.getKeyManager().takeKey(p, selectedType, 1)) {
                    p.sendMessage("§aOpening " + selectedType.getDisplayName() + "§a...");
                    Sounds.playClick(p);
                    new com.smpcore.utils.RouletteTask(plugin, p, selectedType).runTaskTimer(plugin, 0L, 1L);
                } else {
                    p.sendMessage("§cYou don't have any keys of this type!");
                    Sounds.playError(p);
                    p.closeInventory();
                }
            }
        }
    }

    private void handleKeyTradeMenu(InventoryClickEvent e, Player p, ItemStack clickedItem) {
        e.setCancelled(true);
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        KeyType type = null;
        int price = 0;

        switch (e.getSlot()) {
            case 10: type = KeyType.NORMAL; price = 10; break;
            case 11: type = KeyType.RARE; price = 25; break;
            case 12: type = KeyType.LEGENDARY; price = 50; break;
            case 14: type = KeyType.XTREME; price = 100; break;
            case 16: type = KeyType.PRIME; price = 250; break;
        }

        if (type != null) {
            int playerHas = 0;
            for (ItemStack i : p.getInventory().getContents()) {
                if (com.smpcore.utils.ChocolateUtils.isChocolate(i)) playerHas += i.getAmount();
            }

            if (playerHas >= price) {
                int leftToRemove = price;
                for (ItemStack is : p.getInventory().getContents()) {
                    if (leftToRemove <= 0) break;
                    if (com.smpcore.utils.ChocolateUtils.isChocolate(is)) {
                        if (is.getAmount() <= leftToRemove) {
                            leftToRemove -= is.getAmount();
                            p.getInventory().removeItem(is);
                        } else {
                            is.setAmount(is.getAmount() - leftToRemove);
                            leftToRemove = 0;
                        }
                    }
                }
                p.updateInventory();
                plugin.getKeyManager().giveKey(p, type, 1);
                p.sendMessage("§aTrade successful!");
                Sounds.playSuccess(p);
            } else {
                p.sendMessage("§cNot enough Chocolate! Need " + price + ".");
                Sounds.playError(p);
            }
        }
    }

    private void handleCratesMainMenu(InventoryClickEvent e, Player p, ItemStack clickedItem) {
        e.setCancelled(true);
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (e.getSlot() == 11) {
            p.closeInventory();
            com.smpcore.menus.KeyTradeMenu.open(p, plugin);
            Sounds.playClick(p);
        } else if (e.getSlot() == 13) {
            p.closeInventory();
            com.smpcore.menus.KeysMenu.open(p, plugin);
            Sounds.playClick(p);
        } else if (e.getSlot() == 15) {
            p.closeInventory();
            com.smpcore.menus.MissionsMenu.open(p, plugin);
            Sounds.playClick(p);
        }
    }



    private void handleMissionsMenu(InventoryClickEvent e, Player p, ItemStack clickedItem) {
        e.setCancelled(true);
        if (clickedItem == null || clickedItem.getType() != Material.BOOK) return;

        List<com.smpcore.utils.Mission> missions = plugin.getMissionManager().getMissions();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        int missionIndex = -1;

        for (int i = 0; i < slots.length; i++) {
            if (e.getSlot() == slots[i]) {
                missionIndex = i;
                break;
            }
        }

        if (missionIndex >= 0 && missionIndex < missions.size()) {
            com.smpcore.utils.Mission selected = missions.get(missionIndex);
            plugin.getMissionManager().tryCompleteMission(p, selected);
        }
    }

    private void handleMainMenu(Player p, ItemStack clickedItem) {
        Sounds.playClick(p);
        Material type = clickedItem.getType();
        if (type == Material.GOLD_INGOT) BankMenu.open(p, langManager, economyManager, superMoneyManager);
        else if (type == Material.ENDER_PEARL) TeleportMenu.open(p, tprManager, langManager);
        else if (type == Material.EMERALD) ShopMenu.open(p, langManager, economyManager);
        else if (type == Material.GOLDEN_HORSE_ARMOR) AuctionMenu.open(p, auctionManager, AuctionSort.NEWEST, AuctionCategory.ALL, null);
        else if (type == Material.NETHERITE_SWORD) PvPMenu.open(p);
        else if (type == Material.ENDER_CHEST) plugin.getVaultManager().openVault(p);
        else if (type == Material.COMPARATOR) SettingsMenu.open(p, settingsManager);
        else if (type == Material.NETHER_STAR) SuperShopMenu.open(p);
    }

    private void handleBankMenu(Player p, ItemStack clickedItem, String name) {
        Sounds.playClick(p);
        if (clickedItem.getType() == Material.NOTE_BLOCK) {
            if (plugin.getCasinoManager().hasAcceptedTerms(p)) CasinoMenu.open(p);
            else CasinoLegalMenu.open(p);
        } else if (name != null && name.contains("sᴇʟʟ")) SellMenu.open(p, langManager);
        else if (name != null && name.contains("ᴄʀʏᴘᴛᴏ")) InvestmentMenu.open(p, cryptoManager);
        else if (clickedItem.getType() == Material.SHIELD) InsuranceMenu.open(p, plugin.getInsuranceManager());
    }

    private void handleCasinoLegal(Player p, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.LIME_CONCRETE) {
            plugin.getCasinoManager().acceptTerms(p);
            Sounds.playSuccess(p);
            CasinoMenu.open(p);
        } else if (clickedItem.getType() == Material.RED_CONCRETE) {
            p.closeInventory();
            Sounds.playError(p);
        }
    }

    private void handleCasinoMenu(Player p, ItemStack clickedItem) {
        Material t = clickedItem.getType();
        if (t == Material.MAGMA_CREAM) RouletteSetupMenu.open(p, plugin.getCasinoManager());
        else if (t == Material.TNT) MinesSetupMenu.open(p, plugin.getCasinoManager());
        else if (t == Material.ENCHANTED_GOLDEN_APPLE) SlotsSetupMenu.open(p, plugin.getCasinoManager());
        else if (t == Material.DRAGON_HEAD) DragonSetupMenu.open(p, plugin.getCasinoManager());
        else if (t == Material.PAPER) plugin.getLotteryManager().buyTicket(p);
        else if (t == Material.SHIELD) InsuranceMenu.open(p, plugin.getInsuranceManager());
    }

    private void handleJewelerMenu(InventoryClickEvent e, Player p, Inventory clickedInv, int slot, ItemStack clickedItem, Inventory topInv) {
        if (!clickedInv.equals(topInv)) {
            if (e.isShiftClick()) {
                if (clickedItem != null && com.smpcore.menus.JewelerMenu.isValidOre(clickedItem.getType())) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> updateJewelerButton(topInv), 1L);
                } else {
                    e.setCancelled(true);
                }
            }
            return;
        }

        if (slot == 20) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updateJewelerButton(topInv), 1L);
            return;
        }
        if (slot == 24) return;

        if (slot == 22) {
            e.setCancelled(true);
            ItemStack input = topInv.getItem(20);

            if (input == null || !com.smpcore.menus.JewelerMenu.isValidOre(input.getType())) {
                com.smpcore.utils.Sounds.playError(p);
                return;
            }

            if (topInv.getItem(24) != null) {
                p.sendMessage("§cPlease remove the previous item first!");
                com.smpcore.utils.Sounds.playError(p);
                return;
            }

            double costPerItem = com.smpcore.menus.JewelerMenu.getEvaluationCost(input.getType());
            int amount = input.getAmount();
            double totalCost = costPerItem * amount;

            if (!economyManager.has(p, totalCost)) {
                p.sendMessage("§cInsufficient funds! You need §e$" + String.format("%,.0f", totalCost));
                com.smpcore.utils.Sounds.playError(p);
                return;
            }

            economyManager.withdraw(p, totalCost);
            com.smpcore.menus.JewelerMenu menuLogic = new com.smpcore.menus.JewelerMenu();
            double purity = menuLogic.calculatePurity();

            ItemStack evaluated = input.clone();
            evaluated = com.smpcore.utils.PurityUtils.setPurity(evaluated, purity);

            topInv.setItem(20, null);
            topInv.setItem(24, evaluated);

            p.sendMessage("§aEvaluation Complete! §7Purity: §e" + String.format("%.1f", purity) + "%");
            com.smpcore.utils.Sounds.playAnvil(p);
            updateJewelerButton(topInv);
            return;
        }
        e.setCancelled(true);
    }

    private void updateJewelerButton(Inventory inv) {
        if (inv == null || inv.getViewers().isEmpty()) return;
        ItemStack input = inv.getItem(20);

        if (input != null && com.smpcore.menus.JewelerMenu.isValidOre(input.getType())) {
            double cost = com.smpcore.menus.JewelerMenu.getEvaluationCost(input.getType()) * input.getAmount();
            ItemStack anvil = new ItemStack(Material.ANVIL);
            org.bukkit.inventory.meta.ItemMeta meta = anvil.getItemMeta();
            meta.setDisplayName("§a§lEVALUATE ORE");
            List<String> lore = new java.util.ArrayList<>();
            lore.add("§7Click to appraise this item.");
            lore.add(" ");
            lore.add("§7Cost: §c$" + String.format("%,.0f", cost));
            lore.add(" ");
            lore.add("§eClick to Confirm!");
            meta.setLore(lore);
            anvil.setItemMeta(meta);
            inv.setItem(22, anvil);
        } else {
            ItemStack barrier = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            org.bukkit.inventory.meta.ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName("§c§lWAITING FOR ORE...");
            List<String> lore = new java.util.ArrayList<>();
            lore.add("§7Place a valid ore in the");
            lore.add("§7left slot to begin.");
            meta.setLore(lore);
            barrier.setItemMeta(meta);
            inv.setItem(22, barrier);
        }
    }

    private void handleRouletteGame(Player p, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        if (name.contains("RED")) RouletteMenu.startSpin(p, "RED", plugin.getCasinoManager(), economyManager);
        else if (name.contains("GREEN")) RouletteMenu.startSpin(p, "GREEN", plugin.getCasinoManager(), economyManager);
        else if (name.contains("BLACK")) RouletteMenu.startSpin(p, "BLACK", plugin.getCasinoManager(), economyManager);
    }

    private boolean isSystemMenu(String title, InventoryHolder holder) {
        return (holder instanceof MigelSMPMenu) || title.contains("ᴄᴏɴғɪʀᴍ") || title.contains("Confirm");
    }

    private boolean isBackButton(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return (item.getType() == Material.BARRIER || item.getType() == Material.ARROW) &&
                (name.contains("BACK") || name.contains("ʙᴀᴄᴋ") || name.contains("Voltar") || name.contains("Previous"));
    }

    private void handleBackNavigation(Player p, InventoryHolder holder, String title) {
        if (title.equals("§8sᴇᴛᴛɪɴɢs")) { p.closeInventory(); return; }
        if (holder instanceof OverworldShop || holder instanceof NetherShop || holder instanceof EndShop || holder instanceof RedstoneShop || holder instanceof UtilidadesShop || holder instanceof FoodShop || holder instanceof GearShop) {
            ShopMenu.open(p, langManager, economyManager); return;
        }
        if (holder instanceof EnchantmentMenu || holder instanceof EnchantPurchaseMenu || holder instanceof SetsMenu) {
            GearShop.open(p, priceManager, langManager); return;
        }
        if (holder instanceof RouletteMenu || holder instanceof MinesMenu || holder instanceof SlotGameMenu || holder instanceof DragonGameMenu || holder instanceof RouletteSetupMenu || holder instanceof MinesSetupMenu || holder instanceof SlotsSetupMenu || holder instanceof DragonSetupMenu) {
            CasinoMenu.open(p); return;
        }
        if (holder instanceof TradeMenu) { InvestmentMenu.open(p, cryptoManager); return; }
        p.closeInventory();
    }

    private String getSettingKey(int slot) {
        switch (slot) {
            case 0: return "public_chat";
            case 1: return "private_messages";
            case 2: return "chat_server_msgs";
            case 3: return "auction_alerts";
            case 9: return "totem_particles";
            case 10: return "explosion_particles";
            case 11: return "disable_mob_spawns";
            case 18: return "player_visibility";
            case 19: return "scoreboard";
            case 20: return "tpa_confirm_menu";
            case 21: return "sound_notifications";
            case 22: return "order_notification";
            case 27: return "tpa_requests";
            case 28: return "payments";
            case 29: return "quiet_spawn";
            default: return null;
        }
    }

    private boolean isShopCategory(InventoryHolder holder) {
        return holder instanceof OverworldShop || holder instanceof NetherShop || holder instanceof EndShop ||
                holder instanceof RedstoneShop || holder instanceof UtilidadesShop || holder instanceof FoodShop;
    }

    private void handlePvPMenu(Player p, ItemStack clickedItem) {
        Sounds.playClick(p);
        if (clickedItem.getType() == Material.NETHERITE_SWORD) { p.closeInventory(); plugin.getDuelManager().joinQueue(p); }
        else if (clickedItem.getType() == Material.END_CRYSTAL) { p.closeInventory(); plugin.getDuelManager().startTraining(p); }
        else if (clickedItem.getType() == Material.SKELETON_SKULL) BountyMenu.open(p, bountyManager, 1, langManager);
    }

    private void handleInsuranceMenu(Player p, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.SKELETON_SKULL || clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
            plugin.getInsuranceManager().buyDeathInsurance(p);
            InsuranceMenu.open(p, plugin.getInsuranceManager());
        } else if (clickedItem.getType() == Material.IRON_BARS || clickedItem.getType() == Material.GOLD_BLOCK) {
            plugin.getInsuranceManager().buyBankruptcyInsurance(p);
            InsuranceMenu.open(p, plugin.getInsuranceManager());
        }
    }

    private void handleKillMenu(Player p, int slot, ItemStack clickedItem) {
        if (slot == 13 || clickedItem.getType() == Material.LIME_WOOL) {
            p.setHealth(0);
            p.closeInventory();
            p.sendMessage("§c§lYOU DIED §7(Suicide)");
        } else if (clickedItem.getType() == Material.RED_WOOL) {
            p.closeInventory();
        }
    }

    private void handleOrdersMenu(Player p, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasLore()) return;
        List<String> lore = clickedItem.getItemMeta().getLore();
        if (lore == null || lore.isEmpty()) return;
        String idLine = lore.get(lore.size() - 1);

        if (idLine.startsWith("§0id:")) {
            try {
                UUID id = UUID.fromString(idLine.replace("§0id:", ""));
                OrderManager.BuyOrder order = this.orderManager.getOrderById(id);
                if (order != null) {
                    if (order.getBuyer().equals(p.getUniqueId())) {
                        p.sendMessage("§cYou cannot fill your own order!");
                        Sounds.playError(p);
                        return;
                    }
                    int playerHas = 0;
                    for (ItemStack i : p.getInventory().getStorageContents()) {
                        if (i != null && i.getType() == order.getMaterial()) playerHas += i.getAmount();
                    }
                    if (playerHas > 0) {
                        int remainingNeeded = order.getAmount() - order.filledAmount;
                        int toSell = Math.min(playerHas, remainingNeeded);
                        double pricePerUnit = order.getTotalPrice() / order.getAmount();
                        double payout = toSell * pricePerUnit;
                        p.getInventory().removeItem(new ItemStack(order.getMaterial(), toSell));
                        this.economyManager.deposit(p, payout);
                        this.orderManager.fillOrder(p, order, toSell, economyManager);
                        p.sendMessage("§a§lSUCCESS! §7Sold §f" + toSell + "x " + order.getMaterial().name() + " §7for §a$" + String.format("%,.2f", payout));
                        Sounds.playSuccess(p);
                        OrdersMenu.open(p, this.orderManager);
                    } else {
                        p.sendMessage("§cYou don't have any §f" + order.getMaterial().name() + " §cto sell!");
                        Sounds.playError(p);
                    }
                }
            } catch (Exception ex) {}
        }
    }

    private void handleAuctionMenu(Player p, ItemStack clicked, String title) {
        if (clicked.getType() == Material.SUNFLOWER) {
            Sounds.playClick(p);
            AuctionMenu.open(p, auctionManager, getSortFromTitle(title), getCategoryFromTitle(title), getQueryFromTitle(title));
        } else if (clicked.getType() == Material.COMPARATOR) {
            Sounds.playClick(p);
            AuctionMenu.open(p, auctionManager, getSortFromTitle(title), getCategoryFromTitle(title).next(), getQueryFromTitle(title));
        } else if (clicked.getType() == Material.HOPPER) {
            Sounds.playClick(p);
            AuctionMenu.open(p, auctionManager, getSortFromTitle(title).next(), getCategoryFromTitle(title), getQueryFromTitle(title));
        } else if (clicked.hasItemMeta() && clicked.getItemMeta().hasLore()) {
            List<String> lore = clicked.getItemMeta().getLore();
            String idLine = lore.get(lore.size() - 1);
            if (idLine.startsWith("§0id:")) {
                try {
                    UUID id = UUID.fromString(idLine.replace("§0id:", ""));
                    AuctionItem auction = auctionManager.getAuctionByUUID(id);
                    if (auction != null) processAuctionClick(p, auction, title);
                } catch (Exception ignored) {}
            }
        }
    }

    private void processAuctionClick(Player p, AuctionItem auction, String title) {
        if (auction.getSeller().equals(p.getUniqueId())) {
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(auction.getItem());
                auctionManager.removeAuction(auction);
                p.sendMessage("§eItem returned.");
                AuctionMenu.open(p, auctionManager, getSortFromTitle(title), getCategoryFromTitle(title), getQueryFromTitle(title));
            } else p.sendMessage("§cInventory full!");
        } else {
            if (economyManager.has(p, auction.getPrice())) {
                if (p.getInventory().firstEmpty() != -1) {
                    economyManager.withdraw(p, auction.getPrice());
                    economyManager.deposit(Bukkit.getOfflinePlayer(auction.getSeller()), auction.getPrice());
                    p.getInventory().addItem(auction.getItem());
                    auctionManager.removeAuction(auction);
                    p.sendMessage("§aPurchased!");
                    AuctionMenu.open(p, auctionManager, getSortFromTitle(title), getCategoryFromTitle(title), getQueryFromTitle(title));
                } else p.sendMessage("§cInventory full!");
            } else p.sendMessage("§cInsufficient funds!");
        }
    }

    private void handleTeleportMenu(Player p, ItemStack clickedItem) {
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        Sounds.playClick(p);
        if (name.contains("ʜᴏᴍᴇs")) HomesMenu.open(p, homeManager);
        else if (name.contains("ʀᴀɴᴅᴏᴍ ᴛᴘ")) TPRandomMenu.open(p);
        else if (name.contains("ᴛᴘ ʀᴇǫᴜᴇsᴛs")) {
            tprManager.toggleTpr(p);
            TeleportMenu.open(p, tprManager, langManager);
        }
    }

    private void handleInvestmentMenu(Player p, ItemStack clicked) {
        if (clicked.getType() == Material.SUNFLOWER) { InvestmentMenu.open(p, cryptoManager); return; }
        String clickedName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        for (String coinKey : cryptoManager.getCoinNames()) {
            if (ChatColor.stripColor(cryptoManager.getCoinDisplayName(coinKey)).equalsIgnoreCase(clickedName)) {
                TradeMenu.open(p, cryptoManager, coinKey); return;
            }
        }
    }

    private void handleTradeMenu(Player p, String title, ItemStack clicked) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        if (name.contains("ʙᴜʏ") || name.contains("sᴇʟʟ")) {
            String coinNameFromTitle = ChatColor.stripColor(title).replace("ᴛʀᴀᴅɪɴɢ: ", "").trim();
            for (String key : cryptoManager.getCoinNames()) {
                if (ChatColor.stripColor(cryptoManager.getCoinDisplayName(key)).equalsIgnoreCase(coinNameFromTitle)) {
                    boolean isBuy = name.contains("ʙᴜʏ");
                    int amount = name.contains(" 1") ? 1 : (name.contains(" 10") ? 10 : 64);
                    if (isBuy) cryptoManager.buyCoins(p, key, amount, economyManager);
                    else cryptoManager.sellCoins(p, key, amount, economyManager);
                    TradeMenu.open(p, cryptoManager, key);
                    break;
                }
            }
        }
    }

    private void handleShopCategories(Player p, ItemStack clicked) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Sounds.playClick(p);
        if (name.contains("ᴏᴠᴇʀᴡᴏʀʟᴅ")) OverworldShop.open(p, priceManager, langManager);
        else if (name.contains("ɴᴇᴛʜᴇʀ")) NetherShop.open(p, priceManager, langManager);
        else if (name.contains("ᴇɴᴅ")) EndShop.open(p, priceManager, langManager);
        else if (name.contains("ɢᴇᴀʀ")) GearShop.open(p, priceManager, langManager);
        else if (name.contains("ʀᴇᴅsᴛᴏɴᴇ")) RedstoneShop.open(p, priceManager, langManager);
        else if (name.contains("ᴜᴛɪʟs")) UtilidadesShop.open(p, priceManager, langManager);
        else if (name.contains("ғᴏᴏᴅ")) FoodShop.open(p, priceManager, langManager);
    }

    private void handleShopPurchase(Player p, ItemStack clicked, String title) {
        Sounds.playClick(p);
        Material materialToBuy = clicked.getType();
        if (priceManager.hasPrice(materialToBuy)) {
            ConfirmationMenu cm = new ConfirmationMenu(priceManager, economyManager, langManager, materialToBuy, title);
            plugin.getPendingConfirmations().put(p.getUniqueId(), cm);
            cm.open(p);
        }
    }

    private void handleGearShop(Player p, ItemStack clicked, String title) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Sounds.playClick(p);
        if (name.contains("ᴀʀᴍᴏʀ sᴇᴛs")) SetsMenu.open(p, priceManager);
        else if (name.contains("ᴇɴᴄʜᴀɴᴛᴍᴇɴᴛs")) EnchantmentMenu.open(p, 1, enchantmentPriceManager);
        else handleShopPurchase(p, clicked, title);
    }

    private void handleTeleportClick(Player p, int slot) {
        switch (slot) {
            case 11: p.performCommand("spawn"); p.closeInventory(); break;
            case 13: p.performCommand("tpr"); p.closeInventory(); break;
            case 15: p.performCommand("home"); p.closeInventory(); break;
        }
    }

    private void handleSuperShop(Player p, ItemStack clicked) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        if (name.contains("DRILL") || name.contains("ᴅʀɪʟʟ")) buySuperTool(p, 50.0, customItemManager.getDrill());
        else if (name.contains("FASTY") || name.contains("ғᴀsᴛʏ")) buySuperTool(p, 100.0, customItemManager.getFasty());
        else if (name.contains("CLAIM") || name.contains("ᴄʟᴀɪᴍ")) buySuperTool(p, 1000.0, customItemManager.getClaimShovel());
    }

    private void handleEnchantments(Player p, InventoryHolder holder, ItemStack clicked, String title) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Sounds.playClick(p);
        if (holder instanceof EnchantmentMenu) {
            if (clicked.getType() == Material.ARROW) {
                try {
                    String stripped = ChatColor.stripColor(title);
                    String pagePart = stripped.split("Pág ")[1].split("/")[0].trim();
                    int currentPage = Integer.parseInt(pagePart);
                    if (name.contains("NEXT") || name.contains("ɴᴇxᴛ")) EnchantmentMenu.open(p, currentPage + 1, enchantmentPriceManager);
                    else if (name.contains("PREVIOUS") || name.contains("ᴘʀᴇᴠɪᴏᴜs")) EnchantmentMenu.open(p, currentPage - 1, enchantmentPriceManager);
                } catch (Exception ignored) {}
                return;
            }
            if (clicked.getType() == Material.ENCHANTED_BOOK && clicked.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                if (!meta.getStoredEnchants().isEmpty()) {
                    Enchantment enchant = meta.getStoredEnchants().keySet().iterator().next();
                    EnchantPurchaseMenu.open(p, enchant, enchantmentPriceManager);
                }
            }
        } else if (holder instanceof EnchantPurchaseMenu) {
            String stripped = ChatColor.stripColor(title);
            String enchantName = stripped.split(" - ")[1];
            Enchantment enchant = Enchantment.getByName(enchantName.replace("minecraft:", "").toUpperCase());
            if (enchant == null) enchant = Enchantment.getByName(enchantName);
            if (enchant != null && name.startsWith("LEVEL ")) {
                String roman = name.split(":")[0].replace("LEVEL ", "").trim();
                enchantmentPriceManager.buyEnchantment(p, enchant, romanToInt(roman), economyManager, langManager);
                p.closeInventory();
            }
        }
    }

    private void handleBountyMenu(Player p, ItemStack clicked, String title) {
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Sounds.playClick(p);
        if (clicked.getType() == Material.ARROW) {
            try {
                String stripped = ChatColor.stripColor(title);
                String pagePart = stripped.split("Page ")[1].split("/")[0].trim();
                int currentPage = Integer.parseInt(pagePart);
                if (name.contains("NEXT") || name.contains("ɴᴇxᴛ")) BountyMenu.open(p, bountyManager, currentPage + 1, langManager);
                else if (name.contains("PREVIOUS") || name.contains("ᴘʀᴇᴠɪᴏᴜs")) BountyMenu.open(p, bountyManager, currentPage - 1, langManager);
            } catch (Exception ignored) {}
        }
    }

    private void handleHomesMenu(Player p, int slot, ItemStack clicked, boolean isRightClick) {
        int homeNumber = (slot >= 11 && slot <= 15) ? slot - 10 : -1;
        if (homeNumber != -1) {
            if (clicked.getType() == Material.LIME_BED) {
                if (isRightClick) {
                    homeManager.deleteHome(p, homeNumber);
                    Sounds.playAnvil(p);
                    HomesMenu.open(p, homeManager);
                } else {
                    p.closeInventory();
                    plugin.getTeleportManager().teleportWithWarmup(p, homeManager.getHome(p, homeNumber));
                }
            } else if (clicked.getType() == Material.LIGHT_GRAY_BED) {
                if (homeManager.setHome(p, homeNumber, p.getLocation())) {
                    Sounds.playSuccess(p);
                    HomesMenu.open(p, homeManager);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                    p.closeInventory();
                }
            }
        }
    }

    private void handleSetsMenu(Player p, ItemStack clicked) {
        Sounds.playClick(p);
        Material t = clicked.getType();
        if (t == Material.IRON_INGOT || t == Material.DIAMOND || t == Material.NETHERITE_INGOT) ConfirmAllMenu.open(p, t, economyManager, priceManager, langManager);
        else {
            ConfirmationMenu cm = new ConfirmationMenu(priceManager, economyManager, langManager, t, "Sets");
            plugin.getPendingConfirmations().put(p.getUniqueId(), cm);
            cm.open(p);
        }
    }

    private void buySuperTool(Player p, double cost, ItemStack item) {
        if (superMoneyManager.has(p, cost)) {
            if (p.getInventory().firstEmpty() != -1) {
                superMoneyManager.removeBalance(p, cost);
                p.getInventory().addItem(item);
                p.sendMessage("§aItem Purchased!");
                Sounds.playSuccess(p);
            } else { p.sendMessage("§cInventory Full!"); Sounds.playError(p); }
        } else { p.sendMessage("§cNot enough Super Money!"); Sounds.playError(p); }
    }

    private int romanToInt(String s) {
        if (s.equals("I")) return 1; if (s.equals("II")) return 2; if (s.equals("III")) return 3;
        if (s.equals("IV")) return 4; if (s.equals("V")) return 5;
        return 0;
    }

    private AuctionSort getSortFromTitle(String t) { try { String s=ChatColor.stripColor(t); return AuctionSort.valueOf(s.substring(s.indexOf("(")+1, s.indexOf(")"))); } catch(Exception e){ return AuctionSort.NEWEST; } }
    private AuctionCategory getCategoryFromTitle(String t) { try { String s=ChatColor.stripColor(t); return AuctionCategory.valueOf(s.substring(s.indexOf("[")+1, s.indexOf("]"))); } catch(Exception e){ return AuctionCategory.ALL; } }
    private String getQueryFromTitle(String t) { String s=ChatColor.stripColor(t); return s.contains(": ") ? s.substring(s.indexOf(": ")+2).toLowerCase() : null; }
}