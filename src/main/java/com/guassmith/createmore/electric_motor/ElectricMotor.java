package com.guassmith.createmore.electric_motor;

import com.guassmith.createmore.ShapeBuilder;
import com.guassmith.createmore.ModTiles;
import com.simibubi.create.content.contraptions.base.*;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class ElectricMotor extends DirectionalKineticBlock{

    public static final VoxelShaper SHAPE = ShapeBuilder
            .shape(0, 2, 0, 16, 13, 16)
            .add(4, 0, 4, 12, 2, 12)
            .add(5, 13, 5, 11, 14, 11).forDirectional();

    public ElectricMotor(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.get(FACING));
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool) {
        return tool == ToolType.PICKAXE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(FACING).getAxis();
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTiles.ELECTRIC_MOTOR_TILE.create();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isSneaking()) || preferred == null)
            return super.getStateForPlacement(context);
        return getDefaultState().with(FACING, preferred);
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }
}
