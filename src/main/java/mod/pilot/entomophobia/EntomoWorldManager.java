package mod.pilot.entomophobia;

import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.PheromonePrey;
import mod.pilot.entomophobia.effects.PheromonesBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class EntomoWorldManager {
    public static PheromonesEntityBase CreateNewPheromoneAt(EntityType<? extends PheromonesEntityBase> pheroType, Vec3 pos, Level world){
        PheromonesEntityBase phero = pheroType.create(world);
        assert phero != null;
        phero.setPos(pos);

        world.addFreshEntity(phero);

        return phero;
    }
    public static PheromonesEntityBase CreateNewPheromoneAt(EntityType<? extends PheromonesEntityBase> pheroType, LivingEntity parent){
        return CreateNewPheromoneAt(pheroType, parent.position(), parent.level());
    }

    private static final ArrayList<LivingEntity> AllPrey = new ArrayList<>();
    public static boolean IsThisPrey(LivingEntity target){
        PruneAllPrey();
        for (LivingEntity LE : AllPrey){
            if (LE == target){
                return true;
            }
        }
        return false;
    }

    private static void PruneAllPrey() {
        AllPrey.removeIf(target -> !target.hasEffect(EntomoMobEffects.PREY.get()) || target.isDeadOrDying());
    }

    public static boolean AddThisAsPrey(LivingEntity target){
        if (!IsThisPrey(target)){
            AllPrey.add(target);
            return true;
        }
        return false;
    }
    public static boolean RemoveThisAsPrey(LivingEntity target){
        if (IsThisPrey(target)){
            AllPrey.remove(target);
            return true;
        }
        return false;
    }
    public static LivingEntity GetNearbyPrey(MyiaticBase parent){
        PruneAllPrey();
        double PreySearchRange = parent.getAttribute(Attributes.FOLLOW_RANGE).getValue() + Config.SERVER.hunt_bonus_range.get();
        LivingEntity closestPrey = null;

        for (LivingEntity target : AllPrey){
            if (parent.TestValidEntity(target) && target.distanceTo(parent) < PreySearchRange){
                if (closestPrey != null){
                    if (target.distanceTo(parent) < closestPrey.distanceTo(parent)){
                        closestPrey = target;
                    }
                }
                else{
                    closestPrey = target;
                }
            }
        }

        return closestPrey;
    }

    public static MobEffectInstance ApplyPheromoneTo(PheromonesBase effect, LivingEntity target, int duration, int amp){
        MobEffectInstance instance = new MobEffectInstance(effect, duration, amp);
        if (effect.ApplicableToMyiatic && target instanceof MyiaticBase){
            target.addEffect(instance);
        }
        else if (effect.ApplicableToNon && !(target instanceof MyiaticBase)){
            target.addEffect(instance);
            if (effect instanceof PheromonePrey){
                AddThisAsPrey(target);
            }
        }

        return instance;
    }
}
