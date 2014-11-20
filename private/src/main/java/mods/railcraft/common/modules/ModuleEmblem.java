/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.security.cert.Certificate;
import mods.railcraft.client.gui.GuiEngravingBench;
import mods.railcraft.client.gui.GuiEngravingBenchUnlock;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.TileEngravingBench;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.emblems.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.containers.ContainerEngravingBench;
import mods.railcraft.common.gui.containers.ContainerEngravingBenchUnlock;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleEmblem extends RailcraftModule {

    @SidedProxy(clientSide = "mods.railcraft.client.emblems.ClientEmblemProxy", serverSide = "mods.railcraft.common.emblems.EmblemProxy")
    public static EmblemProxy proxy;

    @Override
    public void preInit() {
        if (Game.isObfuscated())
            try {
                Class core = Class.forName("mods.railcraft.common.core.Railcraft");
                if (core != null) {
                    Certificate[] cert = core.getProtectionDomain().getCodeSource().getCertificates();
                    if (cert == null || !CertificateHelper.getFingerprint(cert[0]).equals("a0c255ac501b2749537d5824bb0f0588bf0320fa")) {
                        Game.logErrorFingerprint("Railcraft");
//                        FMLCommonHandler.instance().exitJava(1, false);
                        throw new RuntimeException("Invalid Fingerprint");
                    }
                }
            } catch (ClassNotFoundException ex) {
            }

        EmblemManager.instance.init();

        RecipeSorter.register("railcraft:locomotive.emblem", LocomotiveEmblemRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("railcraft:emblem.post.color", EmblemPostColorRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("railcraft:emblem.post.emblem", EmblemPostEmblemRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public void initFirst() {
        ItemEmblem.registerItem();

        EnumMachineAlpha alpha = EnumMachineAlpha.ENGRAVING_BENCH;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem();

            CraftingPlugin.addShapedRecipe(stack,
                    "TPB",
                    "PCP",
                    "VPV",
                    'T', Items.diamond_pickaxe,
                    'B', Items.book,
                    'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                    'V', Blocks.piston,
                    'C', Blocks.crafting_table);
        }

//        printCode("Bucket");
//        printCode("Stone Age Miner");
//        printCode("Toy Sword");
//        printCode("Bone Dead");
//        printCode("All Aboard");
//        printCode("Sleeper");
//        printCode("It's a lie!");
//        printCode("Beauty");
//        printCode("Power Up");
//        printCode("ssssSS!");
//        printCode("Sticky Situation");
//        printCode("crazy star");
//        printCode("we are vip");
//        printCode("bugeaters anonymous");
//        printCode("steve's evil twin");
//        printCode("danger ahead");
//        printCode("beware the train");
//        printCode("a new era begins");
//        printCode("crossing your path");
//        printCode("break out the champagne");
//        printCode("toil and trouble");
//        printCode("my name is jack");
//        printCode("bloodsucker");
//        printCode("left, right? right!");
//        printCode("white as a ghast");
//        printCode("tells no tales");
//        printCode("are you ready for z-day?");
//        printCode("trick or treat");
//        printCode("orlando");
//        printCode("durant special");
//        printCode("bakken crude");
//        printCode("i have the power!");
//        printCode("turn right here");
//        printCode("no acorns here");
//        printCode("white meat or dark meat");
//        printCode("light as a feather");
//        printCode("its that season again");
//        printCode("every flake is unique");
//        printCode("big red ribbon");
//        printCode("just a little jingle");
//        printCode("welcoming christmas");
//        printCode("naughty or nice");
//        printCode("oh christmas tree");
//        printCode("first gift of christmas");
//        printCode("joy to the world");
//        printCode("wrong way");
//        printCode("out with a bang");
//        printCode("red octagon");
//        printCode("wild goose chase");
//        printCode("frontier lawman");
//        printCode("loneliest number");
//        printCode("as bad as one");
//        printCode("trouble comes in threes");
//        printCode("fantastic four");
//        printCode("screwless software");
//        printCode("high five");
//        printCode("thx1138");
//        printCode("overhauled!");
//        printCode("more nukes");
//        printCode("mcmodcom");
//        printCode("linear story");
//        printCode("mysterydump");
//        System.exit(0);
    }

    @Override
    public void postInit() {
        proxy.initClient();

        addLocomotiveEmblemRecipe(EnumCart.LOCO_STEAM_SOLID);
        addLocomotiveEmblemRecipe(EnumCart.LOCO_STEAM_MAGIC);
        addLocomotiveEmblemRecipe(EnumCart.LOCO_ELECTRIC);
        if (BlockPost.block != null) {
            CraftingPlugin.addRecipe(new EmblemPostColorRecipe());
            CraftingPlugin.addRecipe(new EmblemPostEmblemRecipe());
        }
    }

    private void addLocomotiveEmblemRecipe(EnumCart cart) {
        ItemStack locomotive = cart.getCartItem();
        if (locomotive != null) {
            IRecipe recipe = new LocomotiveEmblemRecipe(locomotive);
            CraftingPlugin.addRecipe(recipe);
        }
    }

    private void printCode(String code) {
        Game.log(Level.INFO, String.format("Emblem Code: %s - %s", code, EmblemManager.getIdentifierFromCode(code)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGuiScreen(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        switch (gui) {
            case ENGRAVING_BENCH:
                return new GuiEngravingBench(inv, (TileEngravingBench) obj);
            case ENGRAVING_BENCH_UNLOCK:
                return new GuiEngravingBenchUnlock(inv, (TileEngravingBench) obj);
        }
        return null;
    }

    @Override
    public Container getGuiContainer(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        switch (gui) {
            case ENGRAVING_BENCH:
                return new ContainerEngravingBench(inv, (TileEngravingBench) obj);
            case ENGRAVING_BENCH_UNLOCK:
                return new ContainerEngravingBenchUnlock(inv, (TileEngravingBench) obj);
        }
        return null;
    }

}
