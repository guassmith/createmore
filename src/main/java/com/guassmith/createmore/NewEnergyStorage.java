package com.guassmith.createmore;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewEnergyStorage extends EnergyStorage {

    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this);

    public NewEnergyStorage(int capacity) {
        super(capacity);
    }

    public NewEnergyStorage(int capacity, int inMax, int outMax) {
        super(capacity, inMax, outMax);
    }

    public void setEnergy(int e) {
        this.energy = e;
    }

    public void generateEnergy(int amount) {
        energy += amount;
        if (energy > capacity) {
            energy = capacity;
        }
    }

    public void outPutEnergy(int amount, World world, BlockPos blockPos, Direction side) {
        TileEntity te = world.getTileEntity(blockPos.offset(side));
        if (te == null) {
            return;
        }
        LazyOptional<IEnergyStorage> neighbourEnergy = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
        neighbourEnergy.ifPresent(energy -> {
            int amountUsed = energy.receiveEnergy(extractEnergy(amount, true), false);
            extractEnergy(amountUsed, false);
        });
    }

    public LazyOptional<IEnergyStorage> getOptional() {
        return optional;
    }

    public void invalidateOptional() {
        optional.invalidate();
    }
}
