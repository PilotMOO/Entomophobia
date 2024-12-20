package mod.pilot.entomophobia.systems.nest.features.wall;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.resources.ResourceLocation;

public class ThickWallTestFeature extends Feature {
    private static final String type = "chonky";
    private static final ResourceLocation north = new ResourceLocation(Entomophobia.MOD_ID, type + "north");
    private static final ResourceLocation east = new ResourceLocation(Entomophobia.MOD_ID, type + "east");
    private static final ResourceLocation south = new ResourceLocation(Entomophobia.MOD_ID, type + "south");
    private static final ResourceLocation west = new ResourceLocation(Entomophobia.MOD_ID, type + "west");
    public ThickWallTestFeature() {
        super(type, (byte)0, (byte)2, north);
        registerAsWallFeature(north, east, south, west);
    }
}
