package mod.pilot.entomophobia.entity.client.celestial;

import mod.azure.azurelib.model.GeoModel;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.resources.ResourceLocation;

public class CelestialCarrionModel extends GeoModel<CelestialCarrionEntity> {
    private static final ResourceLocation base_model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/carrion_base.geo.json");
    private static final ResourceLocation tendril_model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/carrion_tendril.geo.json");
    private static final ResourceLocation eye_model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/carrion_eye.geo.json");
    private static final ResourceLocation claw_model = new ResourceLocation(Entomophobia.MOD_ID, "geo/entity/carrion_claw.geo.json");

    private static final ResourceLocation texture = new ResourceLocation(Entomophobia.MOD_ID, "textures/entity/carrion_texture.png");

    private static final ResourceLocation base_animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/carrion_base.animation.json");
    private static final ResourceLocation tendril_animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/carrion_tendril.animation.json");
    private static final ResourceLocation eye_animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/carrion_eye.animation.json");
    private static final ResourceLocation claw_animation = new ResourceLocation(Entomophobia.MOD_ID, "animations/entity/carrion_claw.animation.json");

    @Override
    public ResourceLocation getModelResource(CelestialCarrionEntity animatable) {
        return switch (animatable.getMType()){
            case base, empty -> base_model;
            case tendril -> tendril_model;
            case eye -> eye_model;
            case claw -> claw_model;
        };
    }

    @Override
    public ResourceLocation getTextureResource(CelestialCarrionEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CelestialCarrionEntity animatable) {
        return switch (animatable.getMType()){
            case base, empty -> base_animation;
            case tendril -> tendril_animation;
            case eye -> eye_animation;
            case claw -> claw_animation;
        };
    }
}
