package mod.pilot.entomophobia.entity.client.pheromones;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraft.resources.ResourceLocation;

public class PheromonePreyHuntModel extends GeoModel<PheromonePreyHuntEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/nothing.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/nothing.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticzombie.animation.json");
    @Override
    public ResourceLocation getModelResource(PheromonePreyHuntEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(PheromonePreyHuntEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PheromonePreyHuntEntity animatable) {
        return animation;
    }
}