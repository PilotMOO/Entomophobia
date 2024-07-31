package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PheromoneHunt extends PheromonesBase{
    public PheromoneHunt() {
        super(3186700, true, false, true, 10, 0, 1);
    }

    //TEMPORARY
    @Override
    protected void MyiaticEffectTick(LivingEntity target, int amp) {
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
    }
}
