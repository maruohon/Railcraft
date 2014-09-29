/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.post.ItemPost;
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
public class EmblemPostEmblemRecipe implements IRecipe {

    private ItemStack postTemplate;
    private ItemStack emblemPostTemplate;

    public EmblemPostEmblemRecipe() {
    }

    public ItemStack getEmblemPost() {
        if (emblemPostTemplate == null)
            emblemPostTemplate = EnumPost.EMBLEM.getItem();
        return emblemPostTemplate;
    }

    public ItemStack getPost() {
        if (postTemplate == null)
            postTemplate = EnumPost.METAL.getItem();
        return postTemplate;
    }

    private boolean isEmblem(ItemStack stack) {
        return ItemEmblem.item != null && stack.getItem() == ItemEmblem.item;
    }

    private boolean isPost(ItemStack stack) {
        if (InvTools.isStackEqualToBlock(stack, BlockPostMetal.post))
            return true;
        return InvTools.isItemEqual(stack, getPost(), true, false);
    }

    @Override
    public boolean matches(InventoryCrafting craftingGrid, World var2) {
        int numPost = 0, numEmblem = 0;

        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack stack = craftingGrid.getStackInSlot(slot);
            if (stack == null)
                continue;
            else if (isPost(stack))
                numPost++;
            else if (isEmblem(stack))
                numEmblem++;
            else
                return false;
        }
        return numPost == 1 && numEmblem == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGrid) {
        ItemStack post = null;
        ItemStack emblem = null;

        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack stack = craftingGrid.getStackInSlot(slot);
            if (stack == null)
                continue;
            else if (isPost(stack))
                post = stack;
            else if (isEmblem(stack))
                emblem = stack;
            else
                return null;
        }

        if (post == null)
            return null;

        ItemStack result = EnumPost.EMBLEM.getItem();
        ItemPost.setEmblem(result, EmblemToolsServer.getEmblemIdentifier(emblem));
        if (InvTools.isStackEqualToBlock(post, BlockPostMetal.post))
            InvTools.setItemColor(result, EnumColor.fromId(post.getItemDamage()));
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return getEmblemPost();
    }

}
