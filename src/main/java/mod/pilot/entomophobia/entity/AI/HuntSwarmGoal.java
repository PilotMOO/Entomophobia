package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.SwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HuntSwarmGoal extends Goal implements SwarmOrder {
    final MyiaticBase parent;
    final MyiaticBase captain;
    final int Priority;
    final int NextAreaTimer;
    int NATTracker = 0;
    public HuntSwarmGoal(MyiaticBase parent, MyiaticBase captain, int nextAreaTimer, int priority){
        this.parent = parent;
        this.captain = captain;
        this.NextAreaTimer = nextAreaTimer;
        this.Priority = priority;
    }

    @Override
    public boolean canUse() {
        return parent.getSwarm() != null && parent.getTarget() == null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return parent.amITheCaptain();
    }

    @Override
    public void tick() {
        if (parent.amITheCaptain()){
            NATTracker++;
            if (NATTracker >= NextAreaTimer){
                Vec3 nextArea = DefaultRandomPos.getPos(parent, 64, 32);
                if (nextArea == null){
                    stop();
                    return;
                }
                parent.getNavigation().moveTo(nextArea.x, nextArea.y, nextArea.z, 1.0);
            }
        }
        else if (getCaptain().getTarget() != null){
            parent.setTarget(getCaptain().getTarget());
        }
    }

    @Override
    public Goal Relay(MyiaticBase M) {
        return new HuntSwarmGoal(M, getCaptain(), NextAreaTimer, getPriority());
    }
    @Override
    public Goal ReplaceCaptain(MyiaticBase toReplace) {
        return new HuntSwarmGoal(toReplace, toReplace, NextAreaTimer, getPriority());
    }
    @Override
    public MyiaticBase getParent() {
        return parent;
    }
    @Override
    public MyiaticBase getCaptain() {
        return captain;
    }
    @Override
    public int getPriority() {
        return Priority;
    }
}
