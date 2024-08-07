package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class DashAttackWithAnimationGoal extends AttackWithAnimationGoal {
    final double DashVelocity;
    public DashAttackWithAnimationGoal(MyiaticBase parent, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, int CD, int strikePos, int SwingAnimationLength, double dashVelocity) {
        super(parent, pSpeedModifier, pFollowingTargetEvenIfNotSeen, CD, strikePos, SwingAnimationLength);
        DashVelocity = dashVelocity;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = mob.getTarget();
        if (CurrentlyAttacking && target != null){
            if (StrikePos == AttackTicker && mob.distanceTo(target) < getAttackReachSqr(target)){
                mob.setDeltaMovement(mob.getDeltaMovement().add(mob.getForward().multiply(DashVelocity, 0, DashVelocity)));
            }
        }
    }
}