/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import java.util.List;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemEmblemDesign extends ItemEmblemBase {

    public static ItemEmblemDesign item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.emblem.design";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemEmblemDesign();
                item.setUnlocalizedName(tag);

                RailcraftRegistry.register(item);
            }
        }
    }

    public static ItemStack getEmblem(String identifier) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setString("emblem", identifier);
        return stack;
    }

    public ItemEmblemDesign() {
        super();
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (EmblemManager.playerHasEmblem(player, EmblemToolsServer.getEmblemIdentifier(stack)))
            return stack;
        // Unlock and send packet to client to tell it to print unlock message
        return stack;
    }

}
