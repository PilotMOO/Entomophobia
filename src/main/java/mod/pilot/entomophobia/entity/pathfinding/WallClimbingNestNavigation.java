package mod.pilot.entomophobia.entity.pathfinding;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.Nest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WallClimbingNestNavigation extends GroundPathNavigation implements INestPathfinding {
    public WallClimbingNestNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }
    public BlockPos targetPosition;

    @Override
    public void tick() {
        super.tick();
        Mob user = getUser();

        if (isDone() && targetPosition != null && targetPosition.getY() > user.position().y){
            if (user.distanceToSqr(targetPosition.getX(), targetPosition.getY(),
                    targetPosition.getZ()) > user.getBbWidth() * user.getBbWidth()){
                user.getMoveControl().setWantedPosition(
                        this.targetPosition.getX(),
                        this.targetPosition.getY(),
                        this.targetPosition.getZ(), this.speedModifier);
            } else targetPosition = null;
        }
        if (user instanceof MyiaticBase m && m.horizontalCollision && !m.onClimbable()){
            m.jump();
        }

        //Checks to see if the entity is done moving
        // then either tells it to update its map OR continue to head to the designated spot if it is not close enough
        NestMap map = getNestMap();
        if (isDone() && map != null && getMoveDirections() != null && guesstimateIfImInANest()) {
            Nest.Offshoot offshoot = moveDirections.currentOffshoot();
            Vec3 pos = null;
            final double error = 36; //36 = 6^, 6 blocks error
            double sizeSqr = 0;
            if (offshoot instanceof Nest.Corridor c){
                pos = getMoveDirections().headToEndIfCorridor() ? c.end : c.getStartDirect();
                sizeSqr = c.weight * c.weight;
            } else if (offshoot instanceof Nest.Chamber c){
                pos = c.getPosition();
                sizeSqr = (c.radius * 2) * (c.radius * 2);
            }
            sizeSqr += error;

            if (pos != null){
                if (user.distanceToSqr(pos) <= sizeSqr) updateNavigationAfterMovement();
                else{
                    if (offshoot instanceof Nest.Corridor c){
                        headTo(c, getMoveDirections().headToEndIfCorridor(), 1);
                    } else {
                        headTo((Nest.Chamber) offshoot, 1);
                    }
                }
            }
        }
        if (shouldIReorientateMyself()) headBackToCenterToReorientateMyself(1);
    }

    @Override
    public @NotNull Path createPath(@NotNull BlockPos pPos, int pAccuracy) {
        targetPosition = pPos;
        return super.createPath(pPos, pAccuracy);
    }
    @Override
    public @NotNull Path createPath(Entity pEntity, int pAccuracy) {
        targetPosition = pEntity.blockPosition();
        return super.createPath(pEntity, pAccuracy);
    }

    @Override
    public Mob getUser() {return mob;}
    @Override
    public PathNavigation getNavigation() {return this;}
    protected NestMap nestMap;
    @Override
    public NestMap getNestMap() {return nestMap;}
    @Override
    public void setNestMap(NestMap newMap) {this.nestMap = newMap;}
    protected MoveDirections moveDirections;
    @Override
    public MoveDirections getMoveDirections() {return moveDirections;}
    @Override
    public void setMoveDirections(MoveDirections directions) {this.moveDirections = directions;}
    protected boolean reorientating;
    @Override
    public boolean reorientating() {
        return reorientating;
    }
    @Override
    public void setReorientating(boolean flag) {reorientating = flag;}

    @Override
    public boolean shouldIReorientateMyself() {
        return !amIHomeless() && amILost() && this.isDone()
                && getNestMap() != null && getNestMap().currentNest() != null;
    }
}
