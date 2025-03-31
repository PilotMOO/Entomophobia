package mod.pilot.entomophobia.entity.client.celestial;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HiveHeartRenderer extends GeoEntityRenderer<HiveHeartEntity> {
    public HiveHeartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HiveHeartModel());
    }
}
