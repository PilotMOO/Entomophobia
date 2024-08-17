package mod.pilot.entomophobia.entity.client.myiatic;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticSheepRenderer extends GeoEntityRenderer<MyiaticSheepEntity> {
    public MyiaticSheepRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticSheepModel());
    }
}
