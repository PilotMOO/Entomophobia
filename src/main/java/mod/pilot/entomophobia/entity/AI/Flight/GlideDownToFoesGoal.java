package mod.pilot.entomophobia.entity.AI.Flight;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;

public class GlideDownToFoesGoal extends FlyToHostileTargetGoal{
    public GlideDownToFoesGoal(MyiaticBase parent, int maxGlideTime, int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
        super(parent, 0, 0, maxGlideTime, targetHeightThreshold, vFlightSpeed, hFlightSpeed);
    }

    @Override
    boolean WantsToTakeOff() {
        return FlightState != FlightStates.Disabled.ordinal() && parent.getTarget().blockPosition().getY() + 3 < parent.blockPosition().getY() && !IsFlying && FlightCD == 0;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && parent.distanceTo(parent.getTarget()) < 10;
    }

    @Override
    protected void StartFlyCycle() {
        parent.getLookControl().setLookAt(parent.getTarget());
        ManageStateSwitch(FlightStates.Gliding);
    }
}