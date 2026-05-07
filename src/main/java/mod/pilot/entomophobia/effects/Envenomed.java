package mod.pilot.entomophobia.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class Envenomed extends MobEffect implements IStackingEffect {
    public Envenomed() {
        super(MobEffectCategory.HARMFUL, 5975808);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        float hp;
        int damage = 1 + (amp >> 2);
        if ((hp = target.getHealth()) <= damage) damage = (int)(hp - 1);
        if (damage > 0) {
            int iframe = target.invulnerableTime;
            target.invulnerableTime = 0;
            target.hurt(target.damageSources().magic(), damage);
            target.invulnerableTime = iframe;
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amp) {
        int j = 30 >> amp;
        return j == 0 || duration % (j + 2) == 0;
    }

    @Override
    public int getWrapAroundThreshold() {
        return 100;
    }

    @Override
    public int getMinimumWrapDuration() {
        return 10;
    }

    @Override
    public int getDegradeDuration() {
        return 60;
    }

    @Override
    public boolean isDegradable() {
        return true;
    }

    @Override
    public int getMaxCap() {
        return 6;
    }
}
