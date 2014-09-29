package mods.railcraft.client.gui;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mods.railcraft.client.gui.GuiTools;
import mods.railcraft.client.gui.TileGui;
import mods.railcraft.client.gui.buttons.GuiButtonSmall;
import mods.railcraft.common.blocks.machine.alpha.TileEngravingBench;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.containers.ContainerEngravingBenchUnlock;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

public class GuiEngravingBenchUnlock extends TileGui {

    private static final String URL_BLOG = "http://railcraft.info/";
    private static final String URL_TWITTER = "https://twitter.com/CovertJaguar";
    private static final String URL_YOUTUBE = "https://www.youtube.com/user/CovertJaguar";
    private final TileEngravingBench tile;
    private final EntityPlayer player;
    private boolean pause = false;
    private String code = "";
    private int updateCount;

    public GuiEngravingBenchUnlock(InventoryPlayer inv, TileEngravingBench tile) {
        super(tile, new ContainerEngravingBenchUnlock(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_engraving_unlock.png");
        this.tile = tile;
        this.player = inv.player;
        ySize = 215;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiButtonSmall(0, w + 16, h + 77, 50, LocalizationPlugin.translate("railcraft.gui.engrave.unlock")));
        buttonList.add(new GuiButtonSmall(1, w + xSize - 58, h + ySize - 23, 50, StatCollector.translateToLocal("gui.back")));

        buttonList.add(new GuiButtonSmall(2, w + 24, h + 146, 128, "The Railcraft Blog"));

        List<GuiButtonSmall> buttons = new ArrayList<GuiButtonSmall>();
        buttons.add(new GuiButtonSmall(3, 0, h + 162, 80, "CJ's Twitter"));
        buttons.add(new GuiButtonSmall(4, 0, h + 162, 80, "CJ's Youtube"));
        GuiTools.newButtonRow(buttonList, w + 7, 2, buttons);
    }

    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_BACK:
                if (code.length() > 0)
                    code = code.substring(0, code.length() - 1);
                return;
            case Keyboard.KEY_RETURN:
                attemptUnlock();
                return;
            case Keyboard.KEY_ESCAPE:
            case Keyboard.KEY_TAB:
                sendUpdateToTile(TileEngravingBench.GuiPacketType.OPEN_NORMAL);
                return;
            default:
                if (code.length() < 24 && ChatAllowedCharacters.isAllowedCharacter(c))
                    code = code + Character.toString(c);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        pause = false;
        switch (button.id) {
            case 0:
                attemptUnlock();
                break;
            case 1:
                sendUpdateToTile(TileEngravingBench.GuiPacketType.OPEN_NORMAL);
                break;
            case 2:
                openLink(URL_BLOG);
                break;
            case 3:
                openLink(URL_TWITTER);
                break;
            case 4:
                openLink(URL_YOUTUBE);
                break;
        }
    }

    private void attemptUnlock() {
        if (code.length() > 0)
            sendUpdateToTile(TileEngravingBench.GuiPacketType.UNLOCK_EMBLEM);
    }

    private void openLink(String url) {
        pause = true;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
        }
    }

    public void sendUpdateToTile(TileEngravingBench.GuiPacketType type) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeByte(type.ordinal());
            switch (type) {
                case UNLOCK_EMBLEM:
                    data.writeByte(container.windowId);
                    data.writeUTF(code.toLowerCase(Locale.ENGLISH));
            }
        } catch (IOException ex) {
        }
        PacketBuilder.instance().sendGuiReturnPacket(tile, bytes.toByteArray());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.engrave.entercode"), 28);

        String codeDisplay = code;
        if (updateCount / 6 % 2 == 0)
            codeDisplay = codeDisplay + "" + EnumChatFormatting.GRAY + "_";
        else
            codeDisplay = codeDisplay + "" + EnumChatFormatting.WHITE + "_";
        fontRendererObj.drawString(codeDisplay, 13, 50, 0xFFFFFF, false);

        String unlockMsg = ((ContainerEngravingBenchUnlock) container).unlockMsg;
        int msgColor = 0x404040;
        if (unlockMsg != null && !unlockMsg.equals("")) {
            if (unlockMsg.equals("railcraft.gui.engrave.unlock.fail"))
                msgColor = 0xFF0000;
            fontRendererObj.drawString(LocalizationPlugin.translate(unlockMsg), 72, 81, msgColor);
        }

        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.engrave.getmore1"), 114);
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.engrave.getmore2"), 130);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return pause;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateCount++;
    }

}
