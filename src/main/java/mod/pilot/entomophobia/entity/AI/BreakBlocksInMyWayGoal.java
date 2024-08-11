package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.goal.Goal;

public class BreakBlocksInMyWayGoal extends Goal {
    final MyiaticBase parent;
    public BreakBlocksInMyWayGoal(MyiaticBase parent){
        this.parent = parent;
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    @Override
    public boolean canUse() {
        return parent.getTarget() != null;
    }
    @Override
    public void tick() {
        if (parent.tickCount % 40 == 0){
            parent.BreakBlocksInMyWay();
        }
    }
}
