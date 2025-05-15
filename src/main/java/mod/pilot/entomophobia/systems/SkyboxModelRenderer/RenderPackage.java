package mod.pilot.entomophobia.systems.SkyboxModelRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.RenderPackageKeyframe;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RenderPackage{
    public final IGenericModel model;
    public Model getModel(){
        return model.getAsModel();
    }

    public static final Vec3 defaultOffset = new Vec3(0, 1, 0);
    public Vec3 offset = defaultOffset;
    public Vec3 offset_o = offset;
    public RenderPackage offset(double x, double y, double z){
        return this.offset(new Vec3(x, y, z));
    }
    public RenderPackage offset(Vec3 offset){
        this.offset = offset;
        return this;
    }
    public void translatePoseStack(PoseStack poseStack, float partial){
        Vec3 _translate = lerp(offset_o, offset, partial);
        if (ductTapeFixes) {
            _translate = _translate.multiply(bobOffsetScaleBy);
        }
        poseStack.translate(_translate.x, _translate.y, _translate.z);
    }

    public float xOrbit, zOrbit;
    public float xOrbit_o, zOrbit_o;
    public RenderPackage orbit(float x, float z){
        this.xOrbit = x;
        this.zOrbit = z;
        return this;
    }
    public void orbitPoseStack(PoseStack poseStack, float partial){
        this.orbitPoseStack(poseStack, null, partial);
    }
    public void orbitPoseStack(PoseStack poseStack, @Nullable Quaternionf q, float partial){
        q = q != null ? q : new Quaternionf();
        float xf = (float)Math.toRadians(lerp(xOrbit_o, xOrbit, partial));
        float zf = (float)Math.toRadians(lerp(zOrbit_o, zOrbit, partial));
        q.rotateXYZ(xf, 0, zf);
        poseStack.mulPose(q);
    }

    public float xRot, yRot, zRot = 0f;
    public float xRot_o, yRot_o, zRot_o = 0f;
    public RenderPackage rotate(float x, float y, float z){
        this.xRot = x;
        this.yRot = y;
        this.zRot = z;
        return this;
    }
    public void rotatePoseStack(PoseStack stack, float partial){
        this.rotatePoseStack(stack, null, partial);
    }
    public void rotatePoseStack(PoseStack poseStack, @Nullable Quaternionf q, float partial){
        q = q != null ? q : new Quaternionf();
        float x, y, z;
        x = (float)Math.toRadians(lerp(xRot_o, xRot, partial));
        y = (float)Math.toRadians(lerp(yRot_o, yRot, partial));
        z = (float)Math.toRadians(lerp(zRot_o, zRot, partial));
        q.rotateXYZ(x, y, z);
        poseStack.mulPose(q);
    }

    public static final Vec3 defaultScale = new Vec3(1, 1, 1);
    public Vec3 scale = defaultScale;
    public Vec3 scale_o = scale;
    public RenderPackage inflate(Vec3 scale){
        this.scale = scale;
        return this;
    }
    public void scalePoseStack(PoseStack poseStack, float partial){
        Vec3 _scale = lerp(scale_o, scale, partial);
        if (ductTapeFixes) {
            _scale = _scale.multiply(1, -1, 1);
            _scale = _scale.multiply(BobInflateScaleBy);
        }
        poseStack.scale((float)_scale.x, (float)_scale.y, (float)_scale.z);
    }

    public boolean remove = false;
    public RenderPackage flagForRemoval(){
        this.remove = true;
        return this;
    }
    public boolean shouldRemove(){
        return remove || checkRemoval();
    }
    protected boolean checkRemoval() {
        return false;
    }

    public RenderPackage addKeyframe(RenderPackageKeyframe kFrame){
        this.keyframeQue.add(kFrame);
        modified();
        return this;
    }
    public RenderPackage removeKeyframe(RenderPackageKeyframe kFrame){
        this.toRemove.add(kFrame);
        modified();
        return this;
    }
    private final ArrayList<RenderPackageKeyframe> keyframeQue = new ArrayList<>();
    private final ArrayList<RenderPackageKeyframe> toRemove = new ArrayList<>();

    private boolean keyframesModified;
    private void modified(){this.keyframesModified = true;}
    private final ArrayList<RenderPackageKeyframe> volatileKeyframes = new ArrayList<>();
    protected void clearKeyframeQues(){
        if (keyframesModified){
            volatileKeyframes.addAll(keyframeQue);
            volatileKeyframes.removeAll(toRemove);
            keyframeQue.clear();
            toRemove.clear();
            keyframesModified = false;
        }
    }
    public void stepKeyframes(){
        clearKeyframeQues();
        saveOldValues();
        volatileKeyframes.forEach((keyframe -> keyframe.attemptStep(this)));
    }
    protected void saveOldValues(){
        this.offset_o = this.offset;
        this.xOrbit_o = this.xOrbit; this.zOrbit_o = this.zOrbit;
        this.xRot_o = this.xRot; this.yRot_o = this.yRot; this.zRot_o = this.zRot;
        this.scale_o = scale;
    }

    public boolean ductTapeFixes = true;
    public RenderPackage disableFixes(){
        this.ductTapeFixes = false;
        return this;
    }
    public Vec3 bobOffsetScaleBy = new Vec3(100, 200, 100);
    public Vec3 BobInflateScaleBy = new Vec3(25, 25, 25);

    public static RenderPackage create(IGenericModel m){
        return new RenderPackage(m);
    }
    public RenderPackage(IGenericModel m){
        this.model = m;
    }

    public void modifyPoseStack(PoseStack poseStack, @Nullable Quaternionf orbit, @Nullable Quaternionf rotate, float partial){
        this.orbitPoseStack(poseStack, orbit, partial);
        this.translatePoseStack(poseStack, partial);
        this.rotatePoseStack(poseStack, rotate, partial);
        this.scalePoseStack(poseStack, partial);
    }

    public void render(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick,
                       Camera camera, boolean isFoggy, @Nullable Quaternionf orbit, @Nullable Quaternionf rotate){
        poseStack.pushPose();
        modifyPoseStack(poseStack, orbit, rotate, partialTick);
        getModel().renderToBuffer(poseStack, model.getVertexConsumer(),
                15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        poseStack.popPose();
    }

    public RenderPackage que(){
        SkyboxModelManager.que.add(this);
        return this;
    }

    private static float lerp(float a, float b, float partial){
        return a + (b - a) * partial;
    }
    private static double lerp(double a, double b, float partial){
        return a + (b - a) * partial;
    }
    private static Vec3 lerp(Vec3 a, Vec3 b, float partial){
        double x, y, z;
        x = lerp(a.x, b.x, partial);
        y = lerp(a.y, b.y, partial);
        z = lerp(a.z, b.z, partial);
        return new Vec3(x, y, z);
    }
}
