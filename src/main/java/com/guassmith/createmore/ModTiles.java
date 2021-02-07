package com.guassmith.createmore;

import com.guassmith.createmore.dynamo.DynamoRenderer;
import com.guassmith.createmore.dynamo.DynamoTile;
import com.guassmith.createmore.electric_motor.ElectricMotorRenderer;
import com.guassmith.createmore.electric_motor.ElectricMotorTile;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class ModTiles {

    public static final TileEntityEntry<ElectricMotorTile> ELECTRIC_MOTOR_TILE = CreateMore.registrate()
        .tileEntity("electric_motor", ElectricMotorTile::new)
        .validBlocks(ModBlocks.ELECTRIC_MOTOR)
        .renderer(() -> ElectricMotorRenderer::new)
        .register();

    public static final TileEntityEntry<DynamoTile> DYNAMO_TILE = CreateMore.registrate()
        .tileEntity("dynamo", DynamoTile::new)
        .validBlocks(ModBlocks.DYNAMO)
        .renderer(() -> DynamoRenderer::new)
        .register();

    public static void register() {}

}
