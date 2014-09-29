/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.emblems;

import mods.railcraft.client.util.textures.Texture;
import cpw.mods.fml.common.CertificateHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumRarity;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemPackageManager implements IEmblemPackageManager {

    public static final EmblemPackageManager instance;
    public static final Texture blankEmblem = new BlankTexture();
    private static final Map<String, Emblem> loadedEmblems = new HashMap<String, Emblem>();
    private static final Map<String, EmblemTexture> emblemTextures = new HashMap<String, EmblemTexture>();

    static {
        instance = new EmblemPackageManager();
        EmblemToolsClient.packageManager = instance;
        ResourceLocation location = new ResourceLocation("railcraft:textures/emblems/placeholder");
        Minecraft.getMinecraft().renderEngine.loadTexture(location, blankEmblem);
    }

    public void init() {
    }

    @Override
    public EmblemTexture getEmblemTexture(String identifier) {
        EmblemTexture texture = emblemTextures.get(identifier);
        if (texture == null) {
            texture = new EmblemTexture(identifier);
            emblemTextures.put(identifier, texture);
            Minecraft.getMinecraft().renderEngine.loadTexture(texture.getLocation(), texture);
        }
        return texture;
    }

    @Override
    public Emblem getEmblem(String identifier) {
        return loadedEmblems.get(identifier);
    }

    @Override
    public ResourceLocation getEmblemTextureLocation(String ident) {
        return getEmblemTexture(ident).getLocation();
    }

    public Emblem getEmblemOrLoad(String identifier) {
        Emblem emblem = loadedEmblems.get(identifier);
        if (emblem != null)
            return emblem;
        getEmblemTexture(identifier);
        return null;
    }

    public void loadEmblems() {
        File emblems = new File(Minecraft.getMinecraft().mcDataDir, "mods/railcraft/emblems");
        if (!emblems.exists())
            emblems.mkdirs();

        if (!emblems.isDirectory())
            return;

        for (File file : emblems.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                String identifier = file.getName().replace(".jar", "").replace("emblem-", "");
                loadEmblem(file, identifier);
            }
        }
    }

    public File getEmblemFile(String identifier) {
        File emblems = new File(Minecraft.getMinecraft().mcDataDir, "mods/railcraft/emblems");
        if (!emblems.exists())
            emblems.mkdirs();

        File emblem = new File(emblems, "emblem-" + identifier + ".jar");
        return emblem;
    }

    public Emblem loadEmblem(String identifier) {
        return loadEmblem(getEmblemFile(identifier), identifier);
    }

    public Emblem loadEmblem(File file, String identifier) {
        JarFile jar = null;
        try {
            if (file == null)
                throw new IOException("Cannot find file.");

            jar = new JarFile(file);

            // Players can't make their own Emblems (collectable!),
            // so we need to check if the jar is signed
            Manifest man = jar.getManifest();
            if (man == null)
                throw new SecurityException("Emblem Jar is not signed!");

            List<JarEntry> jarEntries = new ArrayList<JarEntry>();

            byte[] buffer = new byte[8192];
            Enumeration entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                jarEntries.add(entry);
                InputStream is = jar.getInputStream(entry);
                while (is.read(buffer, 0, buffer.length) != -1) {
                    // Force security check
                }
                is.close();
            }

            for (JarEntry entry : jarEntries) {
                if (entry.isDirectory())
                    continue;
                if (entry.getName().endsWith(".class"))
                    throw new SecurityException("Emblem Jars should not have code!");
                Certificate[] certs = entry.getCertificates();
                if ((certs == null) || (certs.length == 0)) {
                    if (!entry.getName().startsWith("META-INF"))
                        throw new SecurityException("Emblem Jar is not signed!");
                } else if (!CertificateHelper.getFingerprint(certs[0]).equals("a0c255ac501b2749537d5824bb0f0588bf0320fa"))
                    throw new SecurityException("Emblem Jar is not signed!");
            }

            LaunchClassLoader classLoader = ((LaunchClassLoader) EmblemPackageManager.class.getClassLoader());
            classLoader.addURL(file.toURI().toURL());

            for (JarEntry entry : jarEntries) {
                if (entry.isDirectory())
                    continue;
                if (!entry.getName().endsWith(".meta"))
                    continue;
                Properties emblemData = new Properties();
                InputStream iStream = jar.getInputStream(entry);
                try {
                    emblemData.load(iStream);
                } finally {
                    iStream.close();
                }
                int rarity = 0;
                try {
                    rarity = Integer.parseInt(emblemData.getProperty("rarity"));
                    EnumRarity[] rarities = EnumRarity.values();
                    if (rarity >= rarities.length)
                        rarity = rarities.length - 1;
                } catch (NumberFormatException numEx) {
                }
                boolean hasEffect = Boolean.parseBoolean(emblemData.getProperty("effect"));
                String texturePath = parseTexturePath(emblemData.getProperty("texture"), identifier);
                Emblem emblem = new Emblem(identifier, texturePath, emblemData.getProperty("name"), rarity, hasEffect);
                loadedEmblems.put(emblem.identifier, emblem);
                Game.log(Level.INFO, "Loaded Emblem - \"{0}\"", emblem.displayName);
                return emblem;
            }
        } catch (IOException ex) {
            Game.log(Level.WARN, "Failed to load Emblem due to IO exception - \"{0}\"", file.getName().replace(".jar", ""));
            Game.log(Level.WARN, "Reason: {0}", ex);
            return null;
        } catch (SecurityException ex) {
            Game.log(Level.WARN, "Failed to load Emblem due to security failure - \"{0}\"", file.getName().replace(".jar", ""));
            Game.log(Level.WARN, "Reason: {0}", ex);
            return null;
        } finally {
            try {
                if (jar != null)
                    jar.close();
            } catch (IOException ex) {
            }
        }
        return null;
    }

    private String parseTexturePath(String path, String idenifier) {
        path = path.replace("%emblem%", "/assets/railcraft/textures/emblems/" + idenifier);
        path = path.replace("%minecraft_block%", "/assets/minecraft/textures/blocks");
        path = path.replace("%minecraft_item%", "/assets/minecraft/textures/items");
        path = path.replace("%railcraft_item%", "/assets/railcraft/textures/items");
        return path;
    }

}
