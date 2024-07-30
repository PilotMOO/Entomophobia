package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticZombieRenderer extends GeoEntityRenderer<MyiaticZombieEntity> {
    public MyiaticZombieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticZombieModel());
    }
}
