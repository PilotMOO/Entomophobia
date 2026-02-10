package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.SpiderPestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpiderPestRenderer extends GeoEntityRenderer<SpiderPestEntity> {
    public SpiderPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiderPestModel());
    }
}
