package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticChickenRenderer extends GeoEntityRenderer<MyiaticChickenEntity> {
    public MyiaticChickenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticChickenModel());
    }
}
