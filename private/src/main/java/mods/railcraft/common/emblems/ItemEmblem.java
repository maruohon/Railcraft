/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemEmblem extends ItemEmblemBase {

    public static ItemEmblem item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.emblem";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemEmblem();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                RailcraftRegistry.register(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getEmblem(String identifier) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setString("emblem", identifier);
        return stack;
    }
    
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:emblem");
    }

}
