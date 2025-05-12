package mod.pilot.entomophobia.entity.AI.SwarmGoals;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

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
            checkForMerge();
        }
        if (getClose && captain.getNavigation().isDone()){
            getClose = false;
            checkForMerge();
        }

        if (targetFlag){
            if (captain.getSwarm() == null){
                stop();
                return;
            }
            for (MyiaticBase M : captain.getSwarm().getUnits()){
                if (M.getTarget() != null) continue;
                M.setTarget(captain.getTarget());
            }
            targetFlag = false; return;
        }
        targetFlag = captain.getTarget() != null;
    }

    protected void checkForMerge(){
        Swarm cSwarm = captain.getSwarm();
        if (cSwarm == null){
            stop();
            return;
        }
        for (Swarm swarm : SwarmManager.getSwarms()){
            if (!cSwarm.canMergeWith(swarm, true)) continue;
            Vec3 swarmPos = swarm.getSwarmPosition();
            if (swarmPos != null && cSwarm.distanceTo(swarmPos) < 16){
                if (swarm.getMaxRecruits() <= swarm.getRecruitCount() + captain.getSwarm().getRecruitCount()) {
                    swarm.copyUnits(captain.getSwarm(), false);
                    System.out.println("Trying to merge " + captain.getSwarm() + " with " + swarm);
                }
            }
            else if (captain.getTarget() != null && captain.position().distanceTo(swarm.getSwarmPosition()) < 64){
                System.out.println("Trying to get closer to " + swarm);
                captain.getNavigation().moveTo(swarm.getCaptain(), 1);
                getClose = true;
                break;
            }
        }
    }

    @Override
    public Goal relay(MyiaticBase M) {
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
    public boolean captainOnly() {
        return true;
    }
}
