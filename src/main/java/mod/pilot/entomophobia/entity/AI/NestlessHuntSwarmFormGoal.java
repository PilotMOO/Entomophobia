package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;

public class NestlessHuntSwarmFormGoal extends Goal {
    private final MyiaticBase parent;
    private final int MinSwarmSize;
    private final int FormationCheckTimer;
    private int FCTTicker;
    public NestlessHuntSwarmFormGoal(MyiaticBase parent, int checkTimer, int minFormationSize){
        this.parent = parent;
        this.FormationCheckTimer = checkTimer;
        this.MinSwarmSize = minFormationSize;
    }
    @Override
    public boolean canUse() {
        return parent.canSwarm() && !parent.isInSwarm() && (parent.getDistanceToClosestNest() == -1 || parent.getDistanceToClosestNest() > 2048);
    }

    @Override
    public boolean canContinueToUse() {
        return !parent.isInSwarm();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        FCTTicker = FCTTicker < FormationCheckTimer ? FCTTicker + 1 : 0;
        if (FCTTicker >= FormationCheckTimer){
            ArrayList<MyiaticBase> nearbyTeammates = parent.getNearbyMyiatics();
            ArrayList<MyiaticBase> trim = new ArrayList<>();
            for (MyiaticBase M : nearbyTeammates){
                if (!M.canSwarm() || M.isInSwarm()) trim.add(M);
            }
            nearbyTeammates.removeAll(trim);
            if (nearbyTeammates.size() >= MinSwarmSize){
                SwarmManager.CreateSwarm(SwarmManager.SwarmTypes.hunt, nearbyTeammates, SwarmManager.getBaseSwarmMaxSize(), null);
                stop();
            }
            else{
                FCTTicker = 0;
            }
        }
    }
}
