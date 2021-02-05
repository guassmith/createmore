package com.guassmith.createmore;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateMore.MODID)
public class CreateMore
{
    public static final String MODID = "createmore";

    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(MODID);

    public CreateMore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        //MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.register();
        ModTiles.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, Config.spec);
        AllConfigs.SERVER.kinetics.stressValues.getCapacities().put(
            ModBlocks.ELECTRIC_MOTOR.get().getRegistryName(),
            Config.ELECTRIC_MOTOR.stressCapacity
        );
    }

    public static CreateRegistrate registrate() {
        return registrate.get();
    }
}
