package mods.railcraft.common.gui.containers;

import buildcraft.api.power.PowerHandler.PowerReceiver;
import mods.railcraft.common.blocks.machine.alpha.TileEngravingBench;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.widgets.EmblemBankWidget;
import net.minecraft.inventory.ICrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotPassThrough;

public class ContainerEngravingBench extends RailcraftContainer {

    private final TileEngravingBench tile;
    private int lastEnergy, lastProgress;
    public EmblemBankWidget emblemBank;

    public ContainerEngravingBench(final InventoryPlayer inventoryplayer, final TileEngravingBench tile) {
        super(tile);
        this.tile = tile;

        addWidget(new IndicatorWidget(tile.getEnergyIndicator(), 157, 50, 176, 12, 6, 48));

        addWidget(emblemBank = new EmblemBankWidget(25, 25, tile.currentEmblem));

        addSlot(new SlotPassThrough(tile, 0, 35, 66));
        addSlot(new SlotOutput(tile, 1, 125, 66));

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 133 + i1 * 18));
            }
        }

        for (int j1 = 0; j1 < 9; j1++) {
            addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 191));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        PowerReceiver provider = tile.getPowerReceiver(null);
        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (lastProgress != tile.getProgress())
                icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());

            if (provider != null && lastEnergy != provider.getEnergyStored())
                icrafting.sendProgressBarUpdate(this, 1, (int) provider.getEnergyStored());
        }

        lastProgress = tile.getProgress();

        if (provider != null)
            lastEnergy = (int) provider.getEnergyStored();
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());
        PowerReceiver provider = tile.getPowerReceiver(null);
        if (provider != null)
            icrafting.sendProgressBarUpdate(this, 1, (int) provider.getEnergyStored());
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.setProgress(data);
                break;
            case 1:
                tile.guiEnergy = data;
                break;
        }
    }

}
