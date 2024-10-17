package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.world.entity.ai.goal.Goal;

public class CaptainCommandGoal extends Goal implements ISwarmOrder {
    private final MyiaticBase captain;
    private final int Priority;
    private final int MergeCheckFrequency;
    private boolean getClose = false;
    private boolean targetFlag = false;
    public CaptainCommandGoal(MyiaticBase captain, int checkFrequency, int priority){
        this.captain = captain;
        this.MergeCheckFrequency = checkFrequency;
        this.Priority = priority;
    }
    @Override
    public boolean canUse() {
        return captain.amITheCaptain();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (captain.tickCount % MergeCheckFrequency == 0 && SwarmManager.getSwarms().size() > 1){
            CheckForMerge();
        }
        if (getClose && captain.getNavigation().isDone()){
            getClose = false;
            CheckForMerge();
        }

        if (targetFlag){
            for (MyiaticBase M : captain.getSwarm().getUnits()){
                if (M.getTarget() != null) continue;
                M.setTarget(captain.getTarget());
            }
            targetFlag = false; return;
        }
        targetFlag = captain.getTarget() != null;
    }

    protected void CheckForMerge(){
        for (Swarm swarm : SwarmManager.getSwarms()){
            if (!ValidSwarmForMerge(swarm)) continue;
            if (captain.position().distanceTo(swarm.getSwarmPosition()) < 16){
                if (swarm.getMaxRecruits() <= swarm.AmountOfRecruits() + captain.getSwarm().AmountOfRecruits()) {
                    swarm.CopyUnits(captain.getSwarm(), false);
                    System.out.println("Trying to merge " + captain.getSwarm() + " with " + swarm);
                }
            }
            else if (captain.getTarget() != null && captain.position().distanceTo(swarm.getSwarmPosition()) < 64){
                captain.getNavigation().moveTo(swarm.getCaptain(), 1);
                getClose = true;
                break;
            }
        }
    }
    protected boolean ValidSwarmForMerge(Swarm swarm){
        return swarm != captain.getSwarm() && !swarm.isDisbanded() && swarm.getSwarmType() == captain.getSwarm().getSwarmType() && swarm.canMergeWith(captain.getSwarm());
    }

    @Override
    public Goal Relay(MyiaticBase M) {
        return new CaptainCommandGoal(M, MergeCheckFrequency, getPriority());
    }
    @Override
    public MyiaticBase getParent() {
        return captain;
    }
    @Override
    public int getPriority() {
        return Priority;
    }

    @Override
    public boolean CaptainOnly() {
        return true;
    }
}
