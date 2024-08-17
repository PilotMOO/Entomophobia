package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSheepEntity;
import net.minecraft.resources.ResourceLocation;

public class MyiaticSheepModel extends GeoModel<MyiaticSheepEntity> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/myiaticsheep.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/myiaticsheep_texture.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticsheep.animation.json");

    @Override
    public ResourceLocation getModelResource(MyiaticSheepEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MyiaticSheepEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MyiaticSheepEntity animatable) {
        return animation;
    }
}
