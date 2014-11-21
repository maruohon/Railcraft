/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import mods.railcraft.common.carts.*;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.InventoryIterator;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotiveEmblemRecipe implements IRecipe {

    private final ItemStack locomotive;

    public LocomotiveEmblemRecipe(ItemStack locomotive) {
        this.locomotive = locomotive;
        InvTools.addNBTTag(locomotive, "gregfix", "get the hell off my lawn!");
    }

    private boolean isEmblem(ItemStack stack) {
        return ItemEmblem.item != null && stack.getItem() == ItemEmblem.item;
    }

    private boolean isLocomotive(ItemStack loco) {
        return InvTools.isItemEqualIgnoreNBT(this.locomotive, loco);
    }

    @Override
    public boolean matches(InventoryCrafting craftingGrid, World var2) {
        int numLocomotive = 0, numEmblem = 0;

        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack stack = craftingGrid.getStackInSlot(slot);
            if (stack == null)
                continue;
            if (isLocomotive(stack))
                numLocomotive++;
            else if (isEmblem(stack))
                numEmblem++;
            else
                return false;
        }
        return numLocomotive == 1 && numEmblem == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGrid) {
        ItemStack loco = null;
        ItemStack emblem = null;

        for (IInvSlot slot : InventoryIterator.getIterable(craftingGrid)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack == null)
                continue;
            if (isLocomotive(stack))
                loco = stack;
            else if (isEmblem(stack))
                emblem = stack;
            else
                return null;
        }

        if (loco == null || emblem == null)
            return null;

        ItemStack result = loco.copy();
        ItemLocomotive.setEmblem(result, EmblemToolsServer.getEmblemIdentifier(emblem));
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return locomotive;
    }

}
