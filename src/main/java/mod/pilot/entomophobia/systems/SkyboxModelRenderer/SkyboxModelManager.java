package mod.pilot.entomophobia.systems.SkyboxModelRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SkyboxModelManager {
    private static final ArrayList<RenderPackage> _vPackages = new ArrayList<>();
    public static ArrayList<RenderPackage> _que = new ArrayList<>();
    private static boolean _packagesInQue = false;

    public static void SkyboxRenderHook(PoseStack poseStack, Matrix4f projectionMatrix,
                                        float partialTick, Camera camera, boolean isFoggy){
        if (_packagesInQue) {
            _vPackages.addAll(_que);
            _que.clear();
            _packagesInQue = false;
        }

        Quaternionf q = new Quaternionf();
        _vPackages.forEach((renderPackage -> renderPackage.render(poseStack, projectionMatrix, partialTick, camera, isFoggy, q)));
        //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    public static class RenderPackage{
        public final IGenericModel model;
        public Model getModel(){
            return model.getAsModel();
        }

        public static final Vec3 defaultOffset = new Vec3(0, 1, 0);
        public Vec3 offset = defaultOffset;
        public RenderPackage offset(double x, double y, double z){
            return this.offset(new Vec3(x, y, z));
        }
        public RenderPackage offset(Vec3 offset){
            this.offset = offset;
            return this;
        }
        public void translatePoseStack(PoseStack poseStack){
            Vec3 _translate = offset;
            if (monkeyWrenchFixes) {
                //if (_translate.y == 0) _translate = _translate.add(0, 1, 0);
                _translate = _translate.multiply(bobOffsetScaleBy);
            }
            poseStack.translate(_translate.x, _translate.y, _translate.z);
        }

        public float xRot, yRot, zRot = 0f;
        public RenderPackage rotate(float x, float y, float z){
            this.xRot = x;
            this.yRot = y;
            this.zRot = z;
            return this;
        }
        public void rotatePoseStack(PoseStack stack){
            this.rotatePoseStack(stack, null);
        }
        public void rotatePoseStack(PoseStack poseStack, @Nullable Quaternionf q){
            q = q != null ? q : new Quaternionf();
            q.rotateXYZ(xRot, yRot, zRot);
            poseStack.mulPose(q);
        }
        public static final Vec3 defaultScale = new Vec3(1, 1, 1);
        public Vec3 scale = defaultScale;
        public RenderPackage inflate(Vec3 scale){
            this.scale = scale;
            return this;
        }
        public void scalePoseStack(PoseStack poseStack){
            Vec3 _scale = scale;
            if (monkeyWrenchFixes) {
                _scale = _scale.multiply(1, -1, 1);
                _scale = _scale.multiply(BobInflateScaleBy);
            }
            poseStack.scale((float)_scale.x, (float)_scale.y, (float)_scale.z);
        }


        public boolean monkeyWrenchFixes = true;
        public RenderPackage disableFixes(){
            this.monkeyWrenchFixes = false;
            return this;
        }
        public Vec3 bobOffsetScaleBy = new Vec3(100, 200, 100);
        public Vec3 BobInflateScaleBy = new Vec3(25, 25, 25);

        public RenderPackage(IGenericModel m){
            this.model = m;
        }

        public void ModifyPoseStack(PoseStack poseStack, @Nullable Quaternionf q){
            this.translatePoseStack(poseStack);
            this.rotatePoseStack(poseStack, q);
            this.scalePoseStack(poseStack);
        }

        public void render(PoseStack poseStack, Matrix4f projectionMatrix,
                           float partialTick, Camera camera, boolean isFoggy, @Nullable Quaternionf q){
            poseStack.pushPose();
            ModifyPoseStack(poseStack, q);
            getModel().renderToBuffer(poseStack, model.getVertexConsumer(),
                    15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            poseStack.popPose();
        }

        public void que(){
            SkyboxModelManager._que.add(this);
            SkyboxModelManager._packagesInQue = true;
        }
    }
}
