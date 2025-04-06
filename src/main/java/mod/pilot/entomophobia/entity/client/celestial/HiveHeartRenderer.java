package mod.pilot.entomophobia.entity.client.celestial;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HiveHeartRenderer extends GeoEntityRenderer<HiveHeartEntity> {
    private static final RenderType ARTERY_RENDER_TYPE = RenderType.entityCutoutNoCull(
            new ResourceLocation(Entomophobia.MOD_ID, "textures/misc/artery_vein_texture.png"));
    public HiveHeartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HiveHeartModel());
    }

    @Override
    public boolean shouldRender(HiveHeartEntity entity, @NotNull Frustum camera, double pCamX, double pCamY, double pCamZ) {
        if (!entity.shouldRender(pCamX, pCamY, pCamZ)) {
            return false;
        } else if (entity.noCulling) {
            return true;
        } else if (entity.hasArteries()) return true;
        else {
            AABB aabb = entity.getBoundingBoxForCulling().inflate(0.5D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(
                        entity.getX() - 2.0D, entity.getY() - 2.0D, entity.getZ() - 2.0D,
                        entity.getX() + 2.0D, entity.getY() + 2.0D, entity.getZ() + 2.0D);
            }
            return camera.isVisible(aabb);
        }
    }

    @Override
    public void render(@NotNull HiveHeartEntity entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        for (Vec3 arteryPos : entity.getOrCreateArteryHooks()) {
            //Invert because minecraft fucked up the Y axis so you gotta invert shit >:[
            Vec3 offsetSubPos = arteryPos.subtract(entity.position());
            arteryPos = arteryPos.subtract(offsetSubPos.scale(2));
            /**/

            poseStack.pushPose();
            Vec3 heartPos = this.getPosition(entity, (double)entity.getBbHeight() * 0.5D, partialTick);
            Vec3 PosSubEnd = heartPos.subtract(arteryPos);
            Vec3 directTo = EntomoDataManager.getDirectionToAFromB(heartPos, arteryPos).scale(0.5f);
            poseStack.translate(directTo.x, directTo.y/* + entity.getBbHeight() / 2*/, directTo.z);
            float PosSubEndDistPlus1 = (float)(PosSubEnd.length() + 1.0D);
            PosSubEnd = PosSubEnd.normalize();
            float AcosOfPSE = (float)Math.acos(PosSubEnd.y);
            float AtanOfPSE = (float)Math.atan2(PosSubEnd.z, PosSubEnd.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - AtanOfPSE) * (180F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(AcosOfPSE * (180F / (float)Math.PI)));

            float arteryScale = 0.25f;

            VertexConsumer vertexconsumer = bufferSource.getBuffer(ARTERY_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            float U1 = 0.0f; //Horizontal UV width min
            float U2 = 1f; //Horizontal UV width max
            float V1 = 0.0f; //Vertical UV width min
            float V2 = (PosSubEndDistPlus1 / (arteryScale * 2)) + V1; //Vertical UV width max
            //Calculate the height of the wanted UV rather than just setting it to 1 makes it repeat rather than stretch

            drawTaperedCube(vertexconsumer, matrix4f, matrix3f, posestack$pose,
                    arteryScale, arteryScale, arteryScale, arteryScale,
                    PosSubEndDistPlus1, OverlayTexture.NO_OVERLAY, 15728880,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    U1, U2, V1, V2);
            poseStack.popPose();
        }
    }

    private void drawTaperedCube(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal, PoseStack.Pose pose,
                                 float bottomWidth, float bottomDepth,
                                 float topWidth, float topDepth, float height,
                                 int overlay, int lightmap, float red, float green, float blue, float alpha,
                                 float U1, float U2,
                                 float V1, float V2) {
        // Define bottom and top face dimensions
        float x1b = -bottomWidth / 2, x2b = bottomWidth / 2;  // Bottom face width
        float x1t = -topWidth / 2, x2t = topWidth / 2;        // Top face width
        float z1b = -bottomDepth / 2, z2b = bottomDepth / 2;  // Bottom face depth
        float z1t = -topDepth / 2, z2t = topDepth / 2;        // Top face depth
        float y1 = 0f, y2 = height;                // Height

        // Bottom Face (larger or smaller base)
        ///drawFace(vertexConsumer, matrix, pose, x1b, y1, z2b, x2b, y1, z2b, x2b, y1, z1b, x1b, y1, z1b, overlay, lightmap, red, green, blue, alpha, 0, -1, 0);
        // Top Face (larger or smaller top)
        ///drawFace(vertexConsumer, matrix, pose, x1t, y2, z2t, x2t, y2, z2t, x2t, y2, z1t, x1t, y2, z1t, overlay, lightmap, red, green, blue, alpha, 0, 1, 0);

        // Front Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z2b, x2b, y1, z2b, x2t, y2, z2t, x1t, y2, z2t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Back Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z1b, x1b, y1, z1b, x1t, y2, z1t, x2t, y2, z1t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Left Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z1b, x1b, y1, z2b, x1t, y2, z2t, x1t, y2, z1t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Right Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z2b, x2b, y1, z1b, x2t, y2, z1t, x2t, y2, z2t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);

    }

    private void drawFace(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          float x3, float y3, float z3, float x4, float y4, float z4,
                          int overlay, int lightmap,
                          float red, float green, float blue, float alpha,
                          float U1, float U2, float V1, float V2,
                          float normalX, float normalY, float normalZ) {

        // First vertex (bottom left)
        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(red, green, blue, alpha)
                .uv(U1, V2)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Second vertex (bottom right)
        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(red, green, blue, alpha)
                .uv(U2, V2)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Third vertex (top right)
        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(red, green, blue, alpha)
                .uv(U2, V1)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Fourth vertex (top left)
        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(red, green, blue, alpha)
                .uv(U1, V1)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();
    }

    private static void vertex(VertexConsumer pConsumer, Matrix4f pPose, Matrix3f pNormal, float pX, float pY, float pZ,
                               int pRed, int pGreen, int pBlue, float pU, float pV) {
        pConsumer.vertex(pPose, pX, pY, pZ).color(255, 255, 255, 255)
                .uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)
                .normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private Vec3 getPosition(Entity target, double pYOffset, float pPartialTick) {
        double d0 = Mth.lerp(pPartialTick, target.xOld, target.getX());
        double d1 = Mth.lerp(pPartialTick, target.yOld, target.getY()) + pYOffset;
        double d2 = Mth.lerp(pPartialTick, target.zOld, target.getZ());
        return new Vec3(d0, d1, d2);
    }
}
