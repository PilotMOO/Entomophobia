package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public class FollowCaptainGoal extends Goal implements ISwarmOrder {
    final MyiaticBase parent;
    final MyiaticBase captain;
    public double MinDistance;
    public double MaxDistance;
    public int Priority;

    public FollowCaptainGoal(MyiaticBase parent, MyiaticBase captain, double minDistance, double maxDistance, int priority){
        this.parent = parent;
        this.captain = captain;
        MinDistance = minDistance;
        MaxDistance = maxDistance;
        Priority = priority;
    }

    @Override
    public boolean canUse() {
        Swarm swarm = parent.getSwarm();
        return swarm != null && !swarm.isDisbanded() && !swarm.isFinished() && !parent.amITheCaptain();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return parent.distanceTo(captain) > MaxDistance / 2;
    }

    @Override
    public void tick() {
        if (parent.tickCount % 60 == 0){
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
    public Goal Relay(MyiaticBase M) {
        return new FollowCaptainGoal(M, getCaptain(), MinDistance, MaxDistance, Priority);
    }

    @Override
    public Goal ReplaceCaptain(MyiaticBase toReplace) {
        return new FollowCaptainGoal(toReplace, toReplace, MinDistance, MaxDistance, Priority);
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
