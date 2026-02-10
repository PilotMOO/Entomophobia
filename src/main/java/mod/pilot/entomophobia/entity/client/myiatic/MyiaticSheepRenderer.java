package mod.pilot.entomophobia.entity.client.myiatic;

import mod.pilot.entomophobia.entity.myiatic.MyiaticSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MyiaticSheepRenderer extends GeoEntityRenderer<MyiaticSheepEntity> {
    public MyiaticSheepRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticSheepModel());
    }
}
