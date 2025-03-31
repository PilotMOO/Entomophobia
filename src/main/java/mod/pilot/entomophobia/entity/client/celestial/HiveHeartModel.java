package mod.pilot.entomophobia.entity.client.celestial;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.resources.ResourceLocation;

public class HiveHeartModel extends GeoModel<HiveHeartEntity> {
    private static final ResourceLocation model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/hive_heart.geo.json");

    private static final ResourceLocation texture = new ResourceLocation(Entomophobia.MOD_ID, "textures/entity/hive_heart_texture.png");

    private static final ResourceLocation animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/hive_heart.animation.json");

    @Override
    public ResourceLocation getModelResource(HiveHeartEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(HiveHeartEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(HiveHeartEntity animatable) {
        return animation;
    }
}
