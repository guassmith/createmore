package com.guassmith.createmore;

import com.guassmith.createmore.dynamo.Dynamo;
import com.guassmith.createmore.electric_motor.ElectricMotor;
import com.guassmith.createmore.smart_motor.SmartMotor;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CStress;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.item.ItemGroup;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {

    private static final CreateRegistrate REGISTRATE = CreateMore.registrate().itemGroup(() -> ItemGroup.REDSTONE);

    public static final BlockEntry<ElectricMotor> ELECTRIC_MOTOR =
        REGISTRATE.block("electric_motor", ElectricMotor::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Dynamo> DYNAMO =
        REGISTRATE.block("dynamo", Dynamo::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SmartMotor> SMART_MOTOR =
        REGISTRATE.block("smart_motor", SmartMotor::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {}

    public static void registerStress() {
        CStress stressValues = AllConfigs.SERVER.kinetics.stressValues;

        stressValues.getCapacities().put(
                ModBlocks.ELECTRIC_MOTOR.get().getRegistryName(),
                Config.ELECTRIC_MOTOR.stressCapacity
        );

        stressValues.getImpacts().put(
                ModBlocks.DYNAMO.get().getRegistryName(),
                Config.DYNAMO.stressImpact
        );

        stressValues.getCapacities().put(
                ModBlocks.SMART_MOTOR.get().getRegistryName(),
                Config.ELECTRIC_MOTOR.stressCapacity
        );
    }
}
