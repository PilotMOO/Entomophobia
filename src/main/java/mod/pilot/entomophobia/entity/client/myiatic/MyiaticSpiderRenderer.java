package mod.pilot.entomophobia.entity.client.myiatic;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSpiderEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticSpiderRenderer extends GeoEntityRenderer<MyiaticSpiderEntity> {
    public MyiaticSpiderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticSpiderModel());
    }
}
