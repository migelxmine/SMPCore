package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ConsoleEffect {

    public static void showLogLoading(SMPCore plugin, String commandName, String logType) {

        
        Bukkit.getConsoleSender().sendMessage(new String[]{
                "§8",
                "§8================= §9§lSMPCore Logs §8=================",
                "§eShowing logs...",
                "§7Task: §fexecute command §9" + commandName,
                "§aProgress:"
        });

        
        new BukkitRunnable() {
            int step = 0;
            final int maxSteps = 8;

            @Override
            public void run() {
                
                StringBuilder bar = new StringBuilder("§8<§a");
                for (int i = 0; i < maxSteps; i++) {
                    if (i < step) bar.append("#");
                    else bar.append("§7-");
                }
                bar.append("§8>");

                
                Bukkit.getConsoleSender().sendMessage(" " + bar.toString());

                step++;

                
                if (step > maxSteps) {
                    Bukkit.getConsoleSender().sendMessage(new String[]{
                            " ",
                            "§a✔ Log loaded successfully §8[§9CLICK HERE§8]", 
                            "§7(Type §f/economy see " + logType + " §7to open)",
                            "§8====================================================",
                            "§8"
                    });
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 5L); 
    }
}