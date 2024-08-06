package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticCreeperRenderer extends GeoEntityRenderer<MyiaticCreeperEntity> {
    public MyiaticCreeperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticCreeperModel());
    }
}
