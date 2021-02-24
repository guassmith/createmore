package com.guassmith.createmore;

import com.guassmith.createmore.mixin.MixinAllBlockPartials;
import com.guassmith.createmore.steam_engine.BlockPartialsAccessor;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

//todo add data generation

@Mod(CreateMore.MODID)
public class CreateMore
{
    public static final String MODID = "createmore";
    //private static final Logger LOGGER = LogManager.getLogger();
    public static final AllBlockPartials PISTON = ((BlockPartialsAccessor)AllBlockPartials.FLYWHEEL).getPartial("steam_engine/piston");

    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(MODID);
    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID+".main") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() { return new ItemStack(ModBlocks.ELECTRIC_MOTOR.get()); }
    };

    public CreateMore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonConf);

        ModBlocks.register();
        ModTiles.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModBlocks.registerStress();
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

    public static ITextComponent siFormatter(int value, String key) {
        String txtValue = Integer.toString(value);
        String prefix = "regular";
        if(value >= 1000000) {
            prefix = "mega";
            txtValue = String.format("%.2f", (float)value/1000000);
        } else if(value >= 1000){
            prefix = "kilo";
            txtValue = String.format("%.1f", (float)value/1000);
        }

        return new StringTextComponent(txtValue+" ")
            .append(new TranslationTextComponent(MODID+"."+key+"."+prefix))
            .append(new StringTextComponent(" "));
    }

    /*public static AllBlockPartials getSteamEnginePiston() {
        AllBlockPartials partials = new AllBlockPartials();
        partials.modelLocation = new ResourceLocation(Create.ID, "block/" + path);
        all.add(partials);
        return partials;
    }*/
}
