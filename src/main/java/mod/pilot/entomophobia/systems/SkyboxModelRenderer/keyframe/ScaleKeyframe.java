package mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe;

import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;
import net.minecraft.world.phys.Vec3;

public class ScaleKeyframe extends RenderPackageKeyframe{
    public double x, y, z;
    public float duration;
    public float partial;
    public Vec3 getAsVector(){
        return new Vec3(x, y, z);
    }
    public ScaleKeyframe(Vec3 position, float duration){
        this(position.x, position.y, position.z, duration);
    }
    public ScaleKeyframe(double x, double y, double z, float duration){
        this.x = x; this.y = y; this.z = z;
        this.duration = duration;
        this.partial = 0;
    }
    @Override
    public boolean active() {
        return partial <= 1;
    }

    @Override
    public void step(RenderPackage _package) {
        _package.inflate(lerpVectors(_package.scale, getAsVector(), partial));
        this.partial += 1 / (duration * 20);
    }

    public static double lerp(double a, double b, float partial){
        return a + (b - a) * partial;
    }
    public static Vec3 lerpVectors(Vec3 a, Vec3 b, float partial){
        double x, y, z;
        x = lerp(a.x, b.x, partial);
        y = lerp(a.y, b.y, partial);
        z = lerp(a.z, b.z, partial);
        return new Vec3(x, y, z);
    }
}
