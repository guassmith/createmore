package com.guassmith.createmore.dynamo;

import com.guassmith.createmore.Config;
import com.guassmith.createmore.CreateMore;
import com.guassmith.createmore.ModBlocks;
import com.guassmith.createmore.NewEnergyStorage;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamoTile extends KineticTileEntity {

    private static final Logger LOGGER = LogManager.getLogger();
    private final NewEnergyStorage energy = new NewEnergyStorage(1000000, 0, 1000000);
    private Set<Direction> connectedSides;

    public DynamoTile(TileEntityType<?> typeIn) {
        super(typeIn);
        connectedSides = new HashSet<>();
    }

    @Override
    public void write(CompoundNBT compound, boolean clientPacket) {
        compound.putInt("energyAmount", energy.getEnergyStored());
        compound.putIntArray("connectedSides", connectedSides.stream().mapToInt(Direction::getIndex).toArray());
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        energy.setEnergy(compound.getInt("energyAmount"));
        int[] hm = compound.getIntArray("connectedSides");
        connectedSides = Arrays.stream(hm).mapToObj(Direction::byIndex).collect(Collectors.toSet());
        super.fromTag(state, compound, clientPacket);
    }

    private boolean sideOutputsEnergy(Direction side) {
        return side.getAxis() != ModBlocks.DYNAMO.get().getRotationAxis(getBlockState());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((cap == CapabilityEnergy.ENERGY) && (side == null || sideOutputsEnergy(side))) {
            return energy.getOptional().cast();
        }
        return super.getCapability(cap, side);
    }

    public void updateConnections(BlockPos pos, BlockPos neighbour) {
        World world = getWorld();
        if (world == null) { return; }
        if (world.isRemote()) { return; }
        Direction dir = CreateMore.getSide(pos, neighbour);
        if (dir == null || !sideOutputsEnergy(dir)) { return; }
        TileEntity te = world.getTileEntity(neighbour);
        if (te == null ) {
            connectedSides.remove(dir);
            return;
        }
        if(te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent()) {
            connectedSides.add(dir);
        } else {
            connectedSides.remove(dir);
        }
        markDirty();
    }

    @Override
    public void tick() {
        super.tick();

        if(Math.abs(getSpeed()) > 0) {
            energy.generateEnergy((int) Math.abs(getSpeed()) * Config.DYNAMO.energyProduction.get());
            markDirty();
        }
        World world = getWorld();
        if (world == null) { return; }
        connectedSides.forEach(dir -> energy.outPutEnergy(Config.DYNAMO.energyOutput.get(), world, getPos(), dir));
        if(!connectedSides.isEmpty()) { markDirty(); }
    }

    @Override
    public void remove() {
        energy.invalidateOptional();
        super.remove();
    }
}
