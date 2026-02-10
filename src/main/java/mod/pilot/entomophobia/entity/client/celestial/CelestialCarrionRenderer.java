package mod.pilot.entomophobia.entity.client.celestial;

import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CelestialCarrionRenderer extends GeoEntityRenderer<CelestialCarrionEntity> {
    public CelestialCarrionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CelestialCarrionModel());
    }
}
