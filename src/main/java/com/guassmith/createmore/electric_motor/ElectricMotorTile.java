package com.guassmith.createmore.electric_motor;

import com.guassmith.createmore.Config;
import com.guassmith.createmore.CreateMore;
import com.guassmith.createmore.NewEnergyStorage;
import com.guassmith.createmore.smart_motor.SmartMotorTile;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ElectricMotorTile extends GeneratingKineticTileEntity {

    protected final NewEnergyStorage energy = new NewEnergyStorage(Config.ELECTRIC_MOTOR.energyCapacity.get());
    protected boolean powered = false;
    protected boolean enabled = true;
    protected ScrollValueBehaviour generatedSpeed;

    public ElectricMotorTile(TileEntityType<? extends ElectricMotorTile> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
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
        this.generatedSpeed.withCallback(i -> updateGeneratedRotation());
        this.generatedSpeed.withStepFunction(CreativeMotorTileEntity::step);
        behaviours.add(this.generatedSpeed);
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
        if(cap == CapabilityEnergy.ENERGY) {
            return energy.getOptional().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void write(CompoundNBT compound, boolean clientPacket) {
        compound.putBoolean("powered", powered);
        compound.putBoolean("enabled", enabled);
        compound.putInt("energyAmount", energy.getEnergyStored());
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        powered = compound.getBoolean("powered");
        enabled = compound.getBoolean("enabled");
        energy.setEnergy(compound.getInt("energyAmount"));
        super.fromTag(state, compound, clientPacket);
    }

    @Override
    public boolean addToTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
        boolean superResult = super.addToTooltip(tooltip, isPlayerSneaking);
        if (!isPlayerSneaking) { return superResult; }
        if (!powered) {
            tooltip.add(componentSpacing.copyRaw().append(
                new TranslationTextComponent(CreateMore.MODID+".gui.no_power"))
                .mergeStyle(TextFormatting.GOLD)
            );

            ITextComponent hint = new TranslationTextComponent(CreateMore.MODID+".gui.no_power.hint");
            List<ITextComponent> cutString = TooltipHelper.cutTextComponent(hint, TextFormatting.GRAY, TextFormatting.WHITE);
            for (ITextComponent iTextComponent : cutString) {
                tooltip.add(componentSpacing.copyRaw().append(iTextComponent));
            }
            return true;
        }
        if (!enabled && !(this instanceof SmartMotorTile)) {
            tooltip.add(componentSpacing.copyRaw().append(
                    new TranslationTextComponent(CreateMore.MODID+".gui.turned_off"))
                    .mergeStyle(TextFormatting.GOLD)
            );

            ITextComponent hint = new TranslationTextComponent(CreateMore.MODID+".gui.turned_off.hint");
            List<ITextComponent> cutString = TooltipHelper.cutTextComponent(hint, TextFormatting.GRAY, TextFormatting.WHITE);
            for (ITextComponent iTextComponent : cutString) {
                tooltip.add(componentSpacing.copyRaw().append(iTextComponent));
            }
            return true;
        }
        return superResult;
    }

    @Override
    public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
        boolean superResult = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (isPlayerSneaking) {
            tooltip.add(componentSpacing.copyRaw().append(
                new TranslationTextComponent(CreateMore.MODID+".gui.energy_stored")
                .mergeStyle(TextFormatting.GRAY))
            );
            tooltip.add(componentSpacing.copyRaw().append(
                new StringTextComponent(" ").append(CreateMore.siFormatter(energy.getEnergyStored(),"gui.fe_units"))
                .mergeStyle(TextFormatting.AQUA))
            );
            tooltip.add(componentSpacing.copyRaw().append(
                new TranslationTextComponent(CreateMore.MODID+".gui.energy_used")
                .mergeStyle(TextFormatting.GRAY))
            );
            tooltip.add(componentSpacing.copyRaw().append(
                new StringTextComponent(" ").append(CreateMore.siFormatter(
                Math.abs(generatedSpeed.getValue()) * Config.ELECTRIC_MOTOR.energyUsage.get(), "gui.fe_tick"))
                .mergeStyle(TextFormatting.AQUA)
                .append(Lang.translate("gui.goggles.at_current_speed")
                .mergeStyle(TextFormatting.DARK_GRAY)))
            );
            return true;
        }
        return superResult;
    }

    @Override
    public void tick() {
        super.tick();
        assert world != null;
        if(!world.isRemote() && enabled) {
            boolean poweredBefore = powered;
            powered = false;
            int powerUsed = Math.abs(generatedSpeed.getValue()) * Config.ELECTRIC_MOTOR.energyUsage.get();
            powerUsed = poweredBefore ? powerUsed : powerUsed*4;
            if (powerUsed > 0 && energy.getEnergyStored() >= powerUsed) {
                powered = true;
                if(!isOverStressed()) {
                    energy.extractEnergy(powerUsed, false);
                    markDirty();
                }
            }

            if (poweredBefore != powered) {
                updateGeneratedRotation();
                markDirty();
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if(powered && enabled) {
            return convertToDirection(generatedSpeed.getValue(), getBlockState().get(ElectricMotor.FACING));
        }
        return 0;
    }

    @Override
    protected void invalidateCaps() {
        energy.invalidateOptional();
        super.invalidateCaps();
    }
}
