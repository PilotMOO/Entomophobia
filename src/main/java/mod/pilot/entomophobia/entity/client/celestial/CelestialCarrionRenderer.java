package mod.pilot.entomophobia.entity.client.celestial;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CelestialCarrionRenderer extends GeoEntityRenderer<CelestialCarrionEntity> {
    public CelestialCarrionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CelestialCarrionModel());
    }
}
