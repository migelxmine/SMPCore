package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OrderManager {

    private final SMPCore plugin;
    private final List<BuyOrder> activeOrders = new ArrayList<>();


    private final Map<UUID, List<ItemStack>> mailbox = new HashMap<>();

    private final File ordersFile;
    private final File mailboxFile;

    public OrderManager(SMPCore plugin) {
        this.plugin = plugin;
        this.ordersFile = new File(plugin.getDataFolder(), "buy_orders.yml");
        this.mailboxFile = new File(plugin.getDataFolder(), "mailbox.yml");
        loadOrders();
        loadMailbox();
    }

    public void createOrder(Player buyer, Material mat, int amount, double totalPrice) {
        activeOrders.add(new BuyOrder(UUID.randomUUID(), buyer.getUniqueId(), buyer.getName(), mat, amount, totalPrice));
        saveOrders();
    }

    public List<BuyOrder> getActiveOrders() {
        return activeOrders;
    }

    public BuyOrder getOrderById(UUID id) {
        return activeOrders.stream().filter(o -> o.id.equals(id)).findFirst().orElse(null);
    }

    public void fillOrder(Player seller, BuyOrder order, int amountFilled, EconomyManager eco) {

        order.filledAmount += amountFilled;
        if (order.getRemaining() <= 0) {
            activeOrders.remove(order);
        }
        saveOrders();


        ItemStack deliveredItems = new ItemStack(order.material, amountFilled);
        mailbox.computeIfAbsent(order.buyer, k -> new ArrayList<>()).add(deliveredItems);
        saveMailbox();


        Player buyerPlayer = plugin.getServer().getPlayer(order.buyer);
        if (buyerPlayer != null) {
            buyerPlayer.sendMessage("§a§lORDER FILLED! §7Someone sold §f" + amountFilled + "x " + order.material.name() + " §7to your order!");
            buyerPlayer.sendMessage("§eType §b/order claim §eto receive your items.");
            Sounds.playSuccess(buyerPlayer);
        }
    }

    public void removeOrder(BuyOrder order) {
        activeOrders.remove(order);
        saveOrders();
    }


    public void claimMail(Player p) {
        UUID uuid = p.getUniqueId();
        if (!mailbox.containsKey(uuid) || mailbox.get(uuid).isEmpty()) {
            p.sendMessage("§cYou don't have any items waiting in your Mailbox.");
            Sounds.playError(p);
            return;
        }

        List<ItemStack> items = mailbox.get(uuid);
        Iterator<ItemStack> iterator = items.iterator();
        boolean inventoryFull = false;

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(item);
                iterator.remove();
            } else {
                inventoryFull = true;
                break;
            }
        }

        if (items.isEmpty()) {
            mailbox.remove(uuid);
        }
        saveMailbox();

        if (inventoryFull) {
            p.sendMessage("§eYou claimed some items, but your inventory is full! Make space and type §b/order claim §eagain.");
            Sounds.playError(p);
        } else {
            p.sendMessage("§aSuccessfully claimed all items from your Mailbox!");
            Sounds.playSuccess(p);
        }
    }

    public static class BuyOrder {
        public UUID id;
        public UUID buyer;
        public String buyerName;
        public Material material;
        public int amount;
        public double totalPrice;
        public int filledAmount = 0;

        public BuyOrder(UUID id, UUID buyer, String buyerName, Material mat, int amount, double totalPrice) {
            this.id = id;
            this.buyer = buyer;
            this.buyerName = buyerName;
            this.material = mat;
            this.amount = amount;
            this.totalPrice = totalPrice;
        }

        public UUID getId() { return id; }
        public UUID getBuyer() { return buyer; }
        public String getBuyerName() { return buyerName; }
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public double getTotalPrice() { return totalPrice; }
        public int getRemaining() { return amount - filledAmount; }
    }



    public void saveOrders() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (int i = 0; i < activeOrders.size(); i++) {
            BuyOrder o = activeOrders.get(i);
            String path = "orders." + i;
            cfg.set(path + ".id", o.id.toString());
            cfg.set(path + ".buyer", o.buyer.toString());
            cfg.set(path + ".name", o.buyerName);
            cfg.set(path + ".material", o.material.name());
            cfg.set(path + ".amount", o.amount);
            cfg.set(path + ".price", o.totalPrice);
            cfg.set(path + ".filled", o.filledAmount);
        }
        try { cfg.save(ordersFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadOrders() {
        if (!ordersFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(ordersFile);
        if (!cfg.contains("orders")) return;
        for (String s : cfg.getConfigurationSection("orders").getKeys(false)) {
            String path = "orders." + s;
            BuyOrder bo = new BuyOrder(
                    UUID.fromString(cfg.getString(path + ".id")),
                    UUID.fromString(cfg.getString(path + ".buyer")),
                    cfg.getString(path + ".name"),
                    Material.valueOf(cfg.getString(path + ".material")),
                    cfg.getInt(path + ".amount"),
                    cfg.getDouble(path + ".price")
            );
            bo.filledAmount = cfg.getInt(path + ".filled");
            activeOrders.add(bo);
        }
    }

    public void saveMailbox() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<ItemStack>> entry : mailbox.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try { cfg.save(mailboxFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadMailbox() {
        if (!mailboxFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(mailboxFile);
        for (String key : cfg.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<ItemStack> items = (List<ItemStack>) cfg.getList(key);
            if (items != null) mailbox.put(uuid, items);
        }
    }
}