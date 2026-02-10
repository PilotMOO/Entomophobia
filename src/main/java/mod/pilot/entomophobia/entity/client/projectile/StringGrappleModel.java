package mod.pilot.entomophobia.entity.client.projectile;

import mod.pilot.entomophobia.entity.projectile.StringGrappleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StringGrappleModel extends GeoModel<StringGrappleProjectile> {
    private static final ResourceLocation model = new ResourceLocation("entomophobia", "geo/entity/nothing.geo.json");
    private static final ResourceLocation texture = new ResourceLocation("entomophobia", "textures/entity/nothing.png");
    private static final ResourceLocation animation = new ResourceLocation("entomophobia", "animations/entity/myiaticchicken.animation.json");

    @Override
    public ResourceLocation getModelResource(StringGrappleProjectile animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(StringGrappleProjectile animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(StringGrappleProjectile animatable) {
        return animation;
    }
}
