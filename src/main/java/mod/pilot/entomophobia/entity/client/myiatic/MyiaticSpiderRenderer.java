package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticSpiderRenderer extends GeoEntityRenderer<MyiaticSpiderEntity> {
    public MyiaticSpiderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticSpiderModel());
    }
}
