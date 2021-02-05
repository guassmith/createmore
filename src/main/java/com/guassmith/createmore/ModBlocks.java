package com.guassmith.createmore;

import com.guassmith.createmore.electric_motor.ElectricMotor;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {

    private static final CreateRegistrate REGISTRATE = CreateMore.registrate().itemGroup(() -> ItemGroup.REDSTONE);

    public static final BlockEntry<ElectricMotor> ELECTRIC_MOTOR =
            REGISTRATE.block("electric_motor", ElectricMotor::new)
                .initialProperties(SharedProperties::stone)
                .tag(AllBlockTags.SAFE_NBT.tag)
                //.transform(StressConfigDefaults.setCapacity(1024.0))
                .item()
                .transform(customItemModel())
                .register();

    public static void register() {}
}
