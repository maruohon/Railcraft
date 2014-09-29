/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemPackageManager;
import mods.railcraft.client.emblems.EmblemTexture;
import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemSlotWidget extends Widget {

    public String emblemIdentifier;
    private final ToolTip toolTip = new ToolTip(750);

    public EmblemSlotWidget(int x, int y, int u, int v) {
        super(x, y, u, v, 16, 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        String emblem = getEmblem();
        if (emblem != null && !emblem.equals("")) {
            EmblemTexture emblemTexture = EmblemPackageManager.instance.getEmblemTexture(emblem);
            gui.bindTexture(emblemTexture.getLocation());
            gui.drawTexture(guiX + x, guiY + y, 16, 16, 0, 0, 1, 1);
            gui.bindTexture(gui.texture);
        }

        if (isMouseOver(mouseX, mouseY))
            gui.drawGradientRect(guiX + x, guiY + y, guiX + x + 16, guiY + y + 16, -2130706433, -2130706433);
    }

    protected String getEmblem() {
        return emblemIdentifier;
    }

    @Override
    public ToolTip getToolTip() {
        toolTip.clear();
        String emblemIdent = getEmblem();
        if (emblemIdent == null || emblemIdent.equals(""))
            return null;
        Emblem emblem = EmblemPackageManager.instance.getEmblem(emblemIdent);
        if (emblem == null)
            return null;
        toolTip.add(new ToolTipLine(emblem.displayName, EnumRarity.values()[emblem.rarity].rarityColor));
        return toolTip;
    }

}
