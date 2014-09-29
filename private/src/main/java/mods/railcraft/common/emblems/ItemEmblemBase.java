/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemPackageManager;
import mods.railcraft.common.items.ItemRailcraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class ItemEmblemBase extends ItemRailcraft {


    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");
            if (emblemIdent == null) return;
            Emblem emblem = EmblemPackageManager.instance.getEmblemOrLoad(emblemIdent.func_150285_a_());
            if (emblem != null)
                info.add(EnumChatFormatting.GRAY + emblem.displayName);
        }
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        EnumRarity rarity = EnumRarity.common;
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");
            if (emblemIdent == null) return rarity;
            Emblem emblem = EmblemPackageManager.instance.getEmblemOrLoad(emblemIdent.func_150285_a_());
            if (emblem == null) return EnumRarity.common;
            rarity = EnumRarity.values()[emblem.rarity];
        }
        return rarity;
    }

    @SideOnly(value = Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        if (pass != 0) return false;
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");
            if (emblemIdent == null) return false;
            Emblem emblem = EmblemPackageManager.instance.getEmblemOrLoad(emblemIdent.func_150285_a_());
            return emblem != null && emblem.hasEffect;
        }
        return false;
    }

}
