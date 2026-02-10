package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticCreeperRenderer extends GeoEntityRenderer<MyiaticCreeperEntity> {
    public MyiaticCreeperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticCreeperModel());
    }
}
