package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticPigRenderer extends GeoEntityRenderer<MyiaticPigEntity> {
    public MyiaticPigRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticPigModel());
    }
}
