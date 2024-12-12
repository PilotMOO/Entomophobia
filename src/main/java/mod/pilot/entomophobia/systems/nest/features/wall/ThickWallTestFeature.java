package mod.pilot.entomophobia.systems.nest.features.wall;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.resources.ResourceLocation;

public class ThickWallTestFeature extends Feature {
    private static final ResourceLocation north = new ResourceLocation(Entomophobia.MOD_ID, "chonkynorth");
    private static final ResourceLocation east = new ResourceLocation(Entomophobia.MOD_ID, "chonkyeast");
    private static final ResourceLocation south = new ResourceLocation(Entomophobia.MOD_ID, "chonkysouth");
    private static final ResourceLocation west = new ResourceLocation(Entomophobia.MOD_ID, "chonkywest");
    public ThickWallTestFeature() {
        super((byte)2, (byte)0, (byte)2, north);
        registerAsWallFeature(north, east, south, west);
    }
}
