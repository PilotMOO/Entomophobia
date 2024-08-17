package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.resources.ResourceLocation;

public class MyiaticPigModel extends GeoModel<MyiaticPigEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticpig.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticpig_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticpig.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticPigEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticPigEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticPigEntity animatable) {
        return animation;
    }
}
