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
    boolean wantsToTakeOff() {
        if (parent.getTarget() == null) return false;
        return isMyTargetTooHigh(parent.getTarget()) || parent.distanceTo(parent.getTarget()) > 20;
    }
    @Override
    boolean checkFly(){
        return FlightState != FlightStates.Disabled.ordinal() && !isFlying && FlightCD <= 0 && wantsToTakeOff();
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
        if (parent.getTarget() != null && FlightState != FlightStates.Gliding.ordinal()){
            parent.lookAt(parent.getTarget(), parent.getMaxHeadYRot(), parent.getMaxHeadXRot());
            parent.getLookControl().tick();
        }
        if (FlightCD > 0){
            FlightCD--;
        }
        if (checkFly() && parent.getLookControl().isLookingAtTarget()){
            startFlyCycle();
        }
        if (isFlying){
            flightManager();
            strikeWhileGliding(parent.getTarget());
        }
        if (FlightState == FlightStates.Falling.ordinal() && parent.verticalCollisionBelow){
            manageStateSwitch(FlightStates.Landed);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (parent.getTarget() != null){
            parent.getNavigation().moveTo(parent.getTarget(), 1);
        }
    }

    @Override
    protected void handleLand(int priorState) {
        super.handleLand(priorState);
        if (parent.getTarget() != null){
            parent.getNavigation().moveTo(parent.getTarget(), 1.0d);
        }
    }

    @Override
    protected void ascend() {
        super.ascend();
        checkThenDropIfClear(parent.getTarget());
    }

    @Override
    protected void glide() {
        super.glide();
        checkThenDropIfClear(parent.getTarget());
    }
    @Override
    protected double calculateSpeed() {
        if (isMyTargetTooHigh(parent.getTarget())){
            return super.calculateSpeed();
        }
        return HFlightSpeed * 1.25;
    }

    //Subclass-Specific Methods
    boolean isMyTargetTooHigh(LivingEntity target){
        if (target != null){
            return target.position().y - parent.position().y > TargetHeightThreshold;
        }
        return false;
    }
    protected void strikeWhileGliding(LivingEntity target){
        if (target != null){
            double range = parent.getBbWidth() * 2.0F * parent.getBbWidth() * 2.0F + target.getBbWidth();
            if (parent.distanceTo(target) < range){
                parent.doHurtTarget(target);
            }
        }
    }
    protected boolean checkThenDropIfClear(LivingEntity target){
        if (target != null){
            double distanceV = parent.position().y - target.position().y;
            if (distanceV > 0 && distanceV < TargetHeightThreshold){
                float distance2d = new Vec2((float)parent.position().x, (float)parent.position().z).distanceToSqr(new Vec2((float)target.position().x, (float)target.position().z));
                if ((Mth.sqrt(distance2d) < 1.5) || parent.isThereABlockUnderMe(TargetHeightThreshold) && Mth.sqrt(distance2d) < 3){
                    manageStateSwitch(FlightStates.Landed);
                    parent.setNoGravity(false);
                    parent.setDeltaMovement(0, 0, 0);
                    return true;
                }
            }
        }
        return false;
    }
}
