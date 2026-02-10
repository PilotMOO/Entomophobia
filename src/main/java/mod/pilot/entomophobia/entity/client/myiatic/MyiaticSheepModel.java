package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticSheepEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

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
