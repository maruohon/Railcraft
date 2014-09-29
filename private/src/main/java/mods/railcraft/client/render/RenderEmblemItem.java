package mods.railcraft.client.render;

import java.util.Random;
import mods.railcraft.client.emblems.EmblemPackageManager;
import mods.railcraft.client.emblems.EmblemTexture;
import mods.railcraft.common.emblems.EmblemToolsServer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.ItemRenderer.renderItemIn2D;
import static net.minecraft.client.renderer.entity.RenderItem.renderInFrame;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderEmblemItem implements IItemRenderer {

    private static final ResourceLocation GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final RenderItem renderItem = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.ENTITY_BOBBING;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (type == ItemRenderType.INVENTORY)
            render(ItemRenderType.INVENTORY, stack);
        else if (type == ItemRenderType.ENTITY)
            if (RenderManager.instance.options.fancyGraphics)
                renderAsEntity(stack, (EntityItem) data[1]);
            else
                renderAsEntityFlat(stack, (EntityItem) data[1]);
        else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            renderEquiped(stack, (EntityLivingBase) data[1]);
            GL11.glPopMatrix();
        }
    }

    private void renderEquiped(ItemStack stack, EntityLivingBase entity) {
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;

        int meta = stack.getItemDamage();
        for (int pass = 0; pass < stack.getItem().getRenderPasses(meta); ++pass) {
            int color = stack.getItem().getColorFromItemStack(stack, pass);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;

            if (renderItem.renderWithColor)
                GL11.glColor4f(c1, c2, c3, 1.0F);

            String emblemIdentifier = EmblemToolsServer.getEmblemIdentifier(stack);
            if (emblemIdentifier != null && !emblemIdentifier.equals("")) {
                EmblemTexture texture = EmblemPackageManager.instance.getEmblemTexture(emblemIdentifier);
                Minecraft.getMinecraft().renderEngine.bindTexture(texture.getLocation());
                ItemRenderer.renderItemIn2D(tessellator, 1, 0, 0, 1, texture.getImage().getWidth(), texture.getImage().getHeight(), RenderTools.PIXEL);
            } else {
                IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
                ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), RenderTools.PIXEL);
            }

            if (stack.hasEffect(pass)) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                Minecraft.getMinecraft().renderEngine.bindTexture(GLINT_TEXTURE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }

        GL11.glPopMatrix();
    }

    private void renderAsEntity(ItemStack stack, EntityItem entity) {
        GL11.glPushMatrix();
        byte iterations = 1;
        if (stack.stackSize > 1) iterations = 2;
        if (stack.stackSize > 15) iterations = 3;
        if (stack.stackSize > 31) iterations = 4;

        Random rand = new Random(187L);

        float offsetZ = 0.0625F + 0.021875F;

        GL11.glRotatef((((float) entity.age + 1.0F) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.25F, -(offsetZ * (float) iterations / 2.0F));

        for (int count = 0; count < iterations; ++count) {
            if (count > 0) {
                float offsetX = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                float offsetY = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                float z = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                GL11.glTranslatef(offsetX, offsetY, offsetZ);
            } else
                GL11.glTranslatef(0f, 0f, offsetZ);

            EmblemRenderHelper.instance.renderIn3D(stack, false);
        }
        GL11.glPopMatrix();
    }

    private void renderAsEntityFlat(ItemStack stack, EntityItem entity) {
        GL11.glPushMatrix();
        byte iterations = 1;
        if (stack.stackSize > 1) iterations = 2;
        if (stack.stackSize > 15) iterations = 3;
        if (stack.stackSize > 31) iterations = 4;

        Random rand = new Random(187L);

        for (int ii = 0; ii < iterations; ++ii) {
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            if (!renderInFrame)
                GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

            if (ii > 0) {
                float var12 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                float var13 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                float var14 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                GL11.glTranslatef(var12, var13, var14);
            }

            GL11.glTranslatef(0.5f, 0.8f, 0);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(1f / 16f, 1f / 16f, 1);

            render(ItemRenderType.ENTITY, stack);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    private void render(ItemRenderType type, ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        String emblemIdentifier = EmblemToolsServer.getEmblemIdentifier(stack);
        if (emblemIdentifier != null && !emblemIdentifier.equals(""))
            renderTextureObject(0, 0, EmblemPackageManager.instance.getEmblemTexture(emblemIdentifier).getLocation(), 16, 16);
        else {
            IIcon icon = stack.getItem().getIconFromDamageForRenderPass(0, 0);
            renderItem.renderIcon(0, 0, icon, 16, 16);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void renderTextureObject(int x, int y, ResourceLocation texture, int width, int height) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, renderItem.zLevel, 0, 1);
        tessellator.addVertexWithUV(x + width, y + height, renderItem.zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, renderItem.zLevel, 1, 0);
        tessellator.addVertexWithUV(x + 0, y + 0, renderItem.zLevel, 0, 0);
        tessellator.draw();
    }

}
