package mod.pilot.entomophobia.entity.client.truepest;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import net.minecraft.resources.ResourceLocation;

public class CockroachPestModel extends GeoModel<CockroachPestEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/cockroach_pest.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/cockroach_pest_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/cockroach_pest.animation.json");

    @Override
    public ResourceLocation getModelResource(CockroachPestEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(CockroachPestEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CockroachPestEntity animatable) {
        return animation;
    }
}
