package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSpiderEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class PounceOnTargetGoal extends Goal {
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
    final int PounceAnimLength;
    int PounceTimer;
    double PounceHSpeed;
    public PounceOnTargetGoal(MyiaticBase parent, double distance, int CD, int HitCD, int hitAnimPos, int hitAnimLength, int pounceAnimLength, double pounceHSpeed){
        this.parent = parent;
        Distance = distance;
        CDMax = CD;
        this.CD = CD;
        HitCDMax = HitCD;
        this.HitCD = HitCD;
        HitAnimPos = hitAnimPos;
        HitAnimLength = hitAnimLength;
        PounceAnimLength = pounceAnimLength;
        PounceHSpeed = pounceHSpeed;
        PounceTimer = 0;
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
        parent.getLookControl().setLookAt(target);
        if (parent.distanceTo(target) < Distance){
            CD = CD > 0 ? CD - 1 : 0;
            if (CD == 0){
                parent.setYBodyRot((float)parent.getLookControl().getWantedY());
                parent.getNavigation().moveTo(parent, 1.0);
                PounceState = 1;
                parent.setAIState(MyiaticBase.state.other.ordinal());
                if (parent instanceof MyiaticSpiderEntity){
                    ((MyiaticSpiderEntity)parent).setPounceState(MyiaticSpiderEntity.PounceStates.GettingReady.ordinal());
                }
                PounceTimer++;
                if (PounceTimer >= PounceAnimLength){
                    PounceState = 2;
                }
            }
        }
        else{
            PounceState = 0;
            CD = CDMax;
        }
    }
    protected void MidAirChecks() {
        if (parent.distanceTo(parent.getTarget()) < 1.5){
            PounceState = 4;
            if (parent instanceof MyiaticSpiderEntity){
                ((MyiaticSpiderEntity)parent).setPounceState(MyiaticSpiderEntity.PounceStates.Attached.ordinal());
            }
        }
        else if (parent.verticalCollision || parent.horizontalCollision || parent.isInWater()){
            stop();
        }
    }
    protected void Pounce(){
        parent.setDeltaMovement(parent.getDeltaMovement().add(parent.getForward().multiply(PounceHSpeed, CalculateVSpeed(), PounceHSpeed)));
        parent.setAIState(MyiaticBase.state.other.ordinal());
        if (parent instanceof MyiaticSpiderEntity){
            ((MyiaticSpiderEntity)parent).setPounceState(MyiaticSpiderEntity.PounceStates.Midair.ordinal());
        }
        CD = CDMax;
        PounceState = 3;
    }
    protected void GrabOntoPrey() {
        parent.getLookControl().setLookAt(parent.getTarget());
        parent.setPos(GetTargetLatchPos());
        parent.resetFallDistance();
        if (parent instanceof MyiaticSpiderEntity){
            ((MyiaticSpiderEntity)parent).setPounceState(MyiaticSpiderEntity.PounceStates.Attached.ordinal());
        }
        StrikeAttachedTarget();
    }
    protected void StrikeAttachedTarget() {
        HitCD = HitCD > 0 ? HitCD - 1 : 0;
        if (HitCD <= 0){
            HitTicker++;
            if (HitTicker == HitAnimPos){
                LivingEntity target = parent.getTarget();
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
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
        CD = 0;
        HitCD = -1;
        HitTicker = 0;
        parent.setAIState(MyiaticBase.state.idle.ordinal());
        if (parent instanceof MyiaticSpiderEntity){
            ((MyiaticSpiderEntity)parent).setPounceState(MyiaticSpiderEntity.PounceStates.NotPouncing.ordinal());
        }
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
        return 3 /* Mulitplier*/;
    }
    protected Vec3 GetTargetLatchPos(){
        return parent.getTarget().position().add(parent.getForward().reverse().multiply(parent.getTarget().getBbWidth() / 2, 0, parent.getTarget().getBbWidth() / 2)).add(0, parent.getTarget().getBbHeight() / 2, 0);
    }
}
