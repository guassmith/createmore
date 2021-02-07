package com.guassmith.createmore.dynamo;

import com.guassmith.createmore.ModTiles;
import com.guassmith.createmore.ShapeBuilder;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.relays.encased.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dynamo extends AbstractEncasedShaftBlock implements IRotate, ITE<DynamoTile> {

    //sides joined to another dynamo
    protected static final BooleanProperty FRONT = BooleanProperty.create("joined_front");
    protected static final BooleanProperty BACK = BooleanProperty.create("joined_back");

    private static final Logger LOGGER = LogManager.getLogger();

    public static final VoxelShaper NONE_JOINED_SHAPE = ShapeBuilder //none joined
            .shape(1,1,1,15,15,15)
            .erase(4,1,4,12,15,12)
            .add(0, 3, 0, 16, 13, 16)
            .forAxis();

    public static final VoxelShaper ONE_JOINED_SHAPE = ShapeBuilder //none joined
            .shape(1,1,1,15,15,15)
            .erase(4,1,4,12,15,12)
            .add(0, 3, 0, 16, 16, 16)
            .forDirectional();

    public Dynamo(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(FRONT, false).with(BACK, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if(!state.get(FRONT) && !state.get(BACK)) {
            return NONE_JOINED_SHAPE.get(state.get(AXIS));
        } else if(state.get(FRONT) && !state.get(BACK)) {
            return ONE_JOINED_SHAPE.get(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, state.get(AXIS)));
        } else if(!state.get(FRONT) && state.get(BACK)) {
            return ONE_JOINED_SHAPE.get(Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, state.get(AXIS)));
        } else {
            return VoxelShapes.fullCube();
        }
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool) {
        return tool == ToolType.PICKAXE;
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
        return ModTiles.DYNAMO_TILE.create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder.add(FRONT, BACK));
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        if(!worldIn.isRemote()) {
            withTileEntityDo(worldIn, pos, te -> te.updateConnections(pos, fromPos));
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return checkForJoin(stateIn, facing, facingState, currentPos, facingPos);
    }

    private BlockState checkForJoin(BlockState ourState, Direction dir, BlockState state, BlockPos pos, BlockPos fromPos) {
        if(dir.getAxis() != ourState.get(AXIS)) { return ourState; }

        if(! (state.getBlock() instanceof Dynamo) || (state.get(AXIS) != ourState.get(AXIS))) {
            return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                ourState.with(FRONT,false) : ourState.with(BACK, false);
        }
        if(dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            LOGGER.info("FRONT connects");
            return ourState.with(FRONT, true);
        } else {
            LOGGER.info("BACK connects");
            return ourState.with(BACK, true);
        }
    }

    public Class<DynamoTile> getTileEntityClass() {
        return DynamoTile.class;
    }

    public enum JoinedAt implements IStringSerializable {
        NONE("none"),
        FRONT("front"),
        BACK("back"),
        BOTH("both");

        private final String name;

        JoinedAt(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
        public String getString() {
            return this.name;
        }
    }
}
