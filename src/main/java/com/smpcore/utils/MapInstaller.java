package com.smpcore.utils;

import com.smpcore.SMPCore;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapInstaller {

    public static void installMaps(SMPCore plugin) {

        installWorld(plugin, "SMP_Spawn", "SMP_Spawn.zip");


        installWorld(plugin, "SMP_Lobby", "SMP_Lobby.zip");
    }

    private static void installWorld(SMPCore plugin, String folderName, String zipName) {
        File worldContainer = Bukkit.getWorldContainer();
        File worldFolder = new File(worldContainer, folderName);

        if (!worldFolder.exists()) {
            plugin.getLogger().info("[MapInstaller] Folder '" + folderName + "' not found. Installing from ZIP...");
            worldFolder.mkdirs();

            try (InputStream in = plugin.getResource(zipName)) {
                if (in == null) {
                    plugin.getLogger().severe("[MapInstaller] CRITICAL ERROR: " + zipName + " not found inside the JAR!");
                    return;
                }

                unzip(in, worldFolder);
                plugin.getLogger().info("[MapInstaller] Map '" + folderName + "' installed successfully!");

            } catch (Exception e) {
                plugin.getLogger().severe("[MapInstaller] Error extracting map: " + folderName);
                e.printStackTrace();
            }
        }
    }

    private static void unzip(InputStream source, File targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(source)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());


                if (!file.getCanonicalPath().startsWith(targetDir.getCanonicalPath())) {
                    throw new IOException("Invalid ZIP entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                    }
                }
            }
        }
    }
}