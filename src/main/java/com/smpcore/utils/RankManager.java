package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RankManager {

    private final SMPCore plugin;
    private File file;
    private FileConfiguration config;

    
    public enum SocialRank {
        MEMBER("§7Member"),
        EXCLUSIVE("§6Exclusive"),
        VIP("§aVIP");

        private final String display;
        SocialRank(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }

    public enum StaffRole {
        NONE("§8Player"),
        EV_HOST("§dEvent Host"),
        JR_ADMIN("§9Jr. Admin"),
        SR_ADMIN("§cSr. Admin"),
        OWNER("§4§lOWNER");

        private final String display;
        StaffRole(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }

    public RankManager(SMPCore plugin) {
        this.plugin = plugin;
        createFile();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), "ranks.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Error wile trying to create ranks.yml!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error try saving ranks.yml!");
        }
    }

    
    
    

    public SocialRank getSocialRank(Player p) {
        return getSocialRank(p.getUniqueId());
    }

    public SocialRank getSocialRank(UUID uuid) {
        checkExpiry(uuid);
        String rankStr = config.getString(uuid.toString() + ".social_rank", "MEMBER");
        try {
            return SocialRank.valueOf(rankStr);
        } catch (Exception e) {
            return SocialRank.MEMBER;
        }
    }

    public void setSocialRank(UUID uuid, SocialRank rank, long durationMillis) {
        config.set(uuid.toString() + ".social_rank", rank.name());

        
        if (rank == SocialRank.MEMBER) {
            config.set(uuid.toString() + ".expiry", 0L);
        } else {
            long expiryTime = System.currentTimeMillis() + durationMillis;
            config.set(uuid.toString() + ".expiry", expiryTime);
        }
        save();
    }

    public long getRankExpiry(UUID uuid) {
        return config.getLong(uuid.toString() + ".expiry", 0L);
    }

    
    public void checkExpiry(UUID uuid) {
        long expiry = config.getLong(uuid.toString() + ".expiry", 0L);
        if (expiry > 0 && System.currentTimeMillis() >= expiry) {
            
            config.set(uuid.toString() + ".social_rank", SocialRank.MEMBER.name());
            config.set(uuid.toString() + ".expiry", 0L);
            save();

            Player p = plugin.getServer().getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.sendMessage("§c§lWARNING: §7Your subscription expired. You are now a member!");
                Sounds.playError(p);
            }
        }
    }

    public boolean hasUsedTrial(UUID uuid) {
        return config.getBoolean(uuid.toString() + ".trial_used", false);
    }

    public void startTrial(Player p, SocialRank trialRank) {
        if (hasUsedTrial(p.getUniqueId())) {
            p.sendMessage("§cAlready used the free trial! ");
            return;
        }

        
        long threeDays = 259200000L;
        setSocialRank(p.getUniqueId(), trialRank, threeDays);

        
        config.set(p.getUniqueId().toString() + ".trial_used", true);
        save();

        p.sendMessage("§a§lSucess you are now a §f" + trialRank.getDisplay() + " §7for 3 days!");
        Sounds.playSuccess(p);
    }

    public StaffRole getStaffRole(Player p) {
        return getStaffRole(p.getUniqueId());
    }

    public StaffRole getStaffRole(UUID uuid) {
        String roleStr = config.getString(uuid.toString() + ".staff_role", "NONE");
        try {
            return StaffRole.valueOf(roleStr);
        } catch (Exception e) {
            return StaffRole.NONE;
        }
    }

    public void setStaffRole(UUID uuid, StaffRole role) {
        config.set(uuid.toString() + ".staff_role", role.name());
        save();
    }
}