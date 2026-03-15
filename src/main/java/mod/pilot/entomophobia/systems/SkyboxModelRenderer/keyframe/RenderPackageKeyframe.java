package mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe;

import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderPackageKeyframe {
    public int age;
    public abstract boolean active();
    public final void attemptStep(RenderPackage _package){
        ++age;
        if (active()) step(_package);
    }
    public abstract void step(RenderPackage _package);
}