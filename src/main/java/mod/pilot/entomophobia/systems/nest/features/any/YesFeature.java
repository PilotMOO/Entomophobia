package mod.pilot.entomophobia.systems.nest.features.any;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.resources.ResourceLocation;

public class YesFeature extends Feature {
    private static final ResourceLocation structureLocation = new ResourceLocation(Entomophobia.MOD_ID, "yes");
    public YesFeature() {
        super((byte)1, (byte)0, (byte)0, structureLocation);
    }
}
