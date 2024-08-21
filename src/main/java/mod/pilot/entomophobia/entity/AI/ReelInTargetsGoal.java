package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.projectile.AbstractGrappleProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class ReelInTargetsGoal extends Goal {
    final MyiaticBase parent;
    Entity ReelTarget;
    int ReelDistance;
    double ReelSpeed;
    final int ReelMaxTime;
    int ReelTime;
    final int MaxShootCD;
    int ShootCD;
    AbstractGrappleProjectile grapple;

    public ReelInTargetsGoal(MyiaticBase parent, int reelDistance, double reelSpeed, int reelMaxTime, int ShootCD){
        this.parent = parent;
        ReelDistance = reelDistance;
        ReelSpeed = reelSpeed;
        ReelMaxTime = reelMaxTime;
        ReelTime = 0;
        MaxShootCD = ShootCD;
        this.ShootCD = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = parent.getTarget();
        if (target != null){
            double distance = parent.distanceTo(target);
            return (distance > 10 || parent.getY() < target.getY() - 5) && distance < ReelDistance && parent.hasLineOfSight(target);
        }
        return false;
    }

    @Override
    public void tick() {
        if (grapple == null){
            if (parent.getTarget() != null){
                parent.getLookControl().setLookAt(parent.getTarget());
                parent.getLookControl().tick();
                parent.getNavigation().moveTo(parent, 1.0);
                ShootCD = ShootCD > 0 ? ShootCD - 1 : 0;
                if (ShootCD == 0){
                    FireGrapple();
                    ShootCD = MaxShootCD;
                }
            }
            else{
                stop();
            }
        }
        else if(grapple.isRemoved()){
            grapple = null;
        }
        else if(grapple.isGrappled() && grapple.isOfGrappleType(AbstractGrappleProjectile.GrappledTypes.Entity) && grapple.getTarget() != null){
            ReelTarget = grapple.getTarget();
            parent.setAIState(MyiaticBase.state.other);
            parent.getLookControl().setLookAt(ReelTarget);
            parent.getLookControl().tick();
            if (ReelTarget.distanceTo(parent) < 2){
                stop();
            }
        }

        if (grapple == null && ReelTarget != null){
            stop();
        }
    }
    protected void FireGrapple() {
        grapple = EntomoEntities.STRING_GRAPPLE.get().create(parent.level());
        parent.level().addFreshEntity(grapple);
        grapple.shoot(parent.getDirectionToTarget(), 2, 0, parent, ReelSpeed, ReelMaxTime);
    }

    @Override
    public void stop() {
        ReelTarget = null;
        if (grapple != null){
            grapple.ReelGrappleBack();
        }
        parent.setAIState(MyiaticBase.state.idle);
    }
}
