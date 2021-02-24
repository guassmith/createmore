package com.guassmith.createmore.steam_engine;

import com.guassmith.createmore.ModTiles;
import com.guassmith.createmore.ShapeBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SteamEngine extends EngineBlock {

    public static final EnumProperty<SteamEnginePart> PART = EnumProperty.create("part", SteamEnginePart.class);



    public SteamEngine(Properties properties) {
        super(properties);
        setDefaultState(getStateContainer().getBaseState().with(PART, SteamEnginePart.NE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        /*switch(state.get(PART)) {
            case NE: return NE_SHAPE.get(state.get(HORIZONTAL_FACING).getOpposite());
            case NW: return NW_SHAPE.get(state.get(HORIZONTAL_FACING).getOpposite());
            case SE: return SE_SHAPE.get(state.get(HORIZONTAL_FACING).getOpposite());
            case SW: return SW_SHAPE.get(state.get(HORIZONTAL_FACING).getOpposite());
            case CHIMNEY: return CHIMNEY_SHAPE.get(state.get(HORIZONTAL_FACING).getOpposite());
        }*/
        //LOGGER.info("wtf " + state.get(PART).getShape());
        return state.get(PART).getShape().get(state.get(HORIZONTAL_FACING).getOpposite());
        //return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        //return true;
        return state.get(PART) == SteamEnginePart.NE || state.get(PART) == SteamEnginePart.SW;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader iBlockReader) {
        if(state.get(PART) == SteamEnginePart.NE || state.get(PART) == SteamEnginePart.SW) {
            return ModTiles.STEAM_ENGINE_TILE.create();
        }
        return null;
    }

    @Nullable
    @Override
    public AllBlockPartials getFrameModel() {
        return null;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    }

    @Override
    protected boolean isValidBaseBlock(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos) {
        return true;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        if(state.get(PART) != SteamEnginePart.NE) { return true; }
        boolean valid = true;
        for(SteamEnginePart part : SteamEnginePart.values()) {
            if(!worldIn.isAirBlock(pos.add(part.fromNE().rotate(dirToRot(state.get(HORIZONTAL_FACING)))))) {
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if(worldIn.isRemote()) { return; }
        for(SteamEnginePart part : SteamEnginePart.values()) {
            if(part == SteamEnginePart.NE) { continue; }
            worldIn.setBlockState(pos.add(part.fromNE().rotate(dirToRot(state.get(HORIZONTAL_FACING)))), state.with(PART, part));
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        if(worldIn.isRemote()) { return; }

        BlockPos nePos = pos.add(state.get(PART).toNE(state.get(HORIZONTAL_FACING)));
        if (state.get(PART) == SteamEnginePart.NE) {
            removeMultiblock(worldIn, pos, dirToRot(state.get(HORIZONTAL_FACING)));
        } else {
            worldIn.removeBlock(nePos, false);
        }
    }

    private void removeMultiblock(World world, BlockPos pos, Rotation rot) {
        for(SteamEnginePart part : SteamEnginePart.values()) {
            if(part == SteamEnginePart.NE) { continue; }
            BlockPos partPos = pos.add(part.fromNE().rotate(rot));
            if(!(world.getBlockState(partPos).getBlock() instanceof SteamEngine)) { continue; }
            world.removeBlock(pos.add(part.fromNE().rotate(rot)), false);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART);
        super.fillStateContainer(builder);
    }

    private static Rotation dirToRot(Direction d) {
        switch(d){
            case EAST: return Rotation.CLOCKWISE_180;
            case SOUTH: return Rotation.COUNTERCLOCKWISE_90;
            case WEST: return Rotation.NONE;
            default: return Rotation.CLOCKWISE_90;
        }
    }

    public enum SteamEnginePart implements IStringSerializable {
        NE("ne", InnerClass.NE_SHAPE, 0,0,0),
        NW("nw", InnerClass.NW_SHAPE, 0,0,1),
        SE("se", InnerClass.SE_SHAPE, 1,0,0),
        SW("sw", InnerClass.SW_SHAPE, 1,0,1),
        CHIMNEY("chimney", InnerClass.CHIMNEY_SHAPE, 0,1,0);

        private final String name;
        private final VoxelShaper shape;
        private final int x;
        private final int y;
        private final int z;

        SteamEnginePart(String name, VoxelShaper shape, int x, int y, int z) {
            this.name = name;
            LOGGER.info("shpae is " + shape);
            this.shape = shape;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String toString() {
            return name;
        }
        public @NotNull String getString() {
            return name;
        }
        public VoxelShaper getShape() {
            return shape;
        }

        public BlockPos fromNE() {
            return new BlockPos(x,y,z);
        }

        public BlockPos toNE() {
            return new BlockPos(-x,-y,-z);
        }

        public BlockPos toNE(Direction dir) {
            return toNE().rotate(dirToRot(dir));
        }

        private static class InnerClass {
            public static VoxelShaper NE_SHAPE = ShapeBuilder
                .shape(0, 5, 1, 16, 16, 16)
                .add(0, 1, 6, 1, 5, 10)
                .add(1,0,2,15,5,15)
                .add(4.5,2,0,11.5,9,2).forDirectional();

            public static VoxelShaper NW_SHAPE = ShapeBuilder
                .shape(16, 1, 6, 10, 5, 10)
                .add(2, 6, 0, 14, 16, 16)
                .add(0, 6, 2, 16, 16, 14)
                .erase(2,6,2,14,7,14)
                .add(6,1,6,10,7,10).forDirectional();

            public static VoxelShaper SE_SHAPE = ShapeBuilder
                .shape(1, 0, 2, 15, 8, 12)
                .add(0, 0, 12, 16, 8, 16).forDirectional();

            public static VoxelShaper SW_SHAPE = ShapeBuilder
                .shape(0, 0, 2, 16, 11, 14)
                .add(2, 0, 0, 14, 11, 16)
                .erase(2,11,2,14,10,14)
                .add(4,10,4,12,16,12).forDirectional();

            public static VoxelShaper CHIMNEY_SHAPE = ShapeBuilder
                .shape(4.5, 2, 2, 11.5, 9, 16).forDirectional();
        }
    }
}
