package com.guassmith.createmore.steam_engine;

import com.guassmith.createmore.CreateMore;
import com.guassmith.createmore.NewFluidStorage;
import com.guassmith.createmore.mixin.MixinFlywheelTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.content.contraptions.components.flywheel.FlywheelTileEntity;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class SteamEngineRenderer extends SafeTileEntityRenderer<SteamEngineTile> {

    Logger LOGGER = LogManager.getLogger();
    public SteamEngineRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void renderSafe(SteamEngineTile te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        if (te.getWorld() == null || te.getBlockState().get(SteamEngine.PART) != SteamEngine.SteamEnginePart.NE) { return; }

        BlockState blockState = te.getBlockState();
        Direction dir = blockState.get(HORIZONTAL_FACING);
        LOGGER.info("facing " + dir);

        FlywheelTileEntity wte = te.getWheel();
        float angle = 0;
        if(wte instanceof MixinFlywheelTileEntity) {
            MixinFlywheelTileEntity mwte = (MixinFlywheelTileEntity) wte;
            float speed = mwte.getVisualSpeed().get(partialTicks) * 3 / 10f;
            angle = mwte.getAngle() + speed * partialTicks;
        }

        BlockPos pos = BlockPos.ZERO.offset(dir.getOpposite());
        SuperByteBuffer renderBuffer =
            CreateMore.PISTON.renderOn(blockState).translate(pos.getX()*0.5, 0, pos.getZ()*0.5)
            .light(WorldRenderer.getPackedLightmapCoords(te.getWorld(), te.getBlockState(), te.getPos()));

        transformConnector(rotateToFacing(renderBuffer, dir.getOpposite()), angle)
        .renderInto(ms, buffer.getBuffer(RenderType.getSolid()));


        InterpolatedChasingValue fluidLevel = te.fluidLevel;
        if(fluidLevel == null) { return; }

        float yMin = 0.1251f;
        float level = fluidLevel.get(partialTicks) * (1-(yMin*2));
        if (level < 0.003) { return; }

        Vector2f min = new Vector2f(1.1251f, 0.438f);
        Vector2f max = new Vector2f(1.9371f, 1.624f);

        NewFluidStorage fluidStorage = te.fluid;
        if(fluidStorage == null) { return; }
        FluidStack fluidStack = fluidStorage.getFluid();
        if(fluidStack.isEmpty()) { return; }
        ms.push();
        ms.translate(0.5f, 0, 0.5f);
        ms.rotate(new Quaternion(Direction.UP.toVector3f(), AngleHelper.rad(AngleHelper.horizontalAngle(dir)), false));
        ms.translate(-0.5f, 0, -1.5625f);
        //FluidRenderer.renderTiledFluidBB(fluidStack, 0.438f, yMin, 1.1251f, 1.624f, level+yMin, 1.9371f, buffer, ms, light, false);
        FluidRenderer.renderTiledFluidBB(fluidStack, min.x, yMin, min.y, max.x, (level+yMin)-0.001f, max.y, buffer, ms, light, false);
        ms.pop();
    }

    protected SuperByteBuffer transformConnector(SuperByteBuffer buffer, float angle) {
        angle += 180;
        float radians = (float)Math.toRadians(angle);
        float shifting = MathHelper.sin(radians) * 0.25f + 0.25f;

        buffer.translate(0, 0, shifting);

        return buffer;
    }

    protected SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        buffer.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing)));
        return buffer;
    }
}
