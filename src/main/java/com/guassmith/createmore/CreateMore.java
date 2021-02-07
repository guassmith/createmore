package com.guassmith.createmore;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreateMore.MODID)
public class CreateMore
{
    public static final String MODID = "createmore";
    private static final Logger LOGGER = LogManager.getLogger();


    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(MODID);

    public CreateMore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonConf);

        //MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.register();
        ModTiles.register();
    }

    private void setup(final FMLCommonSetupEvent event) {

        AllConfigs.SERVER.kinetics.stressValues.getCapacities().put(
            ModBlocks.ELECTRIC_MOTOR.get().getRegistryName(),
            Config.ELECTRIC_MOTOR.stressCapacity
        );

        AllConfigs.SERVER.kinetics.stressValues.getImpacts().put(
                ModBlocks.DYNAMO.get().getRegistryName(),
                Config.DYNAMO.stressImpact
        );
    }

    public static CreateRegistrate registrate() {
        return registrate.get();
    }

    //returns on which side of the origin block the neighbour is
    public static Direction getSide(BlockPos origin, BlockPos neighbour) {
        for (Direction dir : Direction.values()) {
            if (origin.offset(dir).equals(neighbour)) {
                return dir;
            }
        }
        return null;
    }
}
