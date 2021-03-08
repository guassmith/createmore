package com.guassmith.createmore.mixin;

import com.guassmith.createmore.steam_engine.FlywheelGetter;
import com.simibubi.create.content.contraptions.components.flywheel.FlywheelTileEntity;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlywheelTileEntity.class)
public interface MixinFlywheelTileEntity {

    @Accessor InterpolatedChasingValue getVisualSpeed();

    @Accessor float getAngle();
}
