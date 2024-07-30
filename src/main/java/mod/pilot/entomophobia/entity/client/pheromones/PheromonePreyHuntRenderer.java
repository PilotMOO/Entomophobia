package mod.pilot.entomophobia.entity.client.pheromones;

import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PheromonePreyHuntRenderer extends GeoEntityRenderer<PheromonePreyHuntEntity> {
    public PheromonePreyHuntRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PheromonePreyHuntModel());
    }
}