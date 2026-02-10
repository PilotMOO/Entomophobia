package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticPigRenderer extends GeoEntityRenderer<MyiaticPigEntity> {
    public MyiaticPigRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticPigModel());
    }
}
