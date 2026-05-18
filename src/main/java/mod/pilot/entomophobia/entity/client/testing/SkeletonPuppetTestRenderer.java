package mod.pilot.entomophobia.entity.client.testing;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.entomophobia.entity.EntoEntities;
import mod.pilot.entomophobia.entity.testing.SkeletonPuppetTestEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletonPuppetTestRenderer extends GeoEntityRenderer<SkeletonPuppetTestEntity> {
    public SkeletonPuppetTestRenderer(EntityRendererProvider.Context context) {
        super(context, EntoEntities.TEST_PUPPET.get());
    }

    @Override
    public void render(SkeletonPuppetTestEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        GeoModel<SkeletonPuppetTestEntity> model = getGeoModel();

        GeoBone left_arm = model.getBone("left_arm").orElse(null);
        GeoBone right_arm = model.getBone("right_arm").orElse(null);

        LivingEntity target = entity.getTarget();
        if (target != null) {
            final Vector3d targetPos = from(target.position())/*.add(0, target.getEyeHeight(), 0)*/;

            if (left_arm != null) {
                Vector3d pos = /*left_arm.getWorldPosition()*/ from(entity.position());
                double diffX = pos.x - targetPos.x, diffY = pos.y - targetPos.y, diffZ = pos.z - targetPos.z;
                double flatDist = Math.sqrt( diffX * diffX + diffZ * diffZ);
                double atanX = Math.atan2(diffY, flatDist);
                double atanY = Math.atan2(diffZ, diffX);
                left_arm.setRotX((float)atanX - 90f);
                left_arm.setRotY((float)(-(atanY + entity.getXRot())) + 90f);
            }
            //if (right_arm != null) right_arm.setRotX(right_arm.getRotX() - 1f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static Vector3d from(Vec3 v){
        return new Vector3d(v.x(), v.y(), v.z());
    }
}
