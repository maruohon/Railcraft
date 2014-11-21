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
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.logging.log4j.Level;
import javax.imageio.ImageIO;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemTexture extends Texture {

    private final String identifier;
    private boolean cachedImage = false;
    private boolean triedDownloading;
    private final ResourceLocation location;

    public EmblemTexture(String ident) {
        this.identifier = ident;
        this.location = new ResourceLocation("railcraft:textures/emblems/" + identifier);
    }

    /**
     * Load texture
     * @param resourceManager
     * @throws java.io.IOException
     */
    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (imageData == null && !loadEmblemTexture() && !triedDownloading) {
            triedDownloading = true;
            Game.log(Level.INFO, "Attempting to download Emblem - \"emblem-{0}\"", identifier);
            EmblemDownloader downloader = new EmblemDownloader();
            downloader.setDaemon(true);
            downloader.setName("Emblem downloader (" + identifier + ")");
            downloader.start();
        }
    }

    private boolean loadEmblemTexture() throws IOException {
        Emblem emblem = EmblemPackageManager.instance.getEmblem(identifier);
        if (emblem == null)
            return false;

//        String resource = "/assets/railcraft/textures/emblems/" + identifier + ".png";
        InputStream istream = EmblemPackageManager.class.getResourceAsStream(emblem.textureFile);

        if (istream == null)
            return false;

        imageData = ImageIO.read(istream);

        istream.close();
        return true;
    }

    private class EmblemDownloader extends Thread {

        @Override
        public void run() {
            FileOutputStream fos = null;
            try {
                URL url = new URL(RailcraftConstants.EMBLEM_URL + "emblem-" + identifier + ".jar");
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                fos = new FileOutputStream(EmblemPackageManager.instance.getEmblemFile(identifier));
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                Game.log(Level.INFO, "Downloaded Emblem: \"emblem-{0}\"", identifier);
                EmblemPackageManager.instance.loadEmblem(identifier);
                loadEmblemTexture();
            } catch (Exception ex) {
                Game.log(Level.WARN, "Failed to download Emblem: \"emblem-{0}\". Reason: {1}", identifier, ex);
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (IOException io) {
                }
            }
        }

    }

    @Override
    public int getGlTextureId() {
        int textureIndex = super.getGlTextureId();

        if (!cachedImage && imageData != null) {
            TextureUtil.uploadTextureImage(textureIndex, imageData);
            cachedImage = true;
        }

        if (imageData == null)
            return EmblemPackageManager.blankEmblem.getGlTextureId();

        return textureIndex;
    }

    @Override
    public BufferedImage getImage() {
        if (imageData == null)
            return EmblemPackageManager.blankEmblem.getImage();
        return imageData;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("Emblem Texture - %s - Location: %s", identifier, location);
    }

}
