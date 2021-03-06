package com.guassmith.createmore.electric_motor;

import com.guassmith.createmore.ShapeBuilder;
import com.guassmith.createmore.ModTiles;
import com.simibubi.create.content.contraptions.base.*;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class ElectricMotor extends DirectionalKineticBlock implements ITE<ElectricMotorTile> {

    public static final VoxelShaper SHAPE = ShapeBuilder
        .shape(0, 2, 0, 16, 13, 16)
        .add(4, 0, 4, 12, 2, 12)
        .add(5, 13, 5, 11, 14, 11).forDirectional();

    public ElectricMotor(Properties properties) {
        super(properties);
    }

    @SuppressWarnings({"deprecation", "NullableProblems"})
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.get(FACING));
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool) {
        return tool == ToolType.PICKAXE;
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(FACING);
    }

    @SuppressWarnings({"deprecation", "NullableProblems"})
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isRemote) {
            withTileEntityDo(worldIn, pos, te -> te.setEnabled(!worldIn.isBlockPowered(pos)));
        }
    }

    @SuppressWarnings("deprecation")
    public void skipRedstone(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer().isSneaking()) || preferred == null) {
            return super.getStateForPlacement(context);
        }
        return getDefaultState().with(FACING, preferred);
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
    public Class<ElectricMotorTile> getTileEntityClass() {
        return ElectricMotorTile.class;
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }
}
