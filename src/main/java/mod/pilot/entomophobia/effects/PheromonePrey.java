package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.EntomoWorldManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class PheromonePrey extends PheromonesBase {
    public PheromonePrey() {
        super(-15826930, true, true, false, 0, 10, 1);
    }
}
