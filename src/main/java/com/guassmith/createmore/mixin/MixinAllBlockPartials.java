package com.guassmith.createmore.mixin;

import com.guassmith.createmore.steam_engine.BlockPartialsAccessor;
import com.mojang.datafixers.TypeRewriteRule;
import com.simibubi.create.AllBlockPartials;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllBlockPartials.class)
public abstract class MixinAllBlockPartials implements BlockPartialsAccessor {

    /*private static AllBlockPartials get(String path) {
        AllBlockPartials partials = new AllBlockPartials();
        partials.modelLocation = new ResourceLocation("createmore", "block/" + path);
        all.add(partials);
        return partials;
    }*/

    @Shadow private ResourceLocation modelLocation;
    @Shadow private static AllBlockPartials get(String path) {
        throw new AbstractMethodError("Shadow");
    }

    @Override
    public void setModelLocation(ResourceLocation location) {
        modelLocation = location;
    }

    @Inject(method = "get(Ljava/lang/String;)Lcom/simibubi/create/AllBlockPartials;", at = @At("RETURN"), remap = false, cancellable = true)
    private static void onGet(String path, CallbackInfoReturnable<AllBlockPartials> cir) {
        if(path.startsWith("%")) {
            String[] strings = path.substring(1).split("%");
            String namespace = strings[0];
            String truePath = strings[1];
            AllBlockPartials partial = cir.getReturnValue();
            ((BlockPartialsAccessor)partial).setModelLocation(new ResourceLocation(namespace, truePath));
            cir.setReturnValue(partial);
        } else {
            cir.cancel();
        }
    }

    @Override
    public AllBlockPartials getPartial(String path) {
        return get(path);
    }
}
