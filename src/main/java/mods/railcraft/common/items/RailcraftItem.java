/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.EnumSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItem {

    rail(ItemRail.class, "part.rail", Items.iron_ingot, Items.gold_ingot, "slabWood", "ingotSteel", "ingotSteel"),
    railbed(ItemRailbed.class, "part.railbed", "stickWood", "slabStone"),
    tie(ItemTie.class, "part.tie", "slabWood", Blocks.stone_slab),
    signalLamp(ItemSignalLamp.class, "part.signal.lamp", Blocks.redstone_lamp),
    rebar(ItemRebar.class, "part.rebar", Items.iron_ingot),
    plate(ItemPlate.class, "part.plate", Items.iron_ingot, "ingotSteel", "ingotTin"),
    gear(ItemGear.class, "part.gear", Items.gold_ingot, Blocks.iron_block, "blockSteel", "ingotTin"),
    circuit(ItemCircuit.class, "part.circuit", Items.comparator, Blocks.redstone_torch, Items.repeater),
    dust(ItemDust.class, "dust", "dustObsidian", "dustSulfur", "dustSaltpeter", "dustCharcoal");
    public static final RailcraftItem[] VALUES = values();
    private final Class<? extends ItemRailcraft> itemClass;
    private final String tag;
    private final Object[] altRecipeObjects;
    private ItemRailcraft item;

    RailcraftItem(Class<? extends ItemRailcraft> itemClass, String tag, Object... altRecipeObjects) {
        this.itemClass = itemClass;
        this.tag = tag;
        this.altRecipeObjects = altRecipeObjects;
    }

    public void registerItem() {
        if (item != null)
            return;

        if (isEnabled()) {
            try {
                item = itemClass.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException("Invalid Item Constructor");
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Invalid Item Constructor");
            }
            item.setUnlocalizedName("railcraft." + tag);
            RailcraftRegistry.register(item);
            item.initItem();
            item.defineRecipes();
        }
    }

    public boolean isItemEqual(ItemStack stack) {
        return stack != null && this.item == stack.getItem();
    }

    public boolean isItemEqual(Item i) {
        return i != null && this.item == i;
    }

    public ItemRailcraft item() {
        return item;
    }

    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    public ItemStack getStack() {
        return getStack(1, 0);
    }

    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    public ItemStack getStack(int qty, int meta) {
        registerItem();
        if (item == null)
            return null;
        return new ItemStack(item, qty, meta);
    }

    private void checkMetaObject(IItemMetaEnum meta) {
        if (meta == null || meta.getItemClass() != itemClass)
            throw new RuntimeException("Incorrect Item Meta object used.");
    }

    public ItemStack getStack(IItemMetaEnum meta) {
        return getStack(1, meta);
    }

    public ItemStack getStack(int qty, IItemMetaEnum meta) {
        checkMetaObject(meta);
        return getStack(qty, meta.ordinal());
    }

    public Object getRecipeObject() {
        registerItem();
        if (item != null)
            return item.getRecipeObject(null);
        Object obj = altRecipeObjects[0];
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    public Object getRecipeObject(IItemMetaEnum meta) {
        checkMetaObject(meta);
        registerItem();
        if (item != null)
            return item.getRecipeObject(meta);
        Object obj = altRecipeObjects[meta.ordinal()];
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    public boolean isEnabled() {
        return RailcraftConfig.isItemEnabled(tag);
    }

    public static void definePostRecipes() {
        for (RailcraftItem type : VALUES) {
            if (type.item != null)
                type.item.definePostRecipes();
        }
    }
}
