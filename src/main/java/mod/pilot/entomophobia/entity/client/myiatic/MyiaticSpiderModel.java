package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSpiderEntity;
import net.minecraft.resources.ResourceLocation;

public class MyiaticSpiderModel extends GeoModel<MyiaticSpiderEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticspider.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticspider_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticspider.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticSpiderEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticSpiderEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticSpiderEntity animatable) {
        return animation;
    }
}
