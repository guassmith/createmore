package com.guassmith.createmore.steam_engine;

import com.guassmith.createmore.CreateMore;
import com.guassmith.createmore.mixin.MixinFlywheelTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.components.flywheel.FlywheelTileEntity;
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
        BlockState blockState = te.getBlockState();
        Direction dir = blockState.get(HORIZONTAL_FACING);

        FlywheelTileEntity wte = te.getWheel();
        //LOGGER.info("wte " + wte);
        if(wte == null) { return; }

        MixinFlywheelTileEntity mwte = (MixinFlywheelTileEntity) wte;
        float speed = mwte.getVisualSpeed().get(partialTicks) * 3 / 10f;
        float angle = mwte.getAngle() + speed * partialTicks;

        BlockPos pos = BlockPos.ZERO.offset(dir.getOpposite());
        transformConnector(
        rotateToFacing(CreateMore.PISTON.renderOn(te.getBlockState()).translate(pos.getX()*0.5, 0, pos.getZ()*0.5), dir.getOpposite())
        .light(WorldRenderer.getPackedLightmapCoords(te.getWorld(), te.getBlockState(), te.getPos()))
        ,
                true,
                false,
                angle,
                false
        ).renderInto(ms, buffer.getBuffer(RenderType.getSolid()));
    }

    protected SuperByteBuffer transformConnector(SuperByteBuffer buffer, boolean upper, boolean rotating, float angle,
                                                 boolean flip) {

        float shift = upper ? 1 / 4f : -1 / 8f;
        float offset = upper ? 1 / 4f : 1 / 4f;
        angle += 180;
        angle = angle % 360;
        float radians = (float) (angle / 180 * Math.PI);
        float shifting = MathHelper.sin(radians) * shift + offset;

        float maxAngle = upper ? -5 : -15;
        float minAngle = upper ? -45 : 5;
        float barAngle = 0;

        if (rotating)
            barAngle = MathHelper.lerp((MathHelper.sin((float) (radians + Math.PI / 2)) + 1) / 2, minAngle, maxAngle);

        float pivotX = (upper ? 8f : 3f) / 16;
        float pivotY = (upper ? 8f : 2f) / 16;
        float pivotZ = (upper ? 23f : 21.5f) / 16f;

        buffer.translate(pivotX, pivotY, pivotZ + shifting);
        if (rotating)
            buffer.rotate(Direction.EAST, AngleHelper.rad(barAngle));
        buffer.translate(-pivotX, -pivotY, -pivotZ);

        if (flip && !upper)
            buffer.translate(9 / 16f, 0, 0);

        return buffer;
    }

    protected SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        buffer.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing)));
        return buffer;
    }
}
