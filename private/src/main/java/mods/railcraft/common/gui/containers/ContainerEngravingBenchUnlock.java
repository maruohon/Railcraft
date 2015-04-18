package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.epsilon.TileEngravingBench;
import mods.railcraft.common.gui.widgets.EmblemSlotWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEngravingBenchUnlock extends RailcraftContainer {
    public final EmblemSlotWidget emblemSlot;
    private final TileEngravingBench tile;
    public String unlockResult;
    public String unlockMsg;

    public ContainerEngravingBenchUnlock(final InventoryPlayer inventoryplayer, final TileEngravingBench tile) {
        super(tile);
        this.tile = tile;

        addWidget(emblemSlot = new EmblemSlotWidget(139, 77, 0, 0));
    }

    @Override
    public void updateString(byte id, String data) {
        if (id == 0) {
            unlockResult = data;
            if (unlockResult != null && !unlockResult.equals(""))
                emblemSlot.emblemIdentifier = unlockResult;
            else
                emblemSlot.emblemIdentifier = null;
        }
        if (id == 1) {
            unlockMsg = data;
        }
    }
}
