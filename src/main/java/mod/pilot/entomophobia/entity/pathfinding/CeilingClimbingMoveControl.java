package mod.pilot.entomophobia.entity.pathfinding;

import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class CeilingClimbingMoveControl extends MoveControl {
    private final double ceilingSpeed;
    public CeilingClimbingMoveControl(MyiaticBase parent, double ceilingSpeedMultiplier) {
        super(parent);
        this.ceilingSpeed = ceilingSpeedMultiplier;
    }
    public MyiaticBase asMyiatic(){
        return (MyiaticBase)mob;
    }
    private boolean shouldClimbCeiling(){
        return asMyiatic().ShouldBeCrawlingOnCeiling;
    }
    private float movementLerp;
    @Override
    public void tick() {
        /*if (shouldClimbCeiling() && asMyiatic().hole != null){
            Vec3 wanted = asMyiatic().hole.getCenter()*//*.add(0, -0.5d, 0)*//*;
            Vec3 newDelta = EntomoDataManager.getDirectionFromAToB(mob.getEyePosition(), wanted).scale(ceilingSpeed);
            if (newDelta.length() <= mob.getDeltaMovement().length()){
                movementLerp = 0f;
                return;
            }
            else if (movementLerp < 1) movementLerp += 0.1f;

            Vec3 oldDelta = mob.getDeltaMovement();
            mob.setDeltaMovement(oldDelta.lerp(newDelta, movementLerp));
        } else movementLerp = 0f;*/
        super.tick();
    }
}
