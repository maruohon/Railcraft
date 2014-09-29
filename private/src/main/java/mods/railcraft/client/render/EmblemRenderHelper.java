/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.emblems.EmblemPackageManager;
import mods.railcraft.client.emblems.EmblemTexture;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.client.emblems.IEmblemItemRenderer;
import mods.railcraft.common.emblems.EmblemToolsServer;
import mods.railcraft.common.emblems.ItemEmblem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemRenderHelper implements IEmblemItemRenderer {

    public static final EmblemRenderHelper instance;
    private static final ResourceLocation GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final RenderItem renderItem = new RenderItem();

    static {
        instance = new EmblemRenderHelper();
        EmblemToolsClient.renderer = instance;
    }

    public void init() {
    }

    @Override
    public void renderIn3D(String ident, boolean renderGlint) {
        renderIn3D(ItemEmblem.getEmblem(ident), renderGlint);
    }

    @Override
    public void renderIn3D(ItemStack stack, boolean renderGlint) {
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
                if (texture.getImage() != null)
                    net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, texture.getImage().getWidth(), texture.getImage().getHeight(), RenderTools.PIXEL);
            } else {
                IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
                net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), RenderTools.PIXEL);
            }

            if (renderGlint && stack.hasEffect(pass)) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                RenderManager.instance.renderEngine.bindTexture(GLINT_TEXTURE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f13 = 0.76F;
                GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f14 = 0.125F;
                GL11.glScalef(f14, f14, f14);
                float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f15, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, RenderTools.PIXEL);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f14, f14, f14);
                f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f15, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, RenderTools.PIXEL);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }

        GL11.glPopMatrix();
    }

}
