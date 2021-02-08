package com.guassmith.createmore.smart_motor;

import com.guassmith.createmore.ModTiles;
import com.guassmith.createmore.electric_motor.ElectricMotor;
import com.guassmith.createmore.electric_motor.ElectricMotorTile;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SmartMotor extends ElectricMotor {

    public SmartMotor(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTiles.SMART_MOTOR_TILE.create();
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.skipRedstone(state, worldIn, pos, blockIn, fromPos, isMoving);
    }
}
