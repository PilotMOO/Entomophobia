package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrubPestModel extends GeoModel<GrubPestEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/grub_pest.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/grub_pest_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/grub_pest.animation.json");

    @Override
    public ResourceLocation getModelResource(GrubPestEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GrubPestEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GrubPestEntity animatable) {
        return animation;
    }
}
