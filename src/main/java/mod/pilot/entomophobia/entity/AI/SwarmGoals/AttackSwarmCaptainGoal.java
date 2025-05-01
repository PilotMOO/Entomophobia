package mod.pilot.entomophobia.entity.AI.SwarmGoals;

import mod.pilot.entomophobia.data.IntegerCycleTracker;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class AttackSwarmCaptainGoal extends Goal implements ISwarmOrder {
    public final MyiaticBase parent;
    public final int aggroRange;
    public final int aggroFrequency;
    public final int priority;

    public AttackSwarmCaptainGoal(MyiaticBase parent, int aggroFrequency, int priority){
        this(parent, -1, aggroFrequency, priority);
    }
    public AttackSwarmCaptainGoal(MyiaticBase parent, int aggroRange, int aggroFrequency, int priority){
        this.parent = parent;
        this.aggroRange = aggroRange;
        this.aggroFrequency = aggroFrequency;
        this.priority = priority;
        this.tracker = new IntegerCycleTracker(aggroFrequency);
    }
    private final IntegerCycleTracker tracker;

    @Override
    public boolean canUse() {
        return parent.amITheCaptain() && tracker.tick();
    }
    @Override
    public void tick() {
        Swarm swarm = parent.getSwarm();
        if (swarm != null && swarm.getSwarmTarget() instanceof LivingEntity le){
            if (le.isDeadOrDying()) swarm.disband();
            else swarm.getUnits().forEach(m -> {if (m.getTarget() == null && m.distanceTo(le) < getAggroRangeFor(m)) m.setTarget(le);});
        }
    }
    private int getAggroRangeFor(MyiaticBase m){
        if (aggroRange == -1) return (int) m.getAttributeValue(Attributes.FOLLOW_RANGE);
        else return aggroRange;
    }

    @Override
    public Goal relay(MyiaticBase M) {return new AttackSwarmCaptainGoal(M, aggroRange, aggroFrequency, priority);}
    @Override
    public MyiaticBase getParent() {return parent;}
    @Override
    public int getPriority() {return priority;}
    @Override
    public boolean captainOnly() {return true;}
}
