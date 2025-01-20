package mod.pilot.entomophobia.entity.client.truepest;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CockroachPestRenderer extends GeoEntityRenderer<CockroachPestEntity> {
    public CockroachPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CockroachPestModel());
    }
}
