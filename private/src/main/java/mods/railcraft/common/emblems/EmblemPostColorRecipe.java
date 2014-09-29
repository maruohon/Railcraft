/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.util.crafting.DyeHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EmblemPostColorRecipe implements IRecipe {

    private ItemStack postTemplate;

    public EmblemPostColorRecipe() {
    }

    public ItemStack getPost() {
        if (postTemplate == null)
            postTemplate = EnumPost.EMBLEM.getItem();
        return postTemplate;
    }

    private boolean isPost(ItemStack stack) {
        return InvTools.isItemEqual(stack, getPost(), true, false);
    }

    private boolean isDye(ItemStack stack) {
        return getDyeColor(stack) != null;
    }

    private EnumColor getDyeColor(ItemStack stack) {
        for (EnumColor color : EnumColor.VALUES) {
            if (InvTools.isItemEqual(stack, DyeHelper.getDyes().get(color)))
                return color;
        }
        return null;
    }

    @Override
    public boolean matches(InventoryCrafting craftingGrid, World var2) {
        int numPost = 0, numDye = 0;

        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack stack = craftingGrid.getStackInSlot(slot);
            if (stack == null)
                continue;
            else if (isDye(stack))
                numDye++;
            else if (isPost(stack))
                numPost++;
            else
                return false;
        }
        return numPost == 1 && numDye == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGrid) {
        ItemStack post = null;
        ItemStack dye = null;

        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack stack = craftingGrid.getStackInSlot(slot);
            if (stack == null)
                continue;
            else if (isPost(stack))
                post = stack;
            else if (isDye(stack))
                dye = stack;
            else
                return null;
        }

        if (post == null)
            return null;

        ItemStack result = post.copy();
        result = InvTools.setItemColor(result, getDyeColor(dye));
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return getPost();
    }

}
