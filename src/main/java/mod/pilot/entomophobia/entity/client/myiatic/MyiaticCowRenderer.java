package mod.pilot.entomophobia.entity.client.myiatic;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MyiaticCowRenderer extends GeoEntityRenderer<MyiaticCowEntity> {
    public MyiaticCowRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MyiaticCowModel());
    }

    @Override
    public void render(MyiaticCowEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
