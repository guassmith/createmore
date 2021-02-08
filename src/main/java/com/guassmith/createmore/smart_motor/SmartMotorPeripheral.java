package com.guassmith.createmore.smart_motor;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SmartMotorPeripheral implements IPeripheral {

    private final LazyOptional<IPeripheral> optional = LazyOptional.of(() -> this);
    private final SmartMotorTile tile;

    public SmartMotorPeripheral(SmartMotorTile te) {
        tile = te;
    }

    @Nonnull
    @Override
    public String getType() {
        return "smart_motor";
    }

    @Nullable
    @Override
    public Object getTarget() {
        return tile;
    }

    /**
     * Sets the speed of the smart motor.
     *
     * @param speed The desired speed of the motor.
     * @return The new speed of the motor.
     */
    @LuaFunction
    public final int setSpeed(int speed)
    {
        return tile.setGeneratedSpeed(speed);
    }

    /**
     * Gets the speed of the smart motor.
     *
     * @return The speed of the motor.
     */
    @LuaFunction
    public final int getSpeed()
    {
        return tile.getGeneratedSpeedValue();
    }

    /**
     * Gets the amount of energy stored in the motor.
     *
     * @return The energy stored in the motor.
     */
    @LuaFunction
    public final int getEnergy()
    {
        return tile.getEnergy();
    }

    /**
     * Checks if the motor has enough power to spin.
     *
     * @return true if has enough power and false if it doesn't.
     */
    @LuaFunction
    public final boolean hasPower()
    {
        return tile.hasPower();
    }

    /**
     * Turns the motor on or off.
     *
     * @param enabled true to enable and false to disable.
     */
    @LuaFunction( mainThread = true )
    public final void setEnabled(boolean enabled)
    {
        tile.setEnabled(enabled);
    }

    /**
     * Checks if the smart motor is enabled or not.
     *
     * @return If the motor is enabled or not.
     */
    @LuaFunction
    public final boolean isEnabled()
    {
        return tile.getEnabled();
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    public LazyOptional<IPeripheral> getOptional() {
        return optional;
    }
    public void invalidateOptional() {
        optional.invalidate();
    }
}
