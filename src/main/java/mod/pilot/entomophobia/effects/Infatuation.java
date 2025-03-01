package mod.pilot.entomophobia.effects;

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


public class Infatuation extends MobEffect {
    public Infatuation() {
        super(MobEffectCategory.NEUTRAL, 13697266);
    }
    public static @Nullable CelestialCarrionEntity getClosestCarrion(LivingEntity target){
        return getClosestCarrion(target.position(), target.level(), (int)target.getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public static @Nullable CelestialCarrionEntity getClosestCarrion(Vec3 pos, Level level, int checkDist){
        CelestialCarrionEntity closest = null;
        double distance = Double.MAX_VALUE;
        for (CelestialCarrionEntity c : level.getEntitiesOfClass(CelestialCarrionEntity.class,
                AABB.ofSize(pos, checkDist, checkDist, checkDist))){
            double dist1 = c.distanceToSqr(pos);
            if (dist1 < distance){
                closest = c;
                distance = dist1;
            }
        }
        return closest;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        if (target instanceof Player) return;
        if (target instanceof Mob mob){
            mob.setTarget(null);
            if ((mob.tickCount % 20 == 0 || mob.getNavigation().isDone())) {
                CelestialCarrionEntity c = getClosestCarrion(mob);
                if (c != null && mob.distanceTo(c) > 6) {
                    mob.getNavigation().moveTo(c, 0.75);
                    mob.removeFreeWill();
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int amp) {
        return pDuration > 0;
    }
}
