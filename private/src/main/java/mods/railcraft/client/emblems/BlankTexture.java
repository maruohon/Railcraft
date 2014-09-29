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
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
class BlankTexture extends Texture {

    private boolean cachedImage = false;

    @Override
    public void loadTexture(IResourceManager resourcemanager) throws IOException {
        if (this.imageData == null) {
            String resource = "/assets/railcraft/textures/emblems/placeholder.png";
            InputStream istream = EmblemPackageManager.class.getResourceAsStream(resource);
            if (istream == null) return;
            imageData = ImageIO.read(istream);
            istream.close();
        }
    }

    @Override
    public int getGlTextureId() {
        int textureIndex = super.getGlTextureId();
        if (!cachedImage && imageData != null) {
            TextureUtil.uploadTextureImage(textureIndex, imageData);
            cachedImage = true;
        }
        return textureIndex;
    }

}
