package mod.pilot.entomophobia.entity.client.truepest;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GrubPestRenderer extends GeoEntityRenderer<GrubPestEntity> {
    public GrubPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GrubPestModel());
    }
}
