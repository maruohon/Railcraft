/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ForestryPlugin {
    public static Boolean modLoaded = null;
    public static Item trackmanBackpackT1;
    public static Item trackmanBackpackT2;
    public static Item icemanBackpackT1;
    public static Item icemanBackpackT2;
    public static Item apothecariesBackpackT1;
    public static Item apothecariesBackpackT2;

    public static ItemStack getItem(String tag) {
        if (!isForestryInstalled())
            return null;
        Item item = GameRegistry.findItem("Forestry", tag);
        if (item == null)
            return null;
        return new ItemStack(item, 1);
    }

    public static void addBackpackItem(String pack, ItemStack stack) {
        if (stack == null)
            return;
        addBackpackItem(pack, stack.getItem(), stack.getItemDamage());
    }

    public static void addBackpackItem(String pack, Item item) {
        sendBackpackMessage(String.format("%s@%s:*", pack, GameRegistry.findUniqueIdentifierFor(item)));
    }

    public static void addBackpackItem(String pack, Item item, int damage) {
        sendBackpackMessage(String.format("%s@%s:%d", pack, GameRegistry.findUniqueIdentifierFor(item), damage));
    }

    public static void addBackpackItem(String pack, Block block) {
        sendBackpackMessage(String.format("%s@%s:*", pack, GameRegistry.findUniqueIdentifierFor(block)));
    }

    public static void addBackpackItem(String pack, Block block, int meta) {
        sendBackpackMessage(String.format("%s@%s:%d", pack, GameRegistry.findUniqueIdentifierFor(block), meta));
    }

    private static void sendBackpackMessage(String message) {
        if (message.contains("null"))
            throw new IllegalArgumentException("Attempting to register broken item with Forestry Backpack!");
//        Game.logDebug(Level.FINEST, "Sending IMC to Forestry add-backpack-items: {0}", message);
        FMLInterModComms.sendMessage("Forestry", "add-backpack-items", message);
    }

    public static void registerBackpacks() {
        try {
            if (BackpackManager.backpackInterface == null)
                return;

            String tag = "railcraft.backpack.trackman.t1";
            if (RailcraftConfig.isItemEnabled(tag)) {
                trackmanBackpackT1 = registerBackpack(TrackmanBackpack.getInstance(), EnumBackpackType.T1, tag);

                ItemStack output = new ItemStack(trackmanBackpackT1);
                addBackpackTooltip(output);
                CraftingPlugin.addShapedRecipe(output,
                        "X#X",
                        "VYV",
                        "X#X",
                        '#', Blocks.wool,
                        'V', new ItemStack(Blocks.rail),
                        'X', Items.string,
                        'Y', new ItemStack(Blocks.chest));
            }

            tag = "railcraft.backpack.trackman.t2";
            if (RailcraftConfig.isItemEnabled(tag)) {
                trackmanBackpackT2 = registerBackpack(TrackmanBackpack.getInstance(), EnumBackpackType.T2, tag);

                ItemStack silk = getItem("craftingMaterial");

                if (silk != null) {
                    silk.setItemDamage(3);

                    ItemStack output = new ItemStack(trackmanBackpackT2);
                    addBackpackTooltip(output);
                    RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), null, output, new Object[]{
                            "WXW",
                            "WTW",
                            "WWW",
                            'X', Items.diamond,
                            'W', silk,
                            'T', trackmanBackpackT1});
                }
            }

            tag = "railcraft.backpack.iceman.t1";
            if (RailcraftConfig.isItemEnabled(tag)) {
                icemanBackpackT1 = registerBackpack(IcemanBackpack.getInstance(), EnumBackpackType.T1, tag);

                ItemStack output = new ItemStack(icemanBackpackT1);
                addBackpackTooltip(output);
                CraftingPlugin.addShapedRecipe(output,
                        "X#X",
                        "VYV",
                        "X#X",
                        '#', Blocks.wool,
                        'V', new ItemStack(Blocks.snow),
                        'X', Items.string,
                        'Y', new ItemStack(Blocks.chest));
            }

            tag = "railcraft.backpack.iceman.t2";
            if (RailcraftConfig.isItemEnabled(tag)) {
                icemanBackpackT2 = registerBackpack(IcemanBackpack.getInstance(), EnumBackpackType.T2, tag);

                ItemStack silk = getItem("craftingMaterial");

                if (silk != null) {
                    silk.setItemDamage(3);

                    ItemStack output = new ItemStack(icemanBackpackT2);
                    addBackpackTooltip(output);
                    RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), null, output, new Object[]{
                            "WXW",
                            "WTW",
                            "WWW",
                            'X', Items.diamond,
                            'W', silk,
                            'T', icemanBackpackT1});
                }
            }

            if (icemanBackpackT1 != null || icemanBackpackT2 != null)
                FMLCommonHandler.instance().bus().register(new IceManTickHandler());

            tag = "railcraft.backpack.apothecary.t1";
            if (RailcraftConfig.isItemEnabled(tag)) {
                apothecariesBackpackT1 = registerBackpack(ApothecariesBackpack.getInstance(), EnumBackpackType.T1, tag);

                ItemStack output = new ItemStack(apothecariesBackpackT1);
                addBackpackTooltip(output);
//                if (!ThaumcraftPlugin.isModInstalled()) {
                CraftingPlugin.addShapedRecipe(output,
                        "X#X",
                        "VYV",
                        "X#X",
                        '#', Blocks.wool,
                        'V', new ItemStack(Items.potionitem, 1, 8197),
                        'X', Items.string,
                        'Y', new ItemStack(Blocks.chest));
                CraftingPlugin.addShapedRecipe(output,
                        "X#X",
                        "VYV",
                        "X#X",
                        '#', Blocks.wool,
                        'V', new ItemStack(Items.potionitem, 1, 8261),
                        'X', Items.string,
                        'Y', new ItemStack(Blocks.chest));
                CraftingPlugin.addShapedRecipe(output,
                        "X#X",
                        "VYV",
                        "X#X",
                        '#', Blocks.wool,
                        'V', new ItemStack(Items.potionitem, 1, 8229),
                        'X', Items.string,
                        'Y', new ItemStack(Blocks.chest));
//                } else
//                    ApothecariesBackpack.registerThaumcraftResearch();
            }

            tag = "railcraft.backpack.apothecary.t2";
            if (RailcraftConfig.isItemEnabled(tag)) {
                apothecariesBackpackT2 = registerBackpack(ApothecariesBackpack.getInstance(), EnumBackpackType.T2, tag);

                ItemStack silk = getItem("craftingMaterial");

                if (silk != null) {
                    silk.setItemDamage(3);

                    ItemStack output = new ItemStack(apothecariesBackpackT2);
                    addBackpackTooltip(output);
                    RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), null, output, new Object[]{
                            "WXW",
                            "WTW",
                            "WWW",
                            'X', Items.diamond,
                            'W', silk,
                            'T', apothecariesBackpackT1});
                }
            }
        } catch (Throwable error) {
            Game.logErrorAPI("Forestry", error, BackpackManager.class);
        }
    }

    private static Item registerBackpack(BaseBackpack backpack, EnumBackpackType type, String tag) {
        Item item = BackpackManager.backpackInterface.addBackpack(backpack, type).setCreativeTab(CreativePlugin.RAILCRAFT_TAB).setUnlocalizedName(tag);
        RailcraftRegistry.register(item);
        return item;
    }

    private static void addBackpackTooltip(ItemStack stack) {
        InvTools.addItemToolTip(stack, "\u00a77\u00a7o" + LocalizationPlugin.translate("item.railcraft.backpack.tip"));
    }

    public static void setupBackpackContents() {
        try {
            if (BackpackManager.backpackInterface == null)
                return;
            TrackmanBackpack.getInstance().setup();
            IcemanBackpack.getInstance().setup();
            ApothecariesBackpack.getInstance().setup();
        } catch (Throwable error) {
            Game.logErrorAPI("Forestry", error, BackpackManager.class);
        }
    }

    public static boolean isForestryInstalled() {
        if (modLoaded == null)
            modLoaded = Loader.isModLoaded("Forestry");
        return modLoaded;
    }

    public static void addCarpenterRecipe(String recipeTag, int packagingTime, FluidStack liquid, ItemStack box, ItemStack product, Object... materials) {
        try {
            if (RecipeManagers.carpenterManager != null && RailcraftConfig.getRecipeConfig("forestry.carpenter." + recipeTag))
                RecipeManagers.carpenterManager.addRecipe(packagingTime, liquid, null, product, materials);
        } catch (Throwable error) {
            Game.logErrorAPI("Forestry", error, RecipeManagers.class);
        }
    }
}
