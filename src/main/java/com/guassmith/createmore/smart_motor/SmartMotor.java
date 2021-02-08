package com.guassmith.createmore.smart_motor;

import com.guassmith.createmore.ModTiles;
import com.guassmith.createmore.electric_motor.ElectricMotor;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.config.AllConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SmartMotor extends ElectricMotor implements ITE<SmartMotorTile> {

    public SmartMotor(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTiles.SMART_MOTOR_TILE.create();
    }

    @Override
    public ActionResultType onBlockActivated(
        BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit
    ) {

        if (!player.getHeldItem(handIn).isEmpty() && player.isSneaking()) {
            return ActionResultType.PASS;
        }

        withTileEntityDo(worldIn, pos, te -> te.setEnabled(!player.isSneaking()));
        return ActionResultType.SUCCESS;
    }

    @Override
    public Class<SmartMotorTile> getTileEntityClass() {
        return SmartMotorTile.class;
    }
}
