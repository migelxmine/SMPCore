package com.smpcore.utils;

import com.smpcore.SMPCore;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupManager implements Listener {

    private final SMPCore plugin;
    private final Map<UUID, Boolean> inSetupMode = new HashMap<>();
    private final Map<UUID, String> pendingNames = new HashMap<>();

    public SetupManager(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void startSetup(Player p) {
        if (!p.hasPermission("smpcore.admin")) {
            p.sendMessage("§cYou don't have permission to do this.");
            return;
        }

        inSetupMode.put(p.getUniqueId(), true);

        p.sendMessage(" ");
        p.sendMessage("§e§lSMPCore Setup Wizard");
        p.sendMessage("§7Let's configure your server's identity.");
        p.sendMessage("§fPlease type your §bServer Name §fin the chat (e.g. Migel SMP):");
        p.sendMessage(" ");
        Sounds.playClick(p);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (inSetupMode.containsKey(p.getUniqueId()) && inSetupMode.get(p.getUniqueId())) {
            e.setCancelled(true);

            String serverName = e.getMessage().trim();
            if (serverName.length() > 16) {
                p.sendMessage("§cServer name is too long! Max 16 characters. Try again:");
                return;
            }

            inSetupMode.remove(p.getUniqueId());
            pendingNames.put(p.getUniqueId(), serverName);


            Bukkit.getScheduler().runTask(plugin, () -> showColorOptions(p, serverName));
        }
    }

    private void showColorOptions(Player p, String name) {
        p.sendMessage(" ");
        p.sendMessage("§aServer name registered as: §f§l" + name);
        p.sendMessage("§7Please select a color theme for your Scoreboard by clicking a number below:");
        p.sendMessage(" ");

        sendColorOption(p, 1, name, "Ocean Blue", "§b", "§9");
        sendColorOption(p, 2, name, "Crimson Red", "§c", "§4");
        sendColorOption(p, 3, name, "Toxic Green", "§a", "§2");
        sendColorOption(p, 4, name, "Royal Gold", "§e", "§6");
        sendColorOption(p, 5, name, "Amethyst Purple", "§d", "§5");
        sendColorOption(p, 6, name, "Aqua Cyan", "§b", "§3");
        sendColorOption(p, 7, name, "Monochrome", "§f", "§8");
        sendColorOption(p, 8, name, "Sunset", "§6", "§c", "§e");

        p.sendMessage(" ");
    }

    private void sendColorOption(Player p, int id, String name, String themeName, String... colors) {
        String formatted = applyGradient(name, colors);

        TextComponent msg = new TextComponent("§8[" + id + "] " + formatted + " §7(" + themeName + ")");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setup finish " + id));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to select §f" + themeName + "§7!")));

        p.spigot().sendMessage(msg);
    }

    public void finishSetup(Player p, int colorId) {
        if (!pendingNames.containsKey(p.getUniqueId())) {
            p.sendMessage("§cSession expired. Type /setup again.");
            return;
        }

        String rawName = pendingNames.get(p.getUniqueId());
        String formattedName = "";

        switch (colorId) {
            case 1: formattedName = applyGradient(rawName, "§b", "§9"); break;
            case 2: formattedName = applyGradient(rawName, "§c", "§4"); break;
            case 3: formattedName = applyGradient(rawName, "§a", "§2"); break;
            case 4: formattedName = applyGradient(rawName, "§e", "§6"); break;
            case 5: formattedName = applyGradient(rawName, "§d", "§5"); break;
            case 6: formattedName = applyGradient(rawName, "§b", "§3"); break;
            case 7: formattedName = applyGradient(rawName, "§f", "§8"); break;
            case 8: formattedName = applyGradient(rawName, "§6", "§c", "§e"); break;
            default: formattedName = "§f§l" + rawName; break;
        }


        String scoreboardTitle = "    " + formattedName + "    ";

        plugin.getConfig().set("server-name", rawName);
        plugin.getConfig().set("scoreboard-title", scoreboardTitle);
        plugin.saveConfig();

        pendingNames.remove(p.getUniqueId());

        p.sendMessage(" ");
        p.sendMessage("§a§lSUCCESS! §7Your scoreboard theme has been applied.");
        Sounds.playSuccess(p);
        spawnFirework(p);


        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getScoreboard() != null && online.getScoreboard().getObjective("smpcore") != null) {
                online.getScoreboard().getObjective("smpcore").setDisplayName(scoreboardTitle);
            }
        }
    }

    private String applyGradient(String text, String... colors) {
        StringBuilder sb = new StringBuilder();
        int colorIndex = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                sb.append(" ");
                continue;
            }
            sb.append(colors[colorIndex % colors.length]).append("§l").append(c);
            colorIndex++;
        }
        return sb.toString();
    }

    private void spawnFirework(Player p) {
        Firework fw = p.getWorld().spawn(p.getLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.ORANGE).with(FireworkEffect.Type.BALL_LARGE).withTrail().build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}