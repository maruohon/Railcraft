package mods.railcraft.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.client.gui.GuiTools;
import mods.railcraft.client.gui.TileGui;
import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.gui.buttons.GuiButtonSmall;
import mods.railcraft.common.blocks.machine.alpha.TileEngravingBench;
import mods.railcraft.common.blocks.machine.alpha.TileEngravingBench.GuiPacketType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.containers.ContainerEngravingBench;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

public class GuiEngravingBench extends TileGui {

    private final TileEngravingBench tile;
    private final EntityPlayer player;

    public GuiEngravingBench(InventoryPlayer inventoryplayer, TileEngravingBench tile) {
        super(tile, new ContainerEngravingBench(inventoryplayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_engraving.png");
        this.tile = tile;
        this.player = inventoryplayer.player;
        ySize = 215;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiBetterButton(0, w + 12, h + 26, 10, StandardButtonTextureSets.LEFT_BUTTON, ""));
        buttonList.add(new GuiBetterButton(1, w + 154, h + 26, 10, StandardButtonTextureSets.RIGHT_BUTTON, ""));

        buttonList.add(new GuiButtonSmall(2, w + 61, h + 60, 54, LocalizationPlugin.translate("railcraft.gui.engrave")));
        buttonList.add(new GuiButtonSmall(3, w + 35, h + 100, 106, LocalizationPlugin.translate("railcraft.gui.engrave.openunlock")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        GuiPacketType packetType = GuiPacketType.NORMAL_RETURN;

        if (button.id == 0)
            ((ContainerEngravingBench) container).emblemBank.shiftLeft();
        if (button.id == 1)
            ((ContainerEngravingBench) container).emblemBank.shiftRight();
        if (button.id == 2)
            packetType = GuiPacketType.START_CRAFTING;
        if (button.id == 3)
            packetType = GuiPacketType.OPEN_UNLOCK;

        sendUpdateToTile(packetType);
    }

    public void sendUpdateToTile(GuiPacketType type) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeByte(type.ordinal());
            switch (type) {
                case NORMAL_RETURN:
                case START_CRAFTING:
                    data.writeUTF(((ContainerEngravingBench) container).emblemBank.getSelectedEmblem());
            }
        } catch (IOException ex) {
        }
        PacketBuilder.instance().sendGuiReturnPacket(tile, bytes.toByteArray());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        sendUpdateToTile(GuiPacketType.NORMAL_RETURN);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        if (tile.getProgress() > 0) {
            int progress = tile.getProgressScaled(23);
            drawTexturedModalRect(x + 76, y + 76, 176, 0, progress + 1, 12);
        }
    }

}
