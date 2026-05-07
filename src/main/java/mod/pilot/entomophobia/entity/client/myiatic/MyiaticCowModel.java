package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MyiaticCowModel extends GeoModel<MyiaticCowEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiatic_cow.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiatic_cow_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiatic_cow.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticCowEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticCowEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticCowEntity animatable) {
        return animation;
    }
}
