package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class PreyPriorityNearestAttackable extends NearestAttackableTargetGoal<LivingEntity> {
    final MyiaticBase mob;
    boolean wasPrey = false;
    public PreyPriorityNearestAttackable(MyiaticBase parent, boolean pMustSee) {
        super(parent, LivingEntity.class, pMustSee, parent::TestValidEntity);
        mob = parent;
    }

    @Override
    public void tick() {
        super.tick();
        if (wasPrey && !target.hasEffect(EntomoMobEffects.PREY.get())){
            findTarget();
            this.mob.setTarget(this.target);
        }
    }

    @Override
    protected void findTarget() {
        LivingEntity prey = mob.getClosestPrey();
        if (prey != null && mob.hasEffect(EntomoMobEffects.HUNT.get())){
            this.target = prey;
            wasPrey = true;
        }
        else{
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            if (this.target == null) {
                this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType,
                                this.getTargetSearchArea(this.getFollowDistance()), mob::TestValidEntity),
                        this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                wasPrey = false;
            }
        }
    }
}
