package mod.pilot.entomophobia.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public abstract class StackingEffectBase extends MobEffect {
    protected StackingEffectBase(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration > 0;
    }
}
