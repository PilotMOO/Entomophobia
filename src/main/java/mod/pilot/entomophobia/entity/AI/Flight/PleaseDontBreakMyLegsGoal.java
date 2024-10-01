package mod.pilot.entomophobia.entity.AI.Flight;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PleaseDontBreakMyLegsGoal extends FlyToGoal{
    public PleaseDontBreakMyLegsGoal(MyiaticBase parent, int maxGlideTime, int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
        super(parent, parent.position().add(parent.getForward().multiply(hFlightSpeed, 0, hFlightSpeed)), 0, 0, maxGlideTime, targetHeightThreshold, vFlightSpeed, hFlightSpeed);
    }

    @Override
    public boolean canUse() {
        return !IsFlying && FlightState != FlightStates.Falling.ordinal() && parent.fallDistance > 5 && !parent.isInFluidType();
    }

    @Override
    public boolean canContinueToUse() {
        return FlightState != FlightStates.Landed.ordinal();
    }

    @Override
    boolean WantsToTakeOff() {
        return FlightState == FlightStates.Landed.ordinal() && parent.fallDistance > parent.getMaxFallDistance();
    }

    @Override
    protected void StartFlyCycle() {
        parent.lookAt(EntityAnchorArgument.Anchor.EYES, finalPos);
        ManageStateSwitch(FlightStates.Gliding);
    }

    @Override
    protected void Glide() {
        if (!parent.verticalCollisionBelow){
            if (ActiveFlightTime > 0){
                double hSpeed = CalculateSpeed();
                Vec3 forwards = parent.getDirectionTo(finalPos).multiply(hSpeed, 0, hSpeed);
                double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
                double ySpeedMax = Math.min(parent.getDeltaMovement().y / 2, -VFlightSpeed * 4);
                double zSpeedMax = Mth.abs((float)parent.getDeltaMovement().z) > Mth.abs((float)forwards.z) ? parent.getDeltaMovement().z : forwards.z;
                parent.setDeltaMovement(xSpeedMax, ySpeedMax, zSpeedMax);
                ActiveFlightTime--;
                parent.resetFallDistance();
            }
            else{
                ManageStateSwitch(FlightStates.Falling);
            }
        }
        else{
            ManageStateSwitch(FlightStates.Landed);
        }
        if (parent.horizontalCollision){
            ManageStateSwitch(FlightStates.Falling);
        }
    }

    @Override
    protected double CalculateSpeed() {
        return HFlightSpeed;
    }
}
