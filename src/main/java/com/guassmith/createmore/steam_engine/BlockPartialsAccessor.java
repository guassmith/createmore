package com.guassmith.createmore.steam_engine;

import com.simibubi.create.AllBlockPartials;
import net.minecraft.util.ResourceLocation;

public interface BlockPartialsAccessor {
    void setModelLocation(ResourceLocation location);
    AllBlockPartials getPartial(String path);
}
