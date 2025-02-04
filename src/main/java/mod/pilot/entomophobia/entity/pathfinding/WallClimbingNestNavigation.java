package mod.pilot.entomophobia.entity.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;

public class WallClimbingNestNavigation extends WallClimberNavigation implements INestPathfinding {
    public WallClimbingNestNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }
    public BlockPos targetPosition;

    @Override
    public void tick() {
        super.tick();
        if (isDone() && getNestMap() != null && getMoveDirections() != null && guesstimateIfImInANest()) UpdateNavigationAfterMovement();
        if (shouldIReorientateMyself()) HeadBackToCenterToReorientateMyself(1);
    }

    @Override
    public Path createPath(@NotNull BlockPos pPos, int pAccuracy) {
        targetPosition = pPos;
        return super.createPath(pPos, pAccuracy);
    }
    @Override
    public Path createPath(Entity pEntity, int pAccuracy) {
        targetPosition = pEntity.blockPosition();
        return super.createPath(pEntity, pAccuracy);
    }

    @Override
    public LivingEntity getUser() {return mob;}
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
    public boolean Reorientating() {
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
