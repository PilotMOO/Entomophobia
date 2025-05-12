package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.data.IntegerCycleTracker;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Predicate;

public class ShortLifespanGoal extends Goal implements ISwarmOrder {
    public final MyiaticBase parent;
    public final int maxAge;
    public final Predicate<MyiaticBase> shouldIncrement;
    public final int priority;

    public final IntegerCycleTracker cycle;

    public ShortLifespanGoal(MyiaticBase parent, int age, int priority){
        this(parent, age, priority, m -> true);
    }
    public ShortLifespanGoal(MyiaticBase parent, int age, int priority, Predicate<MyiaticBase> shouldIncrement){
        this.parent = parent;
        this.maxAge = age;
        this.priority = priority;
        this.shouldIncrement = shouldIncrement;

        this.cycle = new IntegerCycleTracker(age);
    }

    @Override
    public boolean canUse() {return shouldIncrement();}
    @Override
    public boolean canContinueToUse() {
        if (shouldIncrement()) return !cycle.tick();
        else return true;
    }
    @Override
    public boolean requiresUpdateEveryTick() {return true;}

    protected boolean shouldIncrement(){
        return shouldIncrement.test(parent);
    }

    @Override
    public void stop() {
        parent.setEncouragedDespawn(true);
        cycle.reset();
    }

    @Override
    public Goal relay(MyiaticBase M) {
        return new ShortLifespanGoal(M, maxAge, priority, shouldIncrement);
    }

    @Override
    public MyiaticBase getParent() {
        return parent;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
