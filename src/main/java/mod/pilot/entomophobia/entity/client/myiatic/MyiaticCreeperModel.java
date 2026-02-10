package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MyiaticCreeperModel extends GeoModel<MyiaticCreeperEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticcreeper.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticcreeper_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticcreeper.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticCreeperEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticCreeperEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticCreeperEntity animatable) {
        return animation;
    }
}
