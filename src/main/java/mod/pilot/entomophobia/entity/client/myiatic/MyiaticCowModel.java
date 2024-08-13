package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.resources.ResourceLocation;

public class MyiaticCowModel extends GeoModel<MyiaticCowEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticcow.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticcow_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticcow.animation.json");

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
