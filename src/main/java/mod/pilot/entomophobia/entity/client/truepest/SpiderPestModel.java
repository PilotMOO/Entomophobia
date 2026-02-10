package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.SpiderPestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpiderPestModel extends GeoModel<SpiderPestEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/spider_pest.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/spider_pest_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/spider_pest.animation.json");

    @Override
    public ResourceLocation getModelResource(SpiderPestEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SpiderPestEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SpiderPestEntity animatable) {
        return animation;
    }
}
