package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;

public class RecruitNearbyGoal extends Goal implements ISwarmOrder {
    private final MyiaticBase parent;
    private final int Priority;
    private final int TickFrequency;
    public RecruitNearbyGoal(MyiaticBase parent, int tickFrequency, int priority){
        this.parent = parent;
        this.TickFrequency = tickFrequency;
        this.Priority = priority;
    }
    @Override
    public boolean canUse() {
        return parent.isInSwarm() && parent.getSwarm().getRecruitCount() < parent.getSwarm().getMaxRecruits();
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
                boolean flag = M.tryToRecruit(parent.getSwarm());
                if (flag) {
                    M.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40));
                }
            }
        }
    }

    @Override
    public Goal relay(MyiaticBase M) {
        return new RecruitNearbyGoal(M, TickFrequency, getPriority());
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
