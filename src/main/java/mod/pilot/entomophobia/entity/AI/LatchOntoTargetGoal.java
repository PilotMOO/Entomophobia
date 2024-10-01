package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;

public class LatchOntoTargetGoal extends Goal {
    final MyiaticBase parent;
    final double Distance;
    final int CDMax;
    int CD;
    final int HitCDMax;
    int HitCD;
    final int HitAnimPos;
    int HitTicker;
    final int HitAnimLength;
    int PounceState;
    double PounceHSpeed;
    double PounceVSpeed;
    float PriorRot;
    LivingEntity latchedTarget = null;
    public LatchOntoTargetGoal(MyiaticBase parent, double distance, int CD, int HitCD, int hitAnimPos, int hitAnimLength, double pounceHSpeed, double pounceVSpeed){
        this.parent = parent;
        Distance = distance;
        CDMax = CD;
        this.CD = CD;
        HitCDMax = HitCD;
        this.HitCD = HitCD;
        HitAnimPos = hitAnimPos;
        HitAnimLength = hitAnimLength;
        PounceHSpeed = pounceHSpeed;
        PounceVSpeed = pounceVSpeed;
        PounceState = 0;
        HitTicker = 0;
    }
    @Override
    public boolean canUse() {
        return parent.getTarget() != null && !parent.isDeadOrDying();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        PriorRot = parent.getYRot();
        if (parent.getTarget() != null){
            switch (PounceState){
                case 2 -> Pounce();
                case 3 -> MidAirChecks();
                case 4 -> GrabOntoPrey();
                default -> PrePounceCheck();
            }
        }
        else{
            stop();
        }
    }

    protected void PrePounceCheck() {
        LivingEntity target = parent.getTarget();
        parent.lookAt(target, 180, 180);
        if (parent.distanceTo(target) < Distance && Mth.abs((float)(parent.position().y - target.position().y)) < 3){
            CD = CD > 0 ? CD - 1 : 0;
            if (CD == 0){
                PounceState = 2;
            }
        }
        else{
            PounceState = 0;
            CD = CDMax;
        }
    }
    protected void Pounce(){
        parent.setDeltaMovement(parent.getDeltaMovement().add(parent.getDirectionToTarget().multiply(PounceHSpeed, 0, PounceHSpeed)).add(0, CalculateVSpeed(), 0));
        CD = CDMax;
        PounceState = 3;
    }
    protected void MidAirChecks() {
        LivingEntity target = parent.getTarget();
        if (parent.distanceTo(target) < 1.5){
            if (target instanceof Player player){
                if (player.isBlocking()){
                    if (player.getOffhandItem().getItem() instanceof ShieldItem || player.getMainHandItem().getItem() instanceof ShieldItem){
                        player.disableShield(false);
                    }
                    parent.setDeltaMovement(parent.getDeltaMovement().reverse());
                    stop();
                }
                else{
                    latchedTarget = player;
                    PounceState = 4;
                }
            }
            else{
                latchedTarget = target;
                PounceState = 4;
            }
        }
        else if (parent.verticalCollision || parent.horizontalCollision || parent.isInWater()){
            stop();
        }
    }
    protected void GrabOntoPrey() {
        if (latchedTarget == parent.getTarget()){
            parent.lookAt(parent.getTarget(), 180, 180);
            parent.setPos(GetTargetLatchPos());
            parent.resetFallDistance();
            parent.setAIState(MyiaticBase.state.other.ordinal());
            StrikeAttachedTarget();
        }
        else{
            stop();
        }
    }
    protected void StrikeAttachedTarget() {
        HitCD = HitCD > 0 ? HitCD - 1 : 0;
        if (HitCD <= 0){
            HitTicker++;
            if (HitTicker == HitAnimPos){
                LivingEntity target = parent.getTarget();
                parent.doHurtTarget(target);
            }
            else if (HitTicker >= HitAnimLength){
                HitCD = HitCDMax;
                HitTicker = 0;
            }
        }
    }

    @Override
    public void stop() {
        PounceState = 1;
        CD = CDMax;
        HitCD = HitCDMax;
        HitTicker = 0;
        latchedTarget = null;
        parent.setAIState(MyiaticBase.state.idle.ordinal());
    }

    protected double CalculateVSpeed(){
        LivingEntity target = parent.getTarget();
        Vec3 targetPos = target.position();

        //Gets the absolute average between the distance of the target and the mob of both X and Z coords
        double AvgDistance2d = Mth.abs((float)((parent.position().x - targetPos.x) * (parent.position().z - targetPos.z))) / 2;
        //Gets the absolute Y distance between the target and the mob's eye height
        double YDistance = Mth.abs((float)(parent.position().y - target.getEyeY()));
        //Tan = Opposite over Adjacent. Trigonometry!
        double TanOfParentPoint = AvgDistance2d / YDistance;
        // Divides the Tangent by 100-- turning it into a decimal multipier to be used to convert the final output
        double DividedDifference = 100 / TanOfParentPoint;
        //Returns the smaller number-- if Tan was 90, DividedDifference would return 1.111...1 which is not exactly needed
        double Mulitplier = Math.min(1, DividedDifference);
        //Multiplies the multiper by the speed and returns it
        return PounceVSpeed * Mulitplier;
    }
    protected Vec3 GetTargetLatchPos(){
        return parent.getTarget().position().add(parent.getForward().reverse().multiply(parent.getTarget().getBbWidth() / 2, 0, parent.getTarget().getBbWidth() / 2)).add(0, parent.getTarget().getBbHeight() * 0.75, 0);
    }
}
