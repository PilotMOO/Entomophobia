package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSheepEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MyiaticChickenModel extends GeoModel<MyiaticChickenEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticchicken.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticchicken_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticchicken.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticChickenEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticChickenEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticChickenEntity animatable) {
        return animation;
    }
}
