package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GrubPestRenderer extends GeoEntityRenderer<GrubPestEntity> {
    public GrubPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GrubPestModel());
    }
}
