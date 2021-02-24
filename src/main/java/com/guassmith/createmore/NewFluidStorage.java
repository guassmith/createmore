package com.guassmith.createmore;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class NewFluidStorage extends FluidTank {

    private final LazyOptional<IFluidTank> optional = LazyOptional.of(() -> this);

    public NewFluidStorage(int capacity) {
        super(capacity);
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return stack.getFluid().getTags().stream().anyMatch(r -> r.toString().equals("forge:steam"));
    }

    public LazyOptional<IFluidTank> getOptional() {
        return optional;
    }

    public void invalidateOptional() {
        optional.invalidate();
    }
}
