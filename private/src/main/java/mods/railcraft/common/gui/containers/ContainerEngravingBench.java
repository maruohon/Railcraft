package mods.railcraft.common.gui.containers;

import cofh.api.energy.EnergyStorage;
import mods.railcraft.common.blocks.machine.epsilon.TileEngravingBench;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotPassThrough;
import mods.railcraft.common.gui.widgets.EmblemBankWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerEngravingBench extends RailcraftContainer {
    private final TileEngravingBench tile;
    private final RFEnergyIndicator energyIndicator;
    public EmblemBankWidget emblemBank;
    private int lastEnergy, lastProgress;

    public ContainerEngravingBench(final InventoryPlayer inventoryplayer, final TileEngravingBench tile) {
        super(tile);
        this.tile = tile;

        energyIndicator = new RFEnergyIndicator(tile);
        addWidget(new IndicatorWidget(energyIndicator, 157, 50, 176, 12, 6, 48));

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
        EnergyStorage storage = tile.getEnergyStorage();
        for (Object crafter : crafters) {
            ICrafting icrafting = (ICrafting) crafter;
            if (lastProgress != tile.getProgress())
                icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());
            if (storage != null && lastEnergy != storage.getEnergyStored())
                icrafting.sendProgressBarUpdate(this, 1, storage.getEnergyStored());
        }

        lastProgress = tile.getProgress();

        if (storage != null)
            lastEnergy = storage.getEnergyStored();
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());
        EnergyStorage storage = tile.getEnergyStorage();
        if (storage != null)
            icrafting.sendProgressBarUpdate(this, 2, storage.getEnergyStored());
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.setProgress(data);
                break;
            case 1:
                energyIndicator.updateEnergy(data);
                break;
            case 2:
                energyIndicator.setEnergy(data);
                break;
        }
    }
}
