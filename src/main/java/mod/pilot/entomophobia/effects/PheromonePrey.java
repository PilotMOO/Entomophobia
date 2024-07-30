package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PheromonePrey extends PheromonesBase{
    public PheromonePrey() {
        super(99000, true, true, false, 0, 10, 1);
    }

    //TEMPORARY
    @Override
    protected void BaseEffectTick(LivingEntity target, int amp) {
        System.out.println("BaseEffectTick was called!");
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20), target);
    }
}
