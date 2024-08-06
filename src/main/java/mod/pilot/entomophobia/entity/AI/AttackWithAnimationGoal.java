package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class AttackWithAnimationGoal extends MeleeAttackGoal {
    final int AnimLength;
    boolean CurrentlyAttacking;
    int AttackTicker = 0;
    final int StrikePos;
    final MyiaticBase mob;
    public AttackWithAnimationGoal(MyiaticBase pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, int strikePos, int SwingAnimationLength) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        mob = pMob;
        StrikePos = strikePos;
        AnimLength = SwingAnimationLength;
    }

    @Override
    public boolean canUse() {
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {

    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = mob.getTarget();
        if (target != null) {
            if(mob.distanceTo(target) <= getAttackReachSqr(target)){
                CurrentlyAttacking = true;
            }
        }
        if (CurrentlyAttacking){
            mob.setAIState(MyiaticBase.state.attacking.ordinal());
            AttackTicker++;
            if (target != null) {
                if (StrikePos == AttackTicker && mob.distanceTo(target) < getAttackReachSqr(target)){
                    mob.doHurtTarget(target);
                }
            }
            if (AttackTicker >= AnimLength){
                FinalizeAttack();
            }
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return super.getAttackReachSqr(pAttackTarget) + mob.getReach();
    }

    @Override
    public void stop() {
        FinalizeAttack();
        super.stop();
    }

    void FinalizeAttack(){
        AttackTicker = 0;
        CurrentlyAttacking = false;
        mob.setAIState(MyiaticBase.state.idle.ordinal());
    }
}