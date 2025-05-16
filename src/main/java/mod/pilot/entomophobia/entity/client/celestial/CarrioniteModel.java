package mod.pilot.entomophobia.entity.client.celestial;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.celestial.CarrioniteEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.resources.ResourceLocation;

public class CarrioniteModel extends GeoModel<CarrioniteEntity> {
    private static final ResourceLocation model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/carrionite.geo.json");

    private static final ResourceLocation texture = new ResourceLocation(Entomophobia.MOD_ID, "textures/entity/carrionite_texture.png");

    private static final ResourceLocation animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/carrionite.animation.json");

    @Override
    public ResourceLocation getModelResource(CarrioniteEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(CarrioniteEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CarrioniteEntity animatable) {
        return animation;
    }
}
