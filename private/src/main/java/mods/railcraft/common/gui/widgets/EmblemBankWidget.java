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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.emblems.EmblemManager;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.util.collections.RevolvingList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemBankWidget extends Widget {

    private static final int NUM_SLOTS = 7;
    private List<EmblemSlotWidget> slots = new ArrayList<EmblemSlotWidget>(7);
    private RevolvingList<List<String>> emblems = new RevolvingList<List<String>>();
    String currentSelection = "";

    public EmblemBankWidget(int x, int y, String currentEmblem) {
        super(x, y, 182, 12, 126, 18);
        for (int i = 0; i < 7; i++) {
            slots.add(new EmblemBankSlotWidget(this, i, x + 1 + i * 18, y + 1, u, v));
        }
        this.currentSelection = currentEmblem;
    }

    @Override
    public void addToContainer(RailcraftContainer container) {
        super.addToContainer(container);
        for (EmblemSlotWidget slot : slots) {
            container.addWidget(slot);
        }
    }

    public List<EmblemSlotWidget> getSlots() {
        return slots;
    }

    public String getEmblem(int index) {
        List<String> view = emblems.getCurrent();
        if (view == null || index >= view.size())
            return "";
        return view.get(index);
    }

    public String getSelectedEmblem() {
        return currentSelection;
    }

    public void shiftLeft() {
        emblems.rotateLeft();
    }

    public void shiftRight() {
        emblems.rotateRight();
    }

    @Override
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
    }

    @Override
    public void initWidget(ICrafting player) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(bytes);
            data.writeUTF(currentSelection);
            Set<String> unlockedEmblems = EmblemManager.getUnlockedEmblems((EntityPlayer) player);
            data.writeShort(unlockedEmblems.size());
            for (String s : unlockedEmblems) {
                data.writeUTF(s);
            }
            container.sendWidgetDataToClient(this, player, bytes.toByteArray());
        } catch (IOException ex) {
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientPacketData(DataInputStream data) throws IOException {
        currentSelection = data.readUTF();
        int count = data.readShort();
        int subIndex = 0;
        List<String> emblemView = new ArrayList<String>(7);
        List<String> currentView = emblemView;
        for (int i = 0; i < count; i++) {
            if (subIndex >= NUM_SLOTS) {
                subIndex = 0;
                emblems.add(emblemView);
                emblemView = new ArrayList<String>(7);
            }
            String identifier = data.readUTF();
            emblemView.add(identifier);
            if (currentSelection.equals(identifier))
                currentView = emblemView;
            subIndex++;
        }
        emblems.add(emblemView);
        emblems.setCurrent(currentView);
    }

}
