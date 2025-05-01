package mod.pilot.entomophobia.entity.AI.SwarmGoals;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import net.minecraft.world.entity.ai.goal.Goal;

public class FollowCaptainGoal extends Goal implements ISwarmOrder {
    final MyiaticBase parent;
    public double MinDistance;
    public double MaxDistance;
    public int Priority;

    public FollowCaptainGoal(MyiaticBase parent, double minDistance, double maxDistance, int priority){
        this.parent = parent;
        MinDistance = minDistance;
        MaxDistance = maxDistance;
        Priority = priority;
    }

    @Override
    public boolean canUse() {
        Swarm swarm = parent.getSwarm();
        return swarm != null && !swarm.isDisbanded() && swarm.getCaptain() != null && !swarm.isFinished() && !parent.amITheCaptain();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        if (getCaptain() == null) return false;
        return parent.distanceTo(getCaptain()) > MaxDistance / 2;
    }

    @Override
    public void tick() {
        if (parent.tickCount % 60 == 0){
            MyiaticBase captain = getCaptain();
            if (captain == null) return;
            double distance = parent.distanceTo(captain);
            if (distance > MinDistance){
                if (distance > MaxDistance){
                    stop();
                    return;
                }
                parent.getNavigation().moveTo(captain, 1);
            }
        }
    }

    @Override
    public void stop() {
        parent.LeaveSwarm(false);
    }

    @Override
    public Goal relay(MyiaticBase M) {
        return new FollowCaptainGoal(M, MinDistance, MaxDistance, Priority);
    }

    @Override
    public MyiaticBase getParent() {
        return parent;
    }
    @Override
    public int getPriority() {
        return Priority;
    }
}
