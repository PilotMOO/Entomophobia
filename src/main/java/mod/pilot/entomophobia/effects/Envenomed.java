package mod.pilot.entomophobia.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class Envenomed extends MobEffect {
    public Envenomed() {
        super(MobEffectCategory.HARMFUL, 13697266);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amp) {
        int j = 25 >> amp;
        return j == 0 || duration % j == 0;
    }
}
