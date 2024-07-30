package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.resources.ResourceLocation;

public class MyiaticZombieModel extends GeoModel<MyiaticZombieEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticzombie.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticzombie_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticzombie.animation.json");
    @Override
    public ResourceLocation getModelResource(MyiaticZombieEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticZombieEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticZombieEntity animatable) {
        return animation;
    }
}
