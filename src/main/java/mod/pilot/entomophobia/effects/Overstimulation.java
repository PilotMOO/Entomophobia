package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class Overstimulation extends MobEffect implements IStacking {
    protected Overstimulation() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        if (target instanceof Player player){
            player.causeFoodExhaustion((float)((amp + 1) * 0.25));
        }

        if (target.tickCount % 10 == 0){
            target.hurt(EntomoDamageTypes.overstimulation(target), 2 + amp);
        }

        int duration = target.getEffect(this).getDuration();
        if (duration <= 1 && duration != -1){
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 200 * (amp + 1), amp));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400 * (amp + 1), 2 + amp));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400 * (amp + 1), 1 + amp));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amp) {
        return duration > 0;
    }

    @Override
    public int getWrapAroundThreshold() {
        return 400;
    }
    @Override
    public int getMinimumWrapDuration() {
        return 100;
    }
}
