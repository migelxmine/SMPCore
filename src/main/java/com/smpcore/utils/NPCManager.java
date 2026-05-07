package com.smpcore.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.smpcore.SMPCore;
import com.smpcore.menus.CratesMainMenu;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class NPCManager implements Listener {

    private final SMPCore plugin;
    private final Map<Integer, NPCData> npcList = new HashMap<>();
    private final Map<UUID, Long> clickCooldowns = new HashMap<>();

    private Class<?> packetPlayOutPlayerInfo, packetPlayOutNamedEntitySpawn, packetPlayOutEntityHeadRotation, packetPlayOutEntityMetadata, enumPlayerInfoAction, serverPlayerClass;
    private Constructor<?> packetInfoConstructor, packetSpawnConstructor, packetHeadConstructor, packetMetaConstructor;
    private boolean initialized = false;

    private BukkitTask rtpParticleTask = null;

    public NPCManager(SMPCore plugin) {
        this.plugin = plugin;
        try {
            cleanupOldHolograms();
            setupReflection();
            initialized = true;
            plugin.getLogger().info("§a[NPCManager] System initialized successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe("§c[NPCManager] Critical Error: " + e.getMessage());
        }
    }

    private void cleanupOldHolograms() {
        NamespacedKey holoKey = new NamespacedKey(plugin, "npc_holo");
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntitiesByClass(ArmorStand.class)) {
                if (e.getPersistentDataContainer().has(holoKey, PersistentDataType.BYTE)) {
                    e.remove();
                }
            }
        }
    }

    private void setupReflection() throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        String nmsPackage = "net.minecraft.network.protocol.game.";
        try { serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer"); }
        catch (ClassNotFoundException e) {
            try { serverPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer"); }
            catch (ClassNotFoundException e2) { serverPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer"); }
        }
        try {
            packetPlayOutPlayerInfo = Class.forName(nmsPackage + "PacketPlayOutPlayerInfo");
            packetPlayOutNamedEntitySpawn = Class.forName(nmsPackage + "PacketPlayOutNamedEntitySpawn");
            packetPlayOutEntityHeadRotation = Class.forName(nmsPackage + "PacketPlayOutEntityHeadRotation");
            packetPlayOutEntityMetadata = Class.forName(nmsPackage + "PacketPlayOutEntityMetadata");
        } catch (ClassNotFoundException e) {
            packetPlayOutNamedEntitySpawn = Class.forName(nmsPackage + "ClientboundAddPlayerPacket");
            packetPlayOutEntityHeadRotation = Class.forName(nmsPackage + "ClientboundRotateHeadPacket");
            packetPlayOutEntityMetadata = Class.forName(nmsPackage + "ClientboundSetEntityDataPacket");
            try { packetPlayOutPlayerInfo = Class.forName(nmsPackage + "ClientboundPlayerInfoPacket"); }
            catch (Exception ex) { packetPlayOutPlayerInfo = Class.forName(nmsPackage + "PacketPlayOutPlayerInfo"); }
        }
        try { enumPlayerInfoAction = Class.forName(nmsPackage + "PacketPlayOutPlayerInfo$EnumPlayerInfoAction"); }
        catch (ClassNotFoundException e) {
            try { enumPlayerInfoAction = Class.forName(nmsPackage + "ClientboundPlayerInfoPacket$Action"); }
            catch (Exception ex) {}
        }
        if (enumPlayerInfoAction != null) {
            try { packetInfoConstructor = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Collection.class); }
            catch (Exception e) {
                try { packetInfoConstructor = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class); }
                catch (Exception ex) { packetInfoConstructor = packetPlayOutPlayerInfo.getConstructors()[0]; }
            }
        }
        packetSpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructors()[0];
        packetHeadConstructor = packetPlayOutEntityHeadRotation.getConstructors()[0];
        packetMetaConstructor = packetPlayOutEntityMetadata.getConstructors()[0];
    }

    public void createNPC(Location loc, String baseName, String skinValue, String skinSig, String actionType) {
        if (!initialized || loc.getWorld() == null) return;

        try {

            String profileName = baseName;
            if (actionType.equals("CASINO")) profileName = "§e§lᴄᴀsɪɴᴏ";
            else if (actionType.equals("RTP")) profileName = "§d§lʀᴛᴘ";
            else if (actionType.equals("KEYS")) profileName = "§6§lᴄʀᴀᴛᴇs";
            else if (actionType.equals("JEWELER")) profileName = "§b§lᴊᴇᴡᴇʟᴇʀ";
            else if (actionType.equals("AUCTION")) profileName = "§a§lᴀᴜᴄᴛɪᴏɴ";
            else if (actionType.equals("GINGERBREAD")) profileName = "§c§lᴛʀᴀᴅᴇ";

            // 🔥 A PROTEÇÃO CONTRA O CRASH ESTÁ AQUI 🔥
            if (profileName.length() > 16) {
                profileName = profileName.substring(0, 16);
            }

            GameProfile profile = new GameProfile(UUID.randomUUID(), profileName);
            if (skinValue != null && !skinValue.isEmpty()) {
                profile.getProperties().put("textures", new Property("textures", skinValue, skinSig));
            }

            Object nmsServer = getMethod(getOBCClass("CraftServer"), "getServer").invoke(Bukkit.getServer());
            Object nmsWorld = getMethod(getOBCClass("CraftWorld"), "getHandle").invoke(loc.getWorld());
            Constructor<?> constructor = serverPlayerClass.getConstructors()[0];
            Object npc = (constructor.getParameterCount() == 4) ? constructor.newInstance(nmsServer, nmsWorld, profile, null) : constructor.newInstance(nmsServer, nmsWorld, profile);

            Method setPos = null;
            for (Method m : serverPlayerClass.getMethods()) {
                if ((m.getName().equals("a") || m.getName().equals("setPos") || m.getName().equals("moveTo")) && m.getParameterCount() == 5 && m.getParameterTypes()[0] == double.class) {
                    setPos = m; break;
                }
            }
            if (setPos != null) setPos.invoke(npc, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            else {
                Method setPosOld = getMethod(serverPlayerClass, "setPos", double.class, double.class, double.class);
                if (setPosOld != null) setPosOld.invoke(npc, loc.getX(), loc.getY(), loc.getZ());
            }

            int id = 0;
            try {
                for (Method m : serverPlayerClass.getMethods()) {
                    if (m.getParameterCount() == 0 && m.getReturnType() == int.class) {
                        if (m.getName().equals("getId") || m.getName().equals("ae") || m.getName().equals("ah")) {
                            id = (int) m.invoke(npc); break;
                        }
                    }
                }
            } catch (Exception ignored) {}

            if (id == 0) {
                Class<?> entityClass = serverPlayerClass.getSuperclass();
                while (entityClass != null && !entityClass.getSimpleName().equals("Entity")) entityClass = entityClass.getSuperclass();
                if (entityClass != null) {
                    Field f = entityClass.getDeclaredField("id");
                    f.setAccessible(true);
                    id = f.getInt(npc);
                }
            }

            spawnClickHologram(loc.clone().add(0, 2.3, 0));

            if (actionType.equals("RTP")) {
                startRtpParticles(loc.clone());
            }

            NPCData data = new NPCData(npc, actionType, loc, id);
            npcList.put(id, data);
            plugin.getLogger().info("§a[NPCManager] Created NPC: " + profileName + " (ID: " + id + ")");

        } catch (Exception e) {
            plugin.getLogger().warning("§c[NPCManager] Failed to create NPC: " + e.getMessage());
        }
    }

    private void spawnClickHologram(Location loc) {
        ArmorStand holo = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        holo.setVisible(false);
        holo.setMarker(true);
        holo.setGravity(false);
        holo.setCustomNameVisible(true);
        holo.setCustomName("§7Click to access!");

        holo.getPersistentDataContainer().set(new NamespacedKey(plugin, "npc_holo"), PersistentDataType.BYTE, (byte) 1);
    }

    private void startRtpParticles(Location loc) {
        if (rtpParticleTask != null) rtpParticleTask.cancel();
        rtpParticleTask = new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                if (loc.getWorld() == null) return;
                t += 0.2;
                double r = 0.7;
                double x = r * Math.cos(t);
                double z = r * Math.sin(t);
                double y = (t % 15) / 5.0;

                loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void spawnNPCFor(Player p, NPCData data) {
        if (!initialized) return;
        try {
            Object connection = getConnection(p);

            Object actionAdd = Enum.valueOf((Class<Enum>) enumPlayerInfoAction, "ADD_PLAYER");
            Object packetInfo = packetInfoConstructor.newInstance(actionAdd, Collections.singletonList(data.nmsEntity));
            sendPacket(connection, packetInfo);

            Object packetSpawn = packetSpawnConstructor.newInstance(data.nmsEntity);
            sendPacket(connection, packetSpawn);

            try {
                Method getData = getMethod(serverPlayerClass, "ai");
                if (getData == null) getData = getMethod(serverPlayerClass, "getEntityData");

                if (getData != null) {
                    Object dataWatcher = getData.invoke(data.nmsEntity);
                    Method getItems = null;
                    for(Method m : dataWatcher.getClass().getMethods()) {
                        if ((m.getName().equals("c") || m.getName().equals("packDirty") || m.getName().equals("getAll"))
                                && m.getReturnType() == List.class) {
                            getItems = m;
                            break;
                        }
                    }

                    if (getItems != null) {
                        Object items = getItems.invoke(dataWatcher);
                        if (items != null) {
                            Object metaPacket = packetMetaConstructor.newInstance(data.id, items);
                            sendPacket(connection, metaPacket);
                        }
                    }
                }
            } catch (Exception ignored) { }

            Object packetHead = packetHeadConstructor.newInstance(data.nmsEntity, (byte) (data.location.getYaw() * 256 / 360));
            sendPacket(connection, packetHead);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    Object actionRemove = Enum.valueOf((Class<Enum>) enumPlayerInfoAction, "REMOVE_PLAYER");
                    Object packetRemove = packetInfoConstructor.newInstance(actionRemove, Collections.singletonList(data.nmsEntity));
                    sendPacket(connection, packetRemove);
                } catch (Exception ignored) {}
            }, 40L);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Object connection, Object packet) {
        try {
            Method sendMethod = null;
            String[] names = {"sendPacket", "send", "a", "sendPacket"};

            for (String name : names) {
                for (Method m : connection.getClass().getMethods()) {
                    if (m.getName().equals(name) && m.getParameterCount() == 1) {
                        if (m.getParameterTypes()[0].isAssignableFrom(packet.getClass())) {
                            sendMethod = m;
                            break;
                        }
                    }
                }
                if (sendMethod != null) break;
            }

            if (sendMethod != null) sendMethod.invoke(connection, packet);
        } catch (Exception e) {}
    }

    private void inject(Player player) {
        try {
            Object craftPlayer = player;
            Object handle = getMethod(craftPlayer.getClass(), "getHandle").invoke(craftPlayer);

            Object connection = getFieldByType(handle, "ServerGamePacketListenerImpl", "PlayerConnection");
            if (connection == null) connection = getField(handle, "b");
            if (connection == null) connection = getField(handle, "c");
            if (connection == null) connection = getField(handle, "connection");

            Object networkManager = getFieldByType(connection, "Connection", "NetworkManager");
            if (networkManager == null) networkManager = getField(connection, "a");
            if (networkManager == null) networkManager = getField(connection, "h");
            if (networkManager == null) networkManager = getField(connection, "connection");

            Channel channel = (Channel) getFieldByType(networkManager, "Channel");
            if (channel == null) channel = (Channel) getField(networkManager, "m");
            if (channel == null) channel = (Channel) getField(networkManager, "channel");

            if (channel != null && channel.pipeline().get("NPCReader") == null) {
                ChannelDuplexHandler handler = new ChannelDuplexHandler() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                        String packetName = packet.getClass().getSimpleName();
                        if (packetName.equals("PacketPlayInUseEntity") || packetName.equals("ServerboundInteractPacket")) {
                            int entityId = getEntityIdFromPacket(packet);
                            if (entityId != -1 && npcList.containsKey(entityId)) {
                                Bukkit.getScheduler().runTask(plugin, () -> handleClick(player, npcList.get(entityId)));
                            }
                        }
                        super.channelRead(ctx, packet);
                    }
                };
                channel.pipeline().addBefore("packet_handler", "NPCReader", handler);
            } else if (channel == null) {
                plugin.getLogger().warning("§c[NPCManager] Failed to inject Netty Channel for " + player.getName());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("§c[NPCManager] Injection Error: " + e.getMessage());
        }
    }

    private void handleClick(Player p, NPCData data) {
        if (clickCooldowns.containsKey(p.getUniqueId())) {
            if (System.currentTimeMillis() - clickCooldowns.get(p.getUniqueId()) < 500) return;
        }
        clickCooldowns.put(p.getUniqueId(), System.currentTimeMillis());

        switch (data.type) {
            case "CASINO": plugin.getCasinoManager().openCasinoMenu(p); break;
            case "RTP": plugin.getTprManager().teleport(p, Bukkit.getWorld("world"), 0.0); break;
            case "KEYS": com.smpcore.menus.CratesMainMenu.open(p, plugin); break;
            case "JEWELER": com.smpcore.menus.JewelerMenu.open(p); break;
            case "AUCTION": p.performCommand("ah"); break;
            case "GINGERBREAD": com.smpcore.menus.KeyTradeMenu.open(p, plugin); break;

            case "ARCADE_ICE_CONTROL":
                plugin.getMinigameManager().joinArcade(p, "Ice Control");
                break;
            case "ARCADE_BLOCK_SHUFFLE":
                plugin.getMinigameManager().joinArcade(p, "Block Shuffle");
                break;
            case "ARCADE_MINE_RUSH":
                plugin.getMinigameManager().joinArcade(p, "Mine Rush");
                break;
            case "ARCADE_BUILD_MEMORY":
                plugin.getMinigameManager().joinArcade(p, "Build Memory");
                break;
        }
    }

    private int getEntityIdFromPacket(Object packet) {
        try {
            for (Field f : packet.getClass().getDeclaredFields()) {
                if (f.getType() == int.class) { f.setAccessible(true); return f.getInt(packet); }
            }
            Field f = packet.getClass().getDeclaredField("a");
            f.setAccessible(true); return f.getInt(packet);
        } catch (Exception e) { return -1; }
    }

    private Object getFieldByType(Object obj, String... typeNames) {
        if (obj == null) return null;
        for (Field f : obj.getClass().getDeclaredFields()) {
            for (String typeName : typeNames) {
                if (f.getType().getSimpleName().equals(typeName) || (typeName.equals("Channel") && Channel.class.isAssignableFrom(f.getType()))) {
                    try { f.setAccessible(true); return f.get(obj); } catch (Exception ignored) {}
                }
            }
        }
        if (obj.getClass().getSuperclass() != null) {
            for (Field f : obj.getClass().getSuperclass().getDeclaredFields()) {
                for (String typeName : typeNames) {
                    if (f.getType().getSimpleName().equals(typeName) || (typeName.equals("Channel") && Channel.class.isAssignableFrom(f.getType()))) {
                        try { f.setAccessible(true); return f.get(obj); } catch (Exception ignored) {}
                    }
                }
            }
        }
        return null;
    }

    private Object getField(Object obj, String name) {
        if (obj == null) return null;
        try { Field f = obj.getClass().getDeclaredField(name); f.setAccessible(true); return f.get(obj); }
        catch (Exception e) {
            if (obj.getClass().getSuperclass() != null) {
                try { Field f = obj.getClass().getSuperclass().getDeclaredField(name); f.setAccessible(true); return f.get(obj); }
                catch (Exception ex) {}
            }
        }
        return null;
    }

    private Object getConnection(Player player) throws Exception {
        Object handle = getMethod(player.getClass(), "getHandle").invoke(player);
        Object connection = getFieldByType(handle, "ServerGamePacketListenerImpl", "PlayerConnection");
        if (connection == null) connection = getField(handle, "b");
        if (connection == null) connection = getField(handle, "c");
        if (connection == null) connection = getField(handle, "connection");
        return connection;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { inject(e.getPlayer()); spawnAll(e.getPlayer()); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { clickCooldowns.remove(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) { spawnAll(e.getPlayer()); }

    private void spawnAll(Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (NPCData data : npcList.values()) {
                if (data.location.getWorld() != null && data.location.getWorld().getName().equals(p.getWorld().getName())) {
                    spawnNPCFor(p, data);
                }
            }
        }, 20L);
    }

    private Class<?> getOBCClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), args)) return m;
        }
        return null;
    }

    private static class NPCData {
        Object nmsEntity; String type; Location location; int id;
        public NPCData(Object nmsEntity, String type, Location location, int id) {
            this.nmsEntity = nmsEntity; this.type = type; this.location = location; this.id = id;
        }
    }
}