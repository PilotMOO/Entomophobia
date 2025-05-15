package mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe;

import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;

public class LifetimeKeyframe extends RenderPackageKeyframe{
    public int lifetime;
    public LifetimeKeyframe(int lifetimeInTicks){
        this.lifetime = lifetimeInTicks;
    }
    @Override
    public boolean active() {
        return age >= lifetime;
    }
    @Override
    public void step(RenderPackage _package) {
        _package.flagForRemoval();
    }
}
