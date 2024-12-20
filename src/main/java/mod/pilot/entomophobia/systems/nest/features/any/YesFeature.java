package mod.pilot.entomophobia.systems.nest.features.any;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.resources.ResourceLocation;

public class YesFeature extends Feature {
    private static final String type = "yes";
    private static final ResourceLocation structureLocation = new ResourceLocation(Entomophobia.MOD_ID, type);
    public YesFeature() {
        super(type, (byte)0, (byte)0, structureLocation);
    }
}
