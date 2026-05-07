package com.smpcore;

import com.smpcore.commands.*;
import com.smpcore.listeners.*;
import com.smpcore.menus.ConfirmationMenu;
import com.smpcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SMPCore extends JavaPlugin {

    private LangManager langManager;
    private PriceManager priceManager;
    private EconomyManager economyManager;
    private EconomyControlManager economyControlManager; // <-- RESTAURADO AQUI
    private ScoreboardHandler scoreboardHandler;
    private HomeManager homeManager;
    private SellManager sellManager;
    private QueueManager queueManager;
    private NPCManager npcManager;
    private CryptoManager cryptoManager;
    private TprManager tprManager;
    private BountyManager bountyManager;
    private com.smpcore.minigames.MinigameManager minigameManager;
    private EnchantmentPriceManager enchantmentPriceManager;
    private SuperMoneyManager superMoneyManager;
    private CustomItemManager customItemManager;
    private AuctionManager auctionManager;
    private OrderManager orderManager;
    private RegionManager regionManager;
    private CasinoManager casinoManager;
    private DuelManager duelManager;
    private SpawnManager spawnManager;
    private LotteryManager lotteryManager;
    private VaultManager vaultManager;
    private KeyManager keyManager;
    private InsuranceManager insuranceManager;
    private SpawnGenManager spawnGenManager;
    private InventoryManager inventoryManager;
    private SettingsManager settingsManager;
    private SetupManager setupManager;
    private TpaManager tpaManager;
    private TeleportManager teleportManager;
    private MissionManager missionManager;
    private RankManager rankManager;
    private CombatManager combatManager;

    private final HashSet<UUID> menuBannedPlayers = new HashSet<>();
    private final Map<UUID, ConfirmationMenu> pendingConfirmations = new HashMap<>();

    @Override
    public void onEnable() {
        printLogo();
        Logger logger = this.getLogger();

        logger.info(" Initializing Rank Manager...");
        this.rankManager = new RankManager(this);

        logger.info("[SMPCore] Initializing Combat Manager...");
        this.combatManager = new CombatManager(this);
        getServer().getPluginManager().registerEvents(new com.smpcore.listeners.CombatListener(this), this);

        logger.info(ChatColor.YELLOW + " >> Starting plugin initialization...");

        logger.info(" Loading configuration files...");
        saveDefaultConfig();

        logger.info("Initializing Queue Manager...");
        this.queueManager = new QueueManager(this);
        getServer().getPluginManager().registerEvents(new com.smpcore.listeners.QueueListener(this), this);

        logger.info(" Initializing PurityUtils...");
        PurityUtils.init(this);

        logger.info(" Initializing Core Managers (Settings, Mission, Teleport)...");
        this.settingsManager = new SettingsManager(this);
        this.missionManager = new MissionManager(this);
        this.teleportManager = new TeleportManager(this);
        this.tpaManager = new TpaManager(this);

        this.langManager = new LangManager(this);

        logger.info(" Initializing Economy & Price Managers...");
        this.economyManager = new EconomyManager(this);
        this.economyControlManager = new EconomyControlManager(this); // <-- RESTAURADO AQUI
        this.priceManager = new PriceManager(this.getConfig(), this.getLogger());
        this.enchantmentPriceManager = new EnchantmentPriceManager(this.getConfig(), this.getLogger());
        this.keyManager = new KeyManager(this);
        this.vaultManager = new VaultManager(this);
        this.inventoryManager = new InventoryManager(this);

        logger.info(" Initializing Advanced Systems (Auctions, Casino, Crypto)...");
        this.homeManager = new HomeManager(this);
        this.sellManager = new SellManager(this.priceManager, this.economyManager);
        this.cryptoManager = new CryptoManager(this, this.langManager);
        this.bountyManager = new BountyManager(this, this.economyManager);
        this.superMoneyManager = new SuperMoneyManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.auctionManager = new AuctionManager(this);
        this.orderManager = new OrderManager(this);
        this.regionManager = new RegionManager(this);
        this.casinoManager = new CasinoManager(this, this.economyManager);
        this.duelManager = new DuelManager(this);
        this.spawnManager = new SpawnManager(this);
        this.lotteryManager = new LotteryManager(this, this.economyManager);
        this.insuranceManager = new InsuranceManager(this, this.economyManager);
        this.tprManager = new TprManager(this);


        this.setupManager = new SetupManager(this);
        getServer().getPluginManager().registerEvents(this.setupManager, this);

        logger.info(ChatColor.GREEN + " Registering Event Listeners...");
        getServer().getPluginManager().registerEvents(new SettingsListener(this), this);
        getServer().getPluginManager().registerEvents(new ChocolateListener(this), this);
        getServer().getPluginManager().registerEvents(new GlitchProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new VaultListener(this, vaultManager, lotteryManager), this);
        getServer().getPluginManager().registerEvents(new CommandBlocker(), this);

        logger.info(" Registering SpawnListener...");
        SpawnListener spawnListener = new SpawnListener(this);
        getServer().getPluginManager().registerEvents(spawnListener, this);

        if (regionManager != null) {
            logger.info(" Registering RegionListener...");
            getServer().getPluginManager().registerEvents(new RegionListener(this.regionManager), this);
        }
        if (insuranceManager != null) {
            logger.info(" Registering InsuranceListener...");
            getServer().getPluginManager().registerEvents(new InsuranceListener(this.insuranceManager), this);
        }

        logger.info(" Registering MenuListener...");
        MenuListener menuListener = new MenuListener(
                this, priceManager, economyManager, homeManager, sellManager,
                cryptoManager, tprManager, bountyManager, settingsManager,
                enchantmentPriceManager, langManager, superMoneyManager,
                customItemManager, auctionManager, orderManager
        );
        getServer().getPluginManager().registerEvents(menuListener, this);

        logger.info(" Initializing Scoreboard Handler...");
        this.scoreboardHandler = new ScoreboardHandler(this, this.economyManager, this.cryptoManager, this.tprManager, this.settingsManager, this.langManager, this.superMoneyManager);
        getServer().getPluginManager().registerEvents(this.scoreboardHandler, this);

        logger.info(ChatColor.YELLOW + " >> Checking Spawn World installation...");
        MapInstaller.installMaps(this);

        logger.info(" Loading SpawnGenManager...");
        this.spawnGenManager = new SpawnGenManager(this);

        logger.info(" Initializing NPC Manager...");
        this.npcManager = new NPCManager(this);
        getServer().getPluginManager().registerEvents(this.npcManager, this);

        logger.info(" Creating NPCs at Spawn...");
        setupNPCs();

        logger.info(ChatColor.GREEN + " Registering Commands...");
        registerAllCommands(spawnListener);

        logger.info(" Loading banned players list...");
        loadBannedPlayers();

        if (!getConfig().contains("server-name")) {
            logger.info(ChatColor.LIGHT_PURPLE + " ! SETUP INCOMPLETE ! Waiting for Admin to join...");
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAdminJoin(PlayerJoinEvent event) {
                    Player p = event.getPlayer();
                    if (p.isOp()) {
                        setupManager.startSetup(p);
                        PlayerJoinEvent.getHandlerList().unregister(this);
                    }
                }
            }, this);
        }
        logger.info(ChatColor.GOLD + " Processing MINIGAMES...");
        logger.info(ChatColor.GOLD + " Starting MinigameManager...");
        this.minigameManager = new com.smpcore.minigames.MinigameManager(this);

        logger.info(ChatColor.GOLD + " >> Plugin successfully ENABLED! All systems go.");
        logger.info(ChatColor.GOLD + "====================================================");
    }

    @Override
    public void onDisable() {
        getLogger().info(" >> Saving data...");
        if (cryptoManager != null) {
            cryptoManager.saveData();
            getLogger().info(" Crypto data saved.");
        }
        if (economyManager != null) {
            economyManager.saveBalances();
            getLogger().info(" Economy balances saved.");
        }
        if (auctionManager != null) {
            auctionManager.saveAuctions();
            getLogger().info(" Auctions saved.");
        }

        getLogger().info(ChatColor.RED + " Plugin DISABLED. Goodbye!");
        printLogo();
    }

    private void setupNPCs() {
        getServer().getScheduler().runTaskLater(this, () -> {
            org.bukkit.World spawn = spawnGenManager.getSpawnWorld();
            org.bukkit.World lobby = spawnGenManager.getLobbyWorld();

            if (spawn != null) {
                getLogger().info(" World loaded. Creating Spawn NPCs...");

                npcManager.createNPC(
                        new Location(spawn, 877.5, 168, 740.5, -90, 0),
                        "§9§lCASINO",
                        SkinData.CASINO_NPC.getValue(),
                        SkinData.CASINO_NPC.getSignature(),
                        "CASINO"
                );

                npcManager.createNPC(
                        new Location(spawn, 876.5, 168, 736.5, -90, 0),
                        "§a§lRTP",
                        SkinData.RTP_NPC.getValue(),
                        SkinData.RTP_NPC.getSignature(),
                        "RTP"
                );

                npcManager.createNPC(
                        new Location(spawn, 878.5, 168, 744.5, -90, 0),
                        "§6§lJEWELER",
                        SkinData.JEWELER_NPC.getValue(),
                        SkinData.JEWELER_NPC.getSignature(),
                        "JEWELER"
                );

                npcManager.createNPC(
                        new Location(spawn, 877.5, 168, 732.5, -90, 0),
                        "§6§lAUCTION",
                        SkinData.AUCTION_NPC.getValue(),
                        SkinData.AUCTION_NPC.getSignature(),
                        "AUCTION"
                );

                npcManager.createNPC(
                        new Location(spawn, 878.5, 168, 728.5, -90, 0),
                        "§6§lGINGERBREAD",
                        SkinData.GINGERBREAD_NPC.getValue(),
                        SkinData.GINGERBREAD_NPC.getSignature(),
                        "GINGERBREAD"
                );
            } else {
                getLogger().warning(" Spawn World STILL not found! Main NPCs skipped.");
            }

            if (lobby != null) {
                getLogger().info(" Spawning Arcade NPCs in Lobby...");

                npcManager.createNPC(
                        new Location(lobby, 12, 2, 14, 180, 0),
                        "§b§lICE CONTROL",
                        SkinData.CASINO_NPC.getValue(),
                        SkinData.CASINO_NPC.getSignature(),
                        "ARCADE_ICE_CONTROL"
                );

                npcManager.createNPC(
                        new Location(lobby, 18.5, 2, 14.5, 180, 0),
                        "§e§lBLOCK SHUFFLE",
                        SkinData.RTP_NPC.getValue(),
                        SkinData.RTP_NPC.getSignature(),
                        "ARCADE_BLOCK_SHUFFLE"
                );

                npcManager.createNPC(
                        new Location(lobby, 24.5, 2, 14.5, 180, 0),
                        "§7§lMINE RUSH",
                        SkinData.JEWELER_NPC.getValue(),
                        SkinData.JEWELER_NPC.getSignature(),
                        "ARCADE_MINE_RUSH"
                );

                npcManager.createNPC(
                        new Location(lobby, 30.5, 2, 14.5, 180, 0),
                        "§a§lBUILD MEMORY",
                        SkinData.AUCTION_NPC.getValue(),
                        SkinData.AUCTION_NPC.getSignature(),
                        "ARCADE_BUILD_MEMORY"
                );

                getLogger().info(" Arcade NPCs created successfully!");
            } else {
                getLogger().warning(" Lobby World not found! Arcade NPCs skipped.");
            }
        }, 40L);
    }

    private void registerAllCommands(SpawnListener spawnListener) {
        SMPTabCompleter tabCompleter = new SMPTabCompleter();

        safeRegisterTab("setup", new SetupCommand(this), null);
        safeRegisterTab("order", new OrderCommand(this), null);
        safeRegisterTab("orders", new OrderCommand(this), null);
        safeRegisterTab("eco", new AdminCommand(economyManager, langManager), new EcoTabCompleter());
        safeRegisterTab("pay", new PayCommand(this), null);
        safeRegisterTab("setp", new SetpCommand(this, spawnListener), null);
        safeRegisterTab("ranks", new RanksCommand(this), null);
        safeRegisterTab("staff", new StaffCommand(this), null);

        TpaCommands tpaCmd = new TpaCommands(this);
        safeRegisterTab("tpa", tpaCmd, null);
        safeRegisterTab("tpahere", tpaCmd, null);
        safeRegisterTab("tpaccept", tpaCmd, null);
        safeRegisterTab("tpdeny", tpaCmd, null);

        KeysCommand keysCmd = new KeysCommand(this);
        safeRegisterTab("keys", keysCmd, keysCmd);

        safeRegisterTab("kill", new KillCommand(), null);
        safeRegisterTab("warp", new com.smpcore.commands.WarpCommand(this), null);
        safeRegisterTab("setspawn", new com.smpcore.commands.SetSpawnCommand(this), null);
        safeRegisterTab("smpstatus", new ServerStatusCommand(this), null);
        safeRegisterTab("menuban", new MenuBanCommand(this, false), null);
        safeRegisterTab("menuunban", new MenuBanCommand(this, true), null);
        safeRegisterTab("economy", new EconomyControlCommand(this), null);
        safeRegisterTab("offend", new OffendCommand(this), null);

        DirectCommands directCmd = new DirectCommands(this, langManager, economyManager, cryptoManager, homeManager);
        safeRegisterTab("jeweler", new com.smpcore.commands.JewelerCommand(), null);
        safeRegisterTab("sell", directCmd, null);
        safeRegisterTab("shop", directCmd, null);
        safeRegisterTab("casino", directCmd, null);
        safeRegisterTab("crypto", directCmd, null);
        safeRegisterTab("pvphub", directCmd, null);
        safeRegisterTab("rtp", directCmd, null);
        safeRegisterTab("homes", directCmd, null);

        safeRegisterTab("ah", new AuctionCommand(this, auctionManager, economyManager, langManager), null);
        safeRegisterTab("insurance", new SimpleMenusCommand(this, "insurance"), null);
        safeRegisterTab("kill", new SimpleMenusCommand(this, "kill"), null);
        safeRegisterTab("settings", new SettingsCommand(this), null);
        safeRegisterTab("sm", new SuperMoneyCommand(this), null);

        safeRegisterTab("vault", (sender, cmd, label, args) -> {
            if (sender instanceof Player) vaultManager.openVault((Player) sender);
            return true;
        }, null);
    }

    private void safeRegisterTab(String name, CommandExecutor executor, TabCompleter completer) {
        getLogger().info(" Registering command: /" + name);
        if (getCommand(name) != null) {
            getCommand(name).setExecutor(executor);
            if (completer != null) getCommand(name).setTabCompleter(completer);
        } else {
            getLogger().warning(" Warning: The command /" + name + " is not registered in plugin.yml!");
        }
    }

    private void loadBannedPlayers() {
        if (getConfig().contains("menu-banned-players")) {
            for (String uuidStr : getConfig().getStringList("menu-banned-players")) {
                try {
                    menuBannedPlayers.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException ignored) {}
            }
            getLogger().info(" Loaded " + menuBannedPlayers.size() + " banned players.");
        }
    }

    public static void printLogo() {
        String m = "§6"; String r1 = "§c"; String r2 = "§6"; String r3 = "§e"; String r4 = "§a";
        Bukkit.getConsoleSender().sendMessage(new String[]{
                " ",
                m + " __  __ " + r1 + " _ " + r2 + "            " + r3 + " _ ",
                m + "|  \\/  |" + r1 + "(_) " + r2 + " __ _  " + r3 + "___" + r4 + "| |",
                m + "| |\\/| |" + r1 + "| |" + r2 + " / _` |" + r3 + "/ _ \\" + r4 + " |",
                m + "| |  | |" + r1 + "| |" + r2 + "| (_| |" + r3 + "  __/" + r4 + " |",
                m + "|_|  |_|" + r1 + "|_|" + r2 + " \\__, |" + r3 + "\\___|" + r4 + "_|",
                r2 + "            __/ |",
                r2 + "           |___/",
                "§7        vBETA by MigelxDev", "hope you enjoy! :)",
                " "
        });
    }

    @Override
    public org.bukkit.generator.ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new com.smpcore.utils.VoidChunkGenerator();
    }


    public CombatManager getCombatManager() { return combatManager; }
    public MissionManager getMissionManager() { return missionManager; }
    public LangManager getLangManager() { return langManager; }
    public CasinoManager getCasinoManager() { return casinoManager; }
    public InsuranceManager getInsuranceManager() { return insuranceManager; }
    public RegionManager getRegionManager() { return regionManager; }
    public com.smpcore.minigames.MinigameManager getMinigameManager() { return minigameManager; }
    public QueueManager getQueueManager() { return queueManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public TprManager getTprManager() { return tprManager; }
    public SettingsManager getSettingsManager() { return settingsManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public BountyManager getBountyManager() { return bountyManager; }
    public SellManager getSellManager() { return sellManager; }
    public SetupManager getSetupManager() { return setupManager; }
    public CryptoManager getCryptoManager() { return cryptoManager; }
    public SuperMoneyManager getSuperMoneyManager() { return superMoneyManager; }
    public EnchantmentPriceManager getEnchantmentPriceManager() { return enchantmentPriceManager; }
    public CustomItemManager getCustomItemManager() { return customItemManager; }
    public AuctionManager getAuctionManager() { return auctionManager; }
    public RankManager getRankManager() { return rankManager; }
    public KeyManager getKeyManager() { return keyManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public EconomyControlManager getEconomyControlManager() { return economyControlManager; } // <-- RESTAURADO AQUI
    public OrderManager getOrderManager() { return orderManager; }
    public VaultManager getVaultManager() { return vaultManager; }
    public PriceManager getPriceManager() { return priceManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public DuelManager getDuelManager() { return duelManager; }
    public LotteryManager getLotteryManager() { return lotteryManager; }
    public SpawnGenManager getSpawnGenManager() { return spawnGenManager; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public NPCManager getNPCManager() { return npcManager; }
    public ScoreboardHandler getScoreboardHandler() { return scoreboardHandler; }
    public TpaManager getTpaManager() { return tpaManager; }
    public Map<UUID, ConfirmationMenu> getPendingConfirmations() { return pendingConfirmations; }

    public boolean isMenuBanned(UUID uuid) { return menuBannedPlayers.contains(uuid); }
    public void setMenuBanned(UUID uuid, boolean banned) { if (banned) menuBannedPlayers.add(uuid); else menuBannedPlayers.remove(uuid); }
}