package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.worlddata.EntomoWorldManager;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class PheromoneExplodeGoal extends Goal {
    final MyiaticBase parent;
    final int PheromoneSearchRange;
    final int MyiaticSearchRange;
    final int TargetSearchRange;
    final int MaxFuseTimer;
    int FuseTimer;
    Predicate<MyiaticBase> WantsToExplodePredicate;
    boolean isExploding = false;

    public PheromoneExplodeGoal(MyiaticCreeperEntity parent, int pheromoneSearchRange, int myiaticSearchRange, int targetSearchRange, int fuseTimer){
        this.parent = parent;
        PheromoneSearchRange = pheromoneSearchRange;
        MyiaticSearchRange = myiaticSearchRange;
        TargetSearchRange = targetSearchRange;
        WantsToExplodePredicate = null;
        MaxFuseTimer = fuseTimer;
        FuseTimer = 0;
    }
    public PheromoneExplodeGoal(MyiaticBase parent, int fuseTimer, Predicate<MyiaticBase> wantsToExplodePredicate){
        this.parent = parent;
        PheromoneSearchRange = 0;
        MyiaticSearchRange = 0;
        TargetSearchRange = 0;
        MaxFuseTimer = fuseTimer;
        FuseTimer = 0;
        WantsToExplodePredicate = wantsToExplodePredicate;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return !parent.hasEffect(EntomoMobEffects.FRENZY.get()) && !parent.isThereAPheromoneOfTypeXNearby(EntomoEntities.FRENZY.get(), 128);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() || isExploding;
    }

    @Override
    public void tick() {
        if (!isExploding){
            if (parent instanceof MyiaticCreeperEntity){
                if (((MyiaticCreeperEntity)parent).WantsToExplode(PheromoneSearchRange, MyiaticSearchRange, TargetSearchRange)){
                    ExplosionPrecheck();
                }
            }
            else if (WantsToExplodePredicate.test(parent)){
                ExplosionPrecheck();
            }
        }
        else{
            AttemptToExplode();
        }
    }

    protected void ExplosionPrecheck(){
        LivingEntity target = parent.getTarget();
        if (target != null){
            parent.getNavigation().moveTo(target, 1.25D);
            if (parent.distanceTo(target) < 3){
                AttemptToExplode();
                parent.level().playSound(parent, parent.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 2.0f, 0.75f);
            }
        }
        else{
            MyiaticBase closestMyiatic = parent.getClosestMyiatic();
            if (closestMyiatic != null){
                parent.getNavigation().moveTo(closestMyiatic, 1.25D);
                if (parent.distanceTo(closestMyiatic) < 3){
                    AttemptToExplode();
                    parent.level().playSound(parent, parent.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 2.0f, 0.75f);
                }
            }
        }
    }

    protected void AttemptToExplode(){
        isExploding = true;
        parent.setAIState(MyiaticBase.state.other.ordinal());
        FuseTimer++;
        parent.getNavigation().moveTo(parent, 0.0D);
        if (FuseTimer >= MaxFuseTimer){
            parent.level().explode(parent, EntomoDamageTypes.myiatic_explode(parent), new ExplosionDamageCalculator(), parent.position(),
                    Config.SERVER.myiatic_creeper_explode_radius.get(), false, Level.ExplosionInteraction.MOB);
            EntomoWorldManager.CreateNewEntityAt(EntomoEntities.FRENZY.get(), parent);
            parent.remove(Entity.RemovalReason.KILLED);
            stop();
        }
    }

    @Override
    public void stop() {
        FuseTimer = 0;
        isExploding = false;
    }
}
