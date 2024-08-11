package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class AttackWithAnimationGoal extends MeleeAttackGoal {
    final int AnimLength;
    boolean CurrentlyAttacking;
    int AttackTicker = 0;
    final int StrikePos;
    final MyiaticBase mob;
    final int MaxCD;
    int CD;
    public AttackWithAnimationGoal(MyiaticBase pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, int CD, int strikePos, int SwingAnimationLength) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        mob = pMob;
        StrikePos = strikePos;
        AnimLength = SwingAnimationLength;
        MaxCD = CD;
        this.CD = 0;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && mob.getAIState() != MyiaticBase.state.flying.ordinal() && mob.getAIState() != MyiaticBase.state.other.ordinal();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {

    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = mob.getTarget();
        CD = CD > 0 ? CD - 1 : 0;
        if (target != null) {
            if(mob.distanceTo(target) <= getAttackReach(target) && CD <= 0){
                CurrentlyAttacking = true;
            }
        }
        if (CurrentlyAttacking){
            mob.setAIState(MyiaticBase.state.attacking.ordinal());
            AttackTicker++;
            if (target != null) {
                if (StrikePos == AttackTicker && mob.distanceTo(target) < getAttackReach(target)){
                    mob.doHurtTarget(target);
                }
            }
            if (AttackTicker >= AnimLength){
                FinalizeAttack();
            }
        }
    }

    protected double getAttackReach(LivingEntity pAttackTarget) {
        return Mth.sqrt((float)getAttackReachSqr(pAttackTarget)) + mob.getReach();
    }

    @Override
    public void stop() {
        FinalizeAttack();
        super.stop();
    }

    void FinalizeAttack(){
        AttackTicker = 0;
        CD = mob.hasEffect(EntomoMobEffects.FRENZY.get()) ? MaxCD / 2 : MaxCD;
        CurrentlyAttacking = false;
        mob.setAIState(MyiaticBase.state.idle.ordinal());
    }
}