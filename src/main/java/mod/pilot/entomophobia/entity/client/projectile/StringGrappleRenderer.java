package mod.pilot.entomophobia.entity.client.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.projectile.StringGrappleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class StringGrappleRenderer extends GeoEntityRenderer<StringGrappleProjectile> {
    private static final RenderType GRAPPLE_RENDER_TYPE = RenderType.entityCutoutNoCull(new ResourceLocation("textures/block/cobweb.png"));
    public StringGrappleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StringGrappleModel());
    }

    @Override
    public void render(StringGrappleProjectile grapple, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(grapple, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        Entity parent = grapple.getOwner();
        if (parent != null) {
            float AnimScale = 1;
            float ClientSideAttackAnimPlusPartial = 0.5f;
            float CSAAPPButchered = ClientSideAttackAnimPlusPartial * 0.5F % 1.0F;
            float eyeHeight = grapple.getEyeHeight();
            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, eyeHeight, 0.0F);
            Vec3 Position = this.getPosition(parent, (double)parent.getBbHeight() * 0.5D, pPartialTicks);
            Vec3 PosAtEye = this.getPosition(grapple, eyeHeight, pPartialTicks);
            Vec3 PosEyeDirection = Position.subtract(PosAtEye);
            float PosEyeDistanceP1 = (float)(PosEyeDirection.length() + 1.0D);
            PosEyeDirection = PosEyeDirection.normalize();
            float AcosOfPED = (float)Math.acos(PosEyeDirection.y);
            float AtanOfPED = (float)Math.atan2(PosEyeDirection.z, PosEyeDirection.x);
            pPoseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - AtanOfPED) * (180F / (float)Math.PI)));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(AcosOfPED * (180F / (float)Math.PI)));
            int Unused = 1;
            float CSAAPPReduced = ClientSideAttackAnimPlusPartial * 0.05F * -1.5F;
            float AnimScaleSqr = AnimScale * AnimScale;
            int AnimSSFull = 64 + (int)(AnimScaleSqr * 191.0F);
            int AnimSSHalf = 32 + (int)(AnimScaleSqr * 191.0F);
            int AminSSReduced = 128 - (int)(AnimScaleSqr * 64.0F);
            float UnusedTwo = 0.2F;
            float UnusedThree = 0.282F;
            float f11 = Mth.cos(CSAAPPReduced + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(CSAAPPReduced + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(CSAAPPReduced + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(CSAAPPReduced + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(CSAAPPReduced + 3.926991F) * 0.282F;
            float f16 = Mth.sin(CSAAPPReduced + 3.926991F) * 0.282F;
            float f17 = Mth.cos(CSAAPPReduced + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(CSAAPPReduced + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(CSAAPPReduced + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(CSAAPPReduced + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(CSAAPPReduced + 0.0F) * 0.2F;
            float f22 = Mth.sin(CSAAPPReduced + 0.0F) * 0.2F;
            float f23 = Mth.cos(CSAAPPReduced + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(CSAAPPReduced + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(CSAAPPReduced + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(CSAAPPReduced + ((float)Math.PI * 1.5F)) * 0.2F;
            float UnusedFour = 0.0F;
            float UnusedFive = 0.4999F;
            float f29 = -1.0F + CSAAPPButchered;
            float f30 = PosEyeDistanceP1 * 2.5F + f29;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(GRAPPLE_RENDER_TYPE);
            PoseStack.Pose posestack$pose = pPoseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, PosEyeDistanceP1, f20, 255, 255, 255, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, 255, 255, 255, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, 255, 255, 255, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, PosEyeDistanceP1, f22, 255, 255, 255, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, PosEyeDistanceP1, f24, 255, 255, 255, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, 255, 255, 255, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, 255, 255, 255, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, PosEyeDistanceP1, f26, 255, 255, 255, 0.0F, f30);
            float f31 = 0.0F;
            if (grapple.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, PosEyeDistanceP1, f12, 255, 255, 255, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, PosEyeDistanceP1, f14, 255, 255, 255, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, PosEyeDistanceP1, f18, 255, 255, 255, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, PosEyeDistanceP1, f16, 255, 255, 255, 0.5F, f31);
            pPoseStack.popPose();
        }
    }

    private static void vertex(VertexConsumer pConsumer, Matrix4f pPose, Matrix3f pNormal, float pX, float pY, float pZ, int pRed, int pGreen, int pBlue, float pU, float pV) {
        pConsumer.vertex(pPose, pX, pY, pZ).color(255, 255, 255, 255).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private Vec3 getPosition(Entity target, double pYOffset, float pPartialTick) {
        double d0 = Mth.lerp(pPartialTick, target.xOld, target.getX());
        double d1 = Mth.lerp(pPartialTick, target.yOld, target.getY()) + pYOffset;
        double d2 = Mth.lerp(pPartialTick, target.zOld, target.getZ());
        return new Vec3(d0, d1, d2);
    }
}
