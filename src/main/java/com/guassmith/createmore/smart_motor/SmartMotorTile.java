package com.guassmith.createmore.smart_motor;

import com.guassmith.createmore.electric_motor.ElectricMotorTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class SmartMotorTile extends ElectricMotorTile {

    private static final Logger LOGGER = LogManager.getLogger();
    SmartMotorPeripheral peripheralCap = new SmartMotorPeripheral(this);

    public SmartMotorTile(TileEntityType<? extends ElectricMotorTile> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public int setGeneratedSpeed(int newSpeed) {
        this.generatedSpeed.setValue(newSpeed);
        //updateGeneratedRotation();
        return this.generatedSpeed.getValue();
    }
    public int getGeneratedSpeedValue() {
        return this.generatedSpeed.getValue();
    }
    public int getEnergy() {
        return energy.getEnergyStored();
    }
    public boolean hasPower() {
        return powered;
    }
    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean e) {
        enabled = e;
        if(this.world != null && !this.world.isRemote()) {
            updateGeneratedRotation();
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CAPABILITY_PERIPHERAL) {
            return peripheralCap.getOptional().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        peripheralCap.invalidateOptional();
        super.invalidateCaps();
    }
}
