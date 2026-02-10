package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticZombieRenderer extends GeoEntityRenderer<MyiaticZombieEntity> {
    public MyiaticZombieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticZombieModel());
    }
}
