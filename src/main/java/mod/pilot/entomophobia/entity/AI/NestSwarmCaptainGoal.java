package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

public class NestSwarmCaptainGoal extends Goal implements ISwarmOrder {
    private final MyiaticBase parent;
    private final int checkTickFrequency;
    private final int requiredMemberCount;
    private final int priority;
    public NestSwarmCaptainGoal(MyiaticBase parent, int checkTickFrequency, int requiredMemberCount, int priority){
        this.parent = parent;
        this.checkTickFrequency = checkTickFrequency;
        this.requiredMemberCount = requiredMemberCount;
        this.priority = priority;
    }
    @Override
    public boolean canUse() {
        return parent.canSwarm() && parent.isInSwarm() && parent.getSwarm().getSwarmType() == 2;
    }
    @Override
    public boolean canContinueToUse() {
        return parent.isInSwarm() && parent.getSwarm().isActive();
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!(parent.level() instanceof ServerLevel server)) return;
        if (parent.tickCount % checkTickFrequency == 0){
            Swarm swarm = parent.getSwarm();
            if (swarm == null) {
                stop();
                return;
            }

            if (swarm.getRecruitCount() >= requiredMemberCount){
                NestManager.ConstructNewNest(server, NestManager.getNewNestPosition(parent.position(), 32, false), false);
                swarm.Finish();
                stop();
            }
        }
    }

    @Override
    public void stop() {
        parent.QueRemoveGoal(this);
    }

    //ISwarmOrder Implementation
    @Override
    public Goal Relay(MyiaticBase M) {
        return new NestSwarmCaptainGoal(M, checkTickFrequency, requiredMemberCount, priority);
    }

    @Override
    public MyiaticBase getParent() {
        return parent;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean CaptainOnly() {return true;}
}
