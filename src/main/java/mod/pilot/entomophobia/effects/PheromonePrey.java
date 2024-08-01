package mod.pilot.entomophobia.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PheromonePrey extends PheromonesBase {
    public PheromonePrey() {
        super(255255255, true, true, false, 0, 10, 1);
    }

    //  temp
    @Override
    protected void BaseEffectTick(LivingEntity target, int amp) {
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20));
    }
}
