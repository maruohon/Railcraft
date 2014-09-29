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
import mods.railcraft.client.gui.GuiContainerRailcraft;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemBankSlotWidget extends EmblemSlotWidget {

    private final EmblemBankWidget bank;
    public final int index;

    EmblemBankSlotWidget(EmblemBankWidget bank, int index, int x, int y, int u, int v) {
        super(x, y, u, v);
        this.bank = bank;
        this.index = index;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            bank.currentSelection = getEmblem();
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        super.draw(gui, guiX, guiY, mouseX, mouseY);
        if (!bank.currentSelection.equals("") && bank.currentSelection.equals(getEmblem()))
            gui.drawTexturedModalRect(guiX + x - 2, guiY + y - 2, u, v, w + 4, h + 4);
    }

    @Override
    protected String getEmblem() {
        return bank.getEmblem(index);
    }

}
