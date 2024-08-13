package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticCowRenderer extends GeoEntityRenderer<MyiaticCowEntity> {
    public MyiaticCowRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticCowModel());
    }
}
