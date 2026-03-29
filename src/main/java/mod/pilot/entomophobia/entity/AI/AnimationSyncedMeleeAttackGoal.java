package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class AnimationSyncedMeleeAttackGoal extends Goal {
    public AnimationSyncedMeleeAttackGoal(MyiaticBase parent,
                                          int animationLength, int hitPos,
                                          int cooldown, boolean alwaysFollow){
        this.parent = parent;
        this.trackIfLostLineOfSight = alwaysFollow;
        this.attackAnimation = animationLength;
        this.hitPosition = hitPos;
        this.cooldownDuration = cooldown;
    }
    public final MyiaticBase parent;
    public long lastCanUse;
    public Path path;

    public boolean isAttacking;
    public final int attackAnimation;
    public int attackTimer;
    public final int hitPosition;
    public final int cooldownDuration;
    public int cd;

    boolean trackIfLostLineOfSight;

    @Override
    public boolean requiresUpdateEveryTick() {return true;}

    @Override
    public boolean canUse() {
        long i = parent.level().getGameTime();
        if (i - this.lastCanUse < 20L) return false;
        else {
            this.lastCanUse = i;
            LivingEntity target = parent.getTarget();
            if (target == null || !target.isAlive()) return false;
            else{
                path = parent.getNavigation().createPath(target, 0);
                return path != null || (isAttacking = parent.distanceToSqr(target) <= getMeleeRangeSqr());
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (isAttacking) return true;
        LivingEntity target = parent.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (!trackIfLostLineOfSight) return !parent.getNavigation().isDone();
        if (target instanceof Player player){
            return !(player.isSpectator() || player.isCreative());
        } else return true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public double getMeleeRangeSqr(){
        double width = parent.getBbWidth();
        width *= width;
        double range = parent.getReach();
        return width + (range * range);
    }
}
