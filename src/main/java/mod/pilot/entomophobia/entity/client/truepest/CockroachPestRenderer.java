package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CockroachPestRenderer extends GeoEntityRenderer<CockroachPestEntity> {
    public CockroachPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CockroachPestModel());
    }
}
