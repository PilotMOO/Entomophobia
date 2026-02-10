package mod.pilot.entomophobia.entity.client.pheromones;

import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PheromonePreyHuntRenderer extends GeoEntityRenderer<PheromonePreyHuntEntity> {
    public PheromonePreyHuntRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PheromonePreyHuntModel());
    }
}