package mod.pilot.entomophobia.entity.AI.Flight;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;

public class GlideDownToFoesGoal extends FlyToHostileTargetGoal{
    public GlideDownToFoesGoal(MyiaticBase parent, int maxGlideTime, int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
        super(parent, 0, 0, maxGlideTime, targetHeightThreshold, vFlightSpeed, hFlightSpeed);
    }

    @Override
    boolean wantsToTakeOff() {
        if (parent.getTarget() == null) return false;
        return FlightState != FlightStates.Disabled.ordinal() && parent.getTarget().blockPosition().getY() + 3 < parent.blockPosition().getY() && !isFlying && FlightCD == 0;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && parent.distanceTo(parent.getTarget()) < 10 && !parent.isInFluidType();
    }

    @Override
    protected void startFlyCycle() {
        parent.lookAt(parent.getTarget(), parent.getMaxHeadYRot(), parent.getMaxHeadXRot());
        manageStateSwitch(FlightStates.Gliding);
    }
}