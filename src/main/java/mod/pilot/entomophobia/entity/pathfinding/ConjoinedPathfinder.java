package mod.pilot.entomophobia.entity.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConjoinedPathfinder<I extends PathNavigation, J extends PathNavigation> extends PathNavigation{
    public ConjoinedPathfinder(Mob mob, Level level, I primaryPathfinder, J secondaryPathfinder, Predicate<Mob> secondaryPredicate) {
        super(mob, level);
        Primary = primaryPathfinder;
        Secondary = secondaryPathfinder;
        this.switchToSecondary = secondaryPredicate;
        Active = Primary;
    }
    public PathNavigation Active;
    public final I Primary;
    public final J Secondary;
    public final Predicate<Mob> switchToSecondary;

    public BlockPos wantedPosition;

    private void setActive(){
        //PathNavigation prior = Active;
        Active = switchToSecondary.test(mob) ? Secondary : Primary;
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        return null;
    }

    public void resetMaxVisitedNodesMultiplier() {
        Active.setMaxVisitedNodesMultiplier(1.0F);
    }
    public void setMaxVisitedNodesMultiplier(float pMultiplier) {
        Active.setMaxVisitedNodesMultiplier(pMultiplier);
    }
    @Nullable
    public BlockPos getTargetPos() {
        return Active.getTargetPos();
    }
    public void setSpeedModifier(double pSpeed) {
        Active.setSpeedModifier(pSpeed);
    }

    public void recomputePath() {
        Active.recomputePath();
    }

    @Nullable
    public Path createPath(@NotNull Stream<BlockPos> pTargets, int pAccuracy) {
        wantedPosition = pTargets.findAny().orElse(null);
        setActive();
        return Active.createPath(pTargets, pAccuracy);
    }
    @Nullable
    public Path createPath(@NotNull Set<BlockPos> pPositions, int pDistance) {
        wantedPosition = new ArrayList<>(pPositions).get(pPositions.size() - 1);
        setActive();
        return Active.createPath(pPositions, pDistance);
    }

    @Nullable
    public Path createPath(@NotNull BlockPos pPos, int pAccuracy) {
        wantedPosition = pPos;
        setActive();
        return Active.createPath(pPos, pAccuracy);
    }

    @Nullable
    public Path createPath(@NotNull BlockPos pPos, int pRegionOffset, int pAccuracy) {
        wantedPosition = pPos;
        setActive();
        return Active.createPath(pPos,  pRegionOffset, pAccuracy);
    }
    @Nullable
    public Path createPath(@NotNull Entity pEntity, int pAccuracy) {
        wantedPosition = pEntity.blockPosition();
        setActive();
        return Active.createPath(pEntity, pAccuracy);
    }

    public boolean moveTo(double pX, double pY, double pZ, double pSpeed) {
        wantedPosition = BlockPos.containing(pX, pY, pZ);
        setActive();
        return Active.moveTo(this.createPath(pX, pY, pZ, 1), pSpeed);
    }

    public boolean moveTo(@NotNull Entity pEntity, double pSpeed) {
        wantedPosition = pEntity.blockPosition();
        setActive();
        return Active.moveTo(pEntity, pSpeed);
    }
    public boolean moveTo(@Nullable Path pPathentity, double pSpeed) {
        if (pPathentity != null && pPathentity.getEndNode() != null){
            wantedPosition = pPathentity.getEndNode().asBlockPos();
        }
        setActive();
        return Active.moveTo(pPathentity, pSpeed);
    }
    @Nullable
    public Path getPath() {
        return Active.getPath();
    }

    public void tick() {
        ++this.tick;
        System.out.println("Active pathfinder is " + (Active == Primary ? "Primary" : "Secondary"));
        Active.tick();
        if (isDone()) wantedPosition = null;
    }

    public boolean isDone() {
        return Active.isDone();
    }
    public boolean isInProgress() {
        return !Active.isDone();
    }
    public void stop() {
        this.Active.stop();
        wantedPosition = null;
    }

    public boolean canCutCorner(@NotNull BlockPathTypes pPathType) {
        return Active.canCutCorner(pPathType);
    }

    public boolean isStableDestination(BlockPos pPos) {
        return Active.isStableDestination(pPos);
    }

    public NodeEvaluator getNodeEvaluator() {
        return Active.getNodeEvaluator();
    }

    public void setCanFloat(boolean pCanSwim) {
        Active.setCanFloat(pCanSwim);
    }

    public boolean canFloat() {
        return Active.canFloat();
    }

    public boolean shouldRecomputePath(BlockPos pPos) {
        return Active.shouldRecomputePath(pPos);
    }

    public float getMaxDistanceToWaypoint() {
        return Active.getMaxDistanceToWaypoint();
    }

    public boolean isStuck() {
        return Active.isStuck();
    }

    @Override
    protected void followThePath() {
        System.err.println("[CONJOINED PATHFINDER LOG] ERROR! Attempted to invoke followThePath() on the Conjoined Pathfinder itself! " +
                "Invoke it on the Active Pathfinder instead!");
        return;
    }

    @Override
    protected @NotNull Vec3 getTempMobPos() {
        System.err.println("[CONJOINED PATHFINDER LOG] ERROR! Attempted to invoke getTempMobPos() on the Conjoined Pathfinder itself! " +
                "Invoke it on the Active Pathfinder instead!");
        System.err.println("[CONJOINED PATHFINDER LOG] returning Vec3.ZERO...");
        return Vec3.ZERO;
    }
    @Override
    protected boolean canUpdatePath() {
        System.err.println("[CONJOINED PATHFINDER LOG] ERROR! Attempted to invoke canUpdatePath() on the Conjoined Pathfinder itself! " +
                "Invoke it on the Active Pathfinder instead!");
        System.err.println("[CONJOINED PATHFINDER LOG] returning false...");
        return false;
    }
}
