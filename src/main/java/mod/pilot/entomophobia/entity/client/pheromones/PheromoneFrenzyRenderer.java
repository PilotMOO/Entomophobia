package mod.pilot.entomophobia.entity.client.pheromones;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.pheromones.PheromoneFrenzyEntity;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PheromoneFrenzyRenderer extends GeoEntityRenderer<PheromoneFrenzyEntity> {
    public PheromoneFrenzyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PheromoneFrenzyModel());
    }
}