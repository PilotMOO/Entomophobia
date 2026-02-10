package mod.pilot.entomophobia.entity.client.pheromones;

import mod.pilot.entomophobia.entity.pheromones.PheromoneFrenzyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PheromoneFrenzyRenderer extends GeoEntityRenderer<PheromoneFrenzyEntity> {
    public PheromoneFrenzyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PheromoneFrenzyModel());
    }
}