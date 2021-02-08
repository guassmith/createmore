package com.guassmith.createmore;

import static net.minecraft.util.Direction.UP;

import java.util.function.BiFunction;

import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class ShapeBuilder {

    public static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    public static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.makeCuboidShape(x1, y1, z1, x2, y2, z2);
    }

    public static class Builder {
        VoxelShape shape;

        public Builder(VoxelShape shape) {
            this.shape = shape;
        }

        public Builder add(VoxelShape shape) {
            this.shape = VoxelShapes.or(this.shape, shape);
            return this;
        }

        public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
            return add(cuboid(x1, y1, z1, x2, y2, z2));
        }

        public Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
            shape = VoxelShapes.combineAndSimplify(shape, cuboid(x1, y1, z1, x2, y2, z2), IBooleanFunction.ONLY_FIRST);
            return this;
        }

        VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
            return factory.apply(shape, direction);
        }

        VoxelShaper build(BiFunction<VoxelShape, Axis, VoxelShaper> factory) {
            return factory.apply(shape, Axis.Y);
        }

        public VoxelShaper forDirectional(Direction direction) {
            return build(VoxelShaper::forDirectional, direction);
        }

        public VoxelShaper forAxis() {
            return build(VoxelShaper::forAxis);
        }

        public VoxelShaper forDirectional() {
            return forDirectional(UP);
        }

    }

}

