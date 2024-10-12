package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;

public class RecruitNearbyGoal extends Goal implements ISwarmOrder {
    private final MyiaticBase parent;
    private final MyiaticBase captain;
    private final int Priority;
    private final int TickFrequency;
    public RecruitNearbyGoal(MyiaticBase parent, MyiaticBase captain, int tickFrequency, int priority){
        this.parent = parent;
        this.captain = captain;
        this.TickFrequency = tickFrequency;
        this.Priority = priority;
    }
    @Override
    public boolean canUse() {
        return parent.isInSwarm() && parent.getSwarm().AmountOfRecruits() < parent.getSwarm().getMaxRecruits();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return parent.amITheCaptain();
    }

    @Override
    public void tick() {
        if (parent.tickCount % TickFrequency == 0){
            if (parent.getSwarm() == null){
                return;
            }
            for (MyiaticBase M : parent.getNearbyMyiatics()){
                if (!M.canSwarm() || M.isInSwarm()) continue;
                M.TryToRecruit(parent.getSwarm());
                M.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40));
            }
        }
    }

    @Override
    public Goal Relay(MyiaticBase M) {
        return new RecruitNearbyGoal(M, getCaptain(), TickFrequency, getPriority());
    }

    @Override
    public Goal ReplaceCaptain(MyiaticBase toReplace) {
        return new RecruitNearbyGoal(toReplace, toReplace, TickFrequency, getPriority());
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
