/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.emblems;

import mods.railcraft.client.render.EmblemRenderHelper;
import mods.railcraft.client.render.RenderEmblemItem;
import mods.railcraft.common.emblems.EmblemProxy;
import mods.railcraft.common.emblems.ItemEmblem;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ClientEmblemProxy extends EmblemProxy {

    @Override
    public void initClient() {
        EmblemRenderHelper.instance.init();
        EmblemPackageManager.instance.init();
        EmblemPackageManager.instance.loadEmblems();

        if (ItemEmblem.item != null)
            MinecraftForgeClient.registerItemRenderer(ItemEmblem.item, new RenderEmblemItem());
    }

}
