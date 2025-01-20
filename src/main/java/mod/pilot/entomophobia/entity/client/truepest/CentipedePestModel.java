package mod.pilot.entomophobia.entity.client.truepest;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.truepest.CentipedePestEntity;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import net.minecraft.resources.ResourceLocation;

public class CentipedePestModel extends GeoModel<CentipedePestEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/centipede_pest.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/centipede_pest_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/centipede_pest.animation.json");

    @Override
    public ResourceLocation getModelResource(CentipedePestEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(CentipedePestEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CentipedePestEntity animatable) {
        return animation;
    }
}
