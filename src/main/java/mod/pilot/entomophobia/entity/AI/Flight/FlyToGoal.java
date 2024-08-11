package mod.pilot.entomophobia.entity.AI.Flight;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.sound.EntomoSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlyToGoal extends Goal {
    final MyiaticBase parent;
    Vec3 finalPos;
    int ActiveFlightTime;
    int FlightState;
    boolean IsFlying;
    int FlightCD;
    final int MaxFlightCD;
    final int MaxAscensionTime;
    final int MaxGlideTime;
    final int TargetHeightThreshold;
    double VFlightSpeed;
    double HFlightSpeed;
    enum FlightStates{
        Disabled,
        Landed,
        Ascending,
        Gliding,
        Falling
    }
    boolean CheckFly(){
        return FlightState != FlightStates.Disabled.ordinal() && !IsFlying && FlightCD <= 0 && WantsToTakeOff();
    }
    boolean WantsToTakeOff(){
        if (parent.getNavigation().getPath() != null && parent.getNavigation().getPath().getEndNode() != null){
            return parent.getNavigation().getPath().getEndNode().y - parent.position().y > TargetHeightThreshold;
        }
        return false;
    }
    boolean AmITooHigh(LivingEntity target){
        if (target != null){
            return parent.position().y - target.position().y > TargetHeightThreshold;
        }
        return false;
    }
    /**/

    public FlyToGoal(MyiaticBase parent, @Nullable Vec3 targetPos, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                     int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed){
        this.parent = parent;
        finalPos = targetPos;
        ActiveFlightTime = 0;
        FlightState = 1;
        IsFlying = false;
        FlightCD = 0;
        MaxFlightCD = maxFlightCD;
        MaxAscensionTime = maxAscensionTime;
        MaxGlideTime = maxGlideTime;
        TargetHeightThreshold = targetHeightThreshold;
        VFlightSpeed = vFlightSpeed / 1000;
        HFlightSpeed = hFlightSpeed / 1000;
    }
    public FlyToGoal(MyiaticBase parent, @Nullable LivingEntity target, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                     int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed){
        this.parent = parent;
        if (target != null){
            finalPos = target.position();
        }
        ActiveFlightTime = 0;
        FlightState = 1;
        IsFlying = false;
        FlightCD = 0;
        MaxFlightCD = maxFlightCD;
        MaxAscensionTime = maxAscensionTime;
        MaxGlideTime = maxGlideTime;
        TargetHeightThreshold = targetHeightThreshold;
        VFlightSpeed = vFlightSpeed;
        HFlightSpeed = hFlightSpeed;
    }

    //Goal-related Methods and overrides
    @Override
    public boolean canUse() {
        return finalPos != null;
    }
    @Override
    public void tick() {
        parent.getNavigation().moveTo(finalPos.x, finalPos.y, finalPos.z, 1);
        if (FlightState != FlightStates.Gliding.ordinal()){
            parent.getLookControl().setLookAt(finalPos);
        }
        if (FlightCD > 0){
            FlightCD--;
        }
        if (CheckFly()){
            StartFlyCycle();
        }
        if (IsFlying){
            FlightManager();
        }
        if (FlightState == FlightStates.Falling.ordinal() && parent.verticalCollisionBelow){
            ManageStateSwitch(FlightStates.Landed);
        }
    }

    @Override
    public void stop() {
        ManageStateSwitch(FlightStates.Landed);
    }
    /**/

    //General Use Methods
    protected void FlightManager(){
        parent.getLookControl().setLookAt(finalPos);
        switch (FlightState){
            case 2 -> Ascend();
            case 3 -> Glide();
            case 4 ->{
                if (parent.verticalCollisionBelow){
                    ManageStateSwitch(FlightStates.Landed);
                }
            }
        }
    }

    protected void StartFlyCycle(){
        parent.getLookControl().setLookAt(finalPos);
        ManageStateSwitch(FlightStates.Ascending);
        PlayFlySound();
    }
    protected void ManageStateSwitch(FlightStates flightStates){
        if (flightStates.ordinal() != FlightState){
            switch (flightStates.ordinal()){
                case 0 ->{
                    IsFlying = false;
                    FlightState = -1;
                    ActiveFlightTime = -1;
                    parent.setAIState(MyiaticBase.state.idle.ordinal());
                    parent.setNoGravity(false);
                }
                case 1 -> HandleLand(FlightState);
                case 2 ->{
                    IsFlying = true;
                    FlightState = 2;
                    ActiveFlightTime = MaxAscensionTime;
                    parent.setAIState(MyiaticBase.state.flying.ordinal());
                    parent.setNoGravity(true);
                }
                case 3 ->{
                    IsFlying = true;
                    FlightState = 3;
                    ActiveFlightTime = MaxGlideTime;
                    parent.setAIState(MyiaticBase.state.flying.ordinal());
                    parent.setNoGravity(false);
                }
                case 4 ->{
                    IsFlying = false;
                    FlightState = 4;
                    ActiveFlightTime = -1;
                    FlightCD = MaxFlightCD;
                    parent.setAIState(MyiaticBase.state.flying.ordinal());
                    parent.setNoGravity(false);
                }
            }
        }
    }

    protected void Ascend(){
        if (ActiveFlightTime > 0 && !AmITooHigh(parent.getTarget())){
            double hSpeed = CalculateSpeed();
            Vec3 forwards = parent.getDirectionTo(finalPos).multiply(hSpeed, 0, hSpeed);
            double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
            double ySpeedMax = Mth.abs((float)parent.getDeltaMovement().y) > VFlightSpeed ? parent.getDeltaMovement().y : VFlightSpeed;
            double zSpeedMax = Mth.abs((float)parent.getDeltaMovement().z) > Mth.abs((float)forwards.z) ? parent.getDeltaMovement().z : forwards.z;
            parent.setDeltaMovement(xSpeedMax, ySpeedMax, zSpeedMax);
            ActiveFlightTime--;
        }
        else{
            ManageStateSwitch(FlightStates.Gliding);
        }
    }
    protected void Glide(){
        if (!parent.verticalCollisionBelow){
            if (ActiveFlightTime > 0){
                double hSpeed = CalculateSpeed();
                Vec3 forwards = parent.getDirectionTo(finalPos).multiply(hSpeed, 0, hSpeed);
                double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
                double ySpeedMax = -VFlightSpeed / 2;
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
    protected void HandleLand(int priorState){
        IsFlying = false;
        FlightState = 1;
        ActiveFlightTime = MaxAscensionTime;
        FlightCD = MaxFlightCD;
        parent.setAIState(MyiaticBase.state.idle.ordinal());
        parent.setNoGravity(false);

        if (priorState == FlightStates.Falling.ordinal()){
            parent.causeFallDamage(parent.fallDistance, 1.25f, parent.damageSources().fall());
        }
        else{
            parent.resetFallDistance();
        }
    }

    protected double CalculateSpeed(){
        //Gets the absolute average between the distance of the target and the mob of both X and Z coords
        double AvgDistance2d = Mth.abs((float)((parent.position().x - finalPos.x) * (parent.position().z - finalPos.z))) / 2;
        //Gets the absolute distance between the target and the mob
        double YDistance = Mth.abs((float)(parent.position().y - finalPos.y));
        //Tan = Opposite over Adjacent. Trigonometry!
        double TanOfParentPoint = AvgDistance2d / YDistance;
        // Divides the Tangent by 100-- turning it into a decimal multipier to be used to convert the final output
        double DividedDifference = 100 / TanOfParentPoint;
        //Returns the smaller number-- if Tan was 90, DividedDifference would return 1.111...1 which is not exactly needed
        double Mulitplier = Math.min(1, DividedDifference);
        //Multiplies the multiper by the speed and returns it
        return HFlightSpeed * Mulitplier;
    }
    protected void PlayFlySound(){
        parent.level().playSound(parent, parent.blockPosition(), EntomoSounds.MYIATIC_FLYING.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
    }
    /**/
}
