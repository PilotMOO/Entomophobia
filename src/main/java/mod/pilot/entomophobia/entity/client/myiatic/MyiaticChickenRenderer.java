package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticChickenRenderer extends GeoEntityRenderer<MyiaticChickenEntity> {
    public MyiaticChickenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticChickenModel());
    }
}
