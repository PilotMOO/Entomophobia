package mod.pilot.entomophobia.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.GenericModelRegistry.RenderBufferAccess;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.SkyboxModelManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class SkyRenderMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void AccessRenderBuffer(Minecraft pMinecraft, EntityRenderDispatcher pEntityRenderDispatcher,
                                   BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, RenderBuffers pRenderBuffers, CallbackInfo ci){
        RenderBufferAccess.registerAccess(pRenderBuffers);
    }


    @Inject(method = "renderSky", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;)V"))
    private void injectSkyModelRendering(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick,
                                         Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci){
        SkyboxModelManager.skyboxRenderHook(poseStack, projectionMatrix, partialTick, camera, isFoggy);
    }

    @Shadow @Nullable private ClientLevel level;
    @Unique private static final ResourceLocation entomophobia$ANNOUNCEMENT =
            new ResourceLocation(Entomophobia.MOD_ID, "textures/world/announcement.png");
    @Unique private static final boolean entomophobia$eggmanEnabled = Config.SERVER.eggman.get();

    @Inject(method = "renderSky", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"))
    public void pissOnTheMoon(PoseStack pPoseStack, Matrix4f pProjectionMatrix,
                                  float pPartialTick, Camera pCamera, boolean pIsFoggy,
                                  Runnable pSkyFogSetup, CallbackInfo ci){
        if (!entomophobia$eggmanEnabled) return;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        //pPoseStack.pushPose();
        assert this.level != null;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        pPoseStack.mulPose(Axis.XP.rotationDegrees((this.level.getTimeOfDay(pPartialTick) * 1.5f) * 360.0F));
        Matrix4f matrix4f1 = pPoseStack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, entomophobia$ANNOUNCEMENT);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
