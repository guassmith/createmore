package com.guassmith.createmore.electric_motor;

import com.guassmith.createmore.Config;
import com.guassmith.createmore.NewEnergyStorage;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ElectricMotorTile extends GeneratingKineticTileEntity {

    private final NewEnergyStorage energy = new NewEnergyStorage(Config.ELECTRIC_MOTOR.energyCapacity.get());
    private boolean powered = false;
    protected ScrollValueBehaviour generatedSpeed;

    public ElectricMotorTile(TileEntityType<? extends ElectricMotorTile> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        Integer max = Config.ELECTRIC_MOTOR.maxSpeed.get();
        Integer min = Config.ELECTRIC_MOTOR.minSpeed.get();
        CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform((motor, side) ->
            motor.get(ElectricMotor.FACING) == side.getOpposite()
        );
        this.generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
        this.generatedSpeed.between(min, max);
        this.generatedSpeed.value = Config.ELECTRIC_MOTOR.defaultSpeed.get();
        this.generatedSpeed.scrollableValue = Config.ELECTRIC_MOTOR.defaultSpeed.get();
        this.generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
        this.generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
        this.generatedSpeed.withStepFunction(CreativeMotorTileEntity::step);
        behaviours.add(this.generatedSpeed);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityEnergy.ENERGY ? energy.getOptional().cast() : super.getCapability(cap, side);
    }

    @Override
    public void write(CompoundNBT compound, boolean clientPacket) {
        compound.putBoolean("powered", powered);
        compound.putInt("energyAmount", energy.getEnergyStored());
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        powered = compound.getBoolean("powered");
        energy.setEnergy(compound.getInt("energyAmount"));
        super.fromTag(state, compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        assert world != null;
        if(!world.isRemote()) {
            boolean activeBefore = powered;
            powered = false;
            int powerUsed = Math.abs(generatedSpeed.getValue()) * Config.ELECTRIC_MOTOR.energyUsage.get();
            if (powerUsed > 0 && energy.getEnergyStored() >= powerUsed) {
                powered = true;
                energy.extractEnergy(powerUsed, false);
                markDirty();
            }

            if (activeBefore != powered) {
                updateGeneratedRotation();
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return powered ? convertToDirection((float)this.generatedSpeed.getValue(), this.getBlockState().get(ElectricMotor.FACING)) : 0.0F;
    }

    @Override
    public void remove() {
        energy.invalidateOptional();
        super.remove();
    }
}
