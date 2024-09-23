package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.SwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import net.minecraft.world.entity.ai.goal.Goal;

public class FollowCaptain extends Goal implements SwarmOrder {
    final MyiaticBase parent;
    final MyiaticBase captain;
    public double MinDistance;
    public double MaxDistance;
    public int Priority;

    public FollowCaptain(MyiaticBase parent, MyiaticBase captain, double minDistance, double maxDistance, int priority){
        this.parent = parent;
        this.captain = captain;
        MinDistance = minDistance;
        MaxDistance = maxDistance;
        Priority = priority;
    }
    public static FollowCaptain CreateCaptainOrder(Swarm swarm, int priority, double minDistance, double maxDistance){
        MyiaticBase captain = swarm.getCaptain();
        FollowCaptain orders = new FollowCaptain(captain, captain, minDistance, maxDistance, priority);
        swarm.RelayOrder(orders, true);
        return orders;
    }

    @Override
    public boolean canUse() {
        Swarm swarm = parent.getSwarm();
        return swarm != null && swarm.isActive() && !parent.amITheCaptain();
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
        parent.goalSelector.removeGoal(this);
    }

    @Override
    public Goal Relay(MyiaticBase M) {
        return new FollowCaptain(M, getCaptain(), MinDistance, MaxDistance, Priority);
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
