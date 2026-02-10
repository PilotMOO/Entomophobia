package mod.pilot.entomophobia.entity.client.celestial;

import mod.pilot.entomophobia.entity.celestial.CarrioniteEntity;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CarrioniteRenderer extends GeoEntityRenderer<CarrioniteEntity> {
    public CarrioniteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CarrioniteModel());
    }
}
