package mod.pilot.entomophobia.entity.client.truepest;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.truepest.CentipedePestEntity;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CentipedePestRenderer extends GeoEntityRenderer<CentipedePestEntity> {
    public CentipedePestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CentipedePestModel());
    }
}
