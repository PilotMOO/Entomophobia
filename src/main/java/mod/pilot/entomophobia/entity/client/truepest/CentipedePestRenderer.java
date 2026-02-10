package mod.pilot.entomophobia.entity.client.truepest;

import mod.pilot.entomophobia.entity.truepest.CentipedePestEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CentipedePestRenderer extends GeoEntityRenderer<CentipedePestEntity> {
    public CentipedePestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CentipedePestModel());
    }
}
