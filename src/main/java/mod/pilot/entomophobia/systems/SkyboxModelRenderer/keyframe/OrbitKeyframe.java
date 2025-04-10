package mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe;

import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;

public class OrbitKeyframe extends RenderPackageKeyframe{
    public float x, z;
    public float duration;
    public float partial;
    public OrbitKeyframe(float x, float z, float duration){
        this.x = x; this.z = z;
        this.duration = duration;
        this.partial = 0;
    }
    @Override
    public boolean active() {
        return partial <= 1;
    }

    @Override
    public void step(RenderPackage _package) {
        _package.orbit(lerp(_package.xRot, x, partial),
                lerp(_package.zRot, z, partial));
        this.partial += 1 / (duration * 20);
    }

    public static float lerp(float a, float b, float partial){
        return a + (b - a) * partial;
    }
}
