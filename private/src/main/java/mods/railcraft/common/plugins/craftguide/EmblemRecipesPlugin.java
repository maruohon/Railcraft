package mods.railcraft.common.plugins.craftguide;

import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.post.ItemPost;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.emblems.EmblemManager;
import mods.railcraft.common.emblems.ItemEmblem;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.modules.ModuleManager.Module;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EmblemRecipesPlugin implements RecipeProvider {

    private final Slot[] slots = new Slot[10];

    public EmblemRecipesPlugin() {
        slots[0] = new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                slots[1 + x + y * 3] = new ItemSlot(3 + x * 18, 3 + y * 18, 16, 16, true).setSlotType(SlotType.INPUT_SLOT);
            }
        }
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        RecipeTemplate template = generator.createRecipeTemplate(slots, null, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);

        // Emblem
        if (Module.EMBLEM.isEnabled()) {
            // Emblem Post
            if (EnumPost.EMBLEM.isEnabled()) {
                ItemStack[] stacks = new ItemStack[10];
                stacks[0] = EnumPost.EMBLEM.getItem();
                ItemPost.setEmblem(stacks[0], EmblemManager.getIdentifierFromCode("Stone Age Miner"));
                stacks[1] = EnumPost.METAL.getItem();
                stacks[2] = ItemEmblem.getEmblem(EmblemManager.getIdentifierFromCode("Stone Age Miner"));
                generator.addRecipe(template, stacks);
            }

            // Emblem Loco
            if (EnumCart.LOCO_STEAM_SOLID.isEnabled()) {
                ItemStack[] stacks = new ItemStack[10];
                stacks[0] = EnumCart.LOCO_STEAM_SOLID.getCartItem();
                ItemLocomotive.setEmblem(stacks[0], EmblemManager.getIdentifierFromCode("Stone Age Miner"));
                stacks[1] = EnumCart.LOCO_STEAM_SOLID.getCartItem();
                stacks[2] = ItemEmblem.getEmblem(EmblemManager.getIdentifierFromCode("Stone Age Miner"));
                generator.addRecipe(template, stacks);
            }
        }
    }

}
