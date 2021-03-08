package com.guassmith.createmore.steam_engine;

import com.guassmith.createmore.ModBlocks;
import com.guassmith.createmore.NewFluidStorage;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.flywheel.FlywheelBlock;
import com.simibubi.create.content.contraptions.components.flywheel.FlywheelTileEntity;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineTileEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class SteamEngineTile extends EngineTileEntity {
    //todo update nearby pipe when placed
    //todo add smoke effects
    //todo add description and goggle tooltips
    Logger LOGGER = LogManager.getLogger();

    private static final int CAPACITY = 8000;
    private static final int STEAM_USAGE = 50;
    private static final int SPEED = 50;

    NewFluidStorage fluid;
    private boolean working;
    private FlywheelTileEntity cachedClientWheel;
    public InterpolatedChasingValue fluidLevel;

    public SteamEngineTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void initialize() {
        if(getBlockState().get(SteamEngine.PART) == SteamEngine.SteamEnginePart.NE) {
            if (fluid == null) {
                fluid = new NewFluidStorage(8000);
            }
            updateEngine();
            markDirty();
        }
        super.initialize();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (getBlockState().get(SteamEngine.PART) == SteamEngine.SteamEnginePart.SW &&
                    side == getBlockState().get(HORIZONTAL_FACING).getOpposite()) {
                TileEntity neTile = getNETile();
                if(neTile instanceof SteamEngineTile) {
                    LazyOptional<IFluidTank> optional = ((SteamEngineTile) neTile).getFluidOptional();
                    if (optional != null) {
                        return optional.cast();
                    }
                }
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void write(CompoundNBT compound, boolean clientPacket) {
        if(getBlockState().get(SteamEngine.PART) != SteamEngine.SteamEnginePart.NE) { return; }
        compound.putBoolean("working", working);
        if(fluid != null) { fluid.writeToNBT(compound); }
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        if(state.get(SteamEngine.PART) != SteamEngine.SteamEnginePart.NE) { return; }
        working = compound.getBoolean("working");
        if(fluid == null) {
            fluid = new NewFluidStorage(8000);
        }
        fluid.readFromNBT(compound);

        float fillAmount = (float)fluid.getFluidAmount() / (float)CAPACITY;
        if (fluidLevel == null) { fluidLevel = new InterpolatedChasingValue().start(fillAmount).withSpeed(1 / 2f); }
        LOGGER.info("fillAmount " + fillAmount);

        if(clientPacket) {
            if (fluidLevel == null) { fluidLevel = new InterpolatedChasingValue().start(fillAmount); }
            fluidLevel.target(fillAmount);
        }

        super.fromTag(state, compound, clientPacket);
    }

    @Override
    public void tick() {
        if (fluidLevel != null) { fluidLevel.tick(); }
        super.tick();
    }

    @Override
    public void lazyTick() {
        if(getWorld() == null) { return; }
        if(getBlockState().get(SteamEngine.PART) != SteamEngine.SteamEnginePart.NE || getWorld().isRemote()) { return; }
        boolean workingBefore = working;
        working = false;
        //LOGGER.info("fluid " + fluid.getFluid().getFluid().getRegistryName());
        //LOGGER.info(!getWorld().isRemote + " amount " + fluid.getFluidAmount());
        if(fluid.getFluidAmount() >= STEAM_USAGE) {
            working = true;
            fluid.drain(STEAM_USAGE, EXECUTE);
        }
        if(workingBefore != working) {
            markDirty();
        }
        updateEngine();
        sendData();

        super.lazyTick();
    }

    private void updateEngine() {
        appliedCapacity = working ? (float) AllConfigs.SERVER.kinetics.stressValues.getCapacityOf(ModBlocks.STEAM_ENGINE.get()) : 0;
        appliedSpeed = working ? SPEED : 0;
        refreshWheelSpeed();
    }

    public FlywheelTileEntity getWheel() {
        if(cachedClientWheel != null && !cachedClientWheel.isRemoved()) { return cachedClientWheel; }
        if(getWorld() == null) { return null; }
        Direction engineFacing = getBlockState().get(HORIZONTAL_FACING);
        BlockPos wheelPos = getPos().offset(engineFacing, 2);
        BlockState wheelState = getWorld().getBlockState(wheelPos);
        if(!(wheelState.getBlock() instanceof FlywheelBlock)) { return null; }
        Direction.Axis wheelAxis = wheelState.get(FlywheelBlock.HORIZONTAL_FACING).getAxis();
        if (wheelAxis != engineFacing.rotateY().getAxis()) { return null; }
        if (!FlywheelBlock.isConnected(wheelState) || FlywheelBlock.getConnection(wheelState) == engineFacing.getOpposite()) {
            TileEntity te = getWorld().getTileEntity(wheelPos);
            if (te instanceof FlywheelTileEntity && !te.isRemoved()) {
                cachedClientWheel = (FlywheelTileEntity) te;
                return cachedClientWheel;
            }
        }
        return null;
    }

    private TileEntity getNETile() {
        World world = getWorld();
        if(world == null) { return null; }
        BlockPos pos = getPos().add(getBlockState().get(SteamEngine.PART).toNE(getBlockState().get(HORIZONTAL_FACING)));
        return world.getTileEntity(pos);
    }

    public LazyOptional<IFluidTank> getFluidOptional() {
        if(fluid == null) { return null; }
        return fluid.getOptional();
    }

    @Override
    protected void invalidateCaps() {
        if(fluid != null) { fluid.invalidateOptional(); }
        super.invalidateCaps();
    }
}