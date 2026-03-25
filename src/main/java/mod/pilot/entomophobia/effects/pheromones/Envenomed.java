package mod.pilot.entomophobia.effects.pheromones;

import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
