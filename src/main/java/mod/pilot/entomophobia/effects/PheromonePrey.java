package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.EntomoWorldManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class PheromonePrey extends PheromonesBase {
    public PheromonePrey() {
        super(-5763570, true, true, false, 0, 10, 1);
    }

    //  temp
    @Override
    protected void BaseEffectTick(LivingEntity target, int amp) {
        if (target.isDeadOrDying()){
            EntomoWorldManager.RemoveThisAsPrey(target);
        }
    }
}
