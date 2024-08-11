package mod.pilot.entomophobia.entity.AI.Flight;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;

public class FlyToHostileTargetGoal extends FlyToGoal{
    public FlyToHostileTargetGoal(MyiaticBase parent, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                                  int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
        super(parent, parent.getTarget(), maxFlightCD, maxAscensionTime, maxGlideTime,
                targetHeightThreshold, vFlightSpeed, hFlightSpeed);
    }
    @Override
    boolean WantsToTakeOff() {
        return IsMyTargetTooHigh(parent.getTarget()) || parent.distanceTo(parent.getTarget()) > 20;
    }
    @Override
    boolean CheckFly(){
        return FlightState != FlightStates.Disabled.ordinal() && !IsFlying && FlightCD <= 0 && WantsToTakeOff();
    }

    @Override
    public boolean canUse() {
        if (parent.getTarget() != null){
            super.finalPos = parent.getTarget().position();
        }
        return super.canUse() && parent.getTarget() != null;
    }

    @Override
    public void tick() {
        parent.getNavigation().moveTo(parent.getTarget(), 1);
        if (FlightState != FlightStates.Gliding.ordinal()){
            parent.getLookControl().setLookAt(parent.getTarget());
        }
        if (FlightCD > 0){
            FlightCD--;
        }
        if (CheckFly() && parent.getLookControl().isLookingAtTarget()){
            StartFlyCycle();
        }
        if (IsFlying){
            FlightManager();
            StrikeWhileGliding(parent.getTarget());
        }
        if (FlightState == FlightStates.Falling.ordinal() && parent.verticalCollisionBelow){
            ManageStateSwitch(FlightStates.Landed);
        }
    }

    @Override
    protected void HandleLand(int priorState) {
        super.HandleLand(priorState);
        if (parent.getTarget() != null){
            parent.getNavigation().moveTo(parent.getTarget(), 1.0d);
        }
    }

    @Override
    protected void Ascend() {
        super.Ascend();
        CheckThenDropIfClear(parent.getTarget());
    }

    @Override
    protected void Glide() {
        super.Glide();
        CheckThenDropIfClear(parent.getTarget());
    }
    @Override
    protected double CalculateSpeed() {
        if (IsMyTargetTooHigh(parent.getTarget())){
            return super.CalculateSpeed();
        }
        return HFlightSpeed * 1.25;
    }

    //Subclass-Specific Methods
    boolean IsMyTargetTooHigh(LivingEntity target){
        if (target != null){
            return target.position().y - parent.position().y > TargetHeightThreshold;
        }
        return false;
    }
    protected void StrikeWhileGliding(LivingEntity target){
        if (target != null){
            double range = parent.getBbWidth() * 2.0F * parent.getBbWidth() * 2.0F + target.getBbWidth();
            if (parent.distanceTo(target) < range){
                parent.doHurtTarget(target);
            }
        }
    }
    protected boolean CheckThenDropIfClear(LivingEntity target){
        if (target != null){
            double distanceV = parent.position().y - target.position().y;
            if (distanceV > 0 && distanceV < TargetHeightThreshold){
                float distance2d = new Vec2((float)parent.position().x, (float)parent.position().z).distanceToSqr(new Vec2((float)target.position().x, (float)target.position().z));
                if ((Mth.sqrt(distance2d) < 1.5) || parent.isThereABlockUnderMe(TargetHeightThreshold) && Mth.sqrt(distance2d) < 3){
                    ManageStateSwitch(FlightStates.Landed);
                    parent.setNoGravity(false);
                    parent.setDeltaMovement(0, 0, 0);
                    return true;
                }
            }
        }
        return false;
    }
}
