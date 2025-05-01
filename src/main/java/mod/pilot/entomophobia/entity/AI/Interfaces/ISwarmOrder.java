package mod.pilot.entomophobia.entity.AI.Interfaces;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public interface ISwarmOrder {
    Goal relay(MyiaticBase M);
    MyiaticBase getParent();
    default @Nullable MyiaticBase getCaptain(){
        return getParent() != null && getParent().isInSwarm() ? getParent().getSwarm().getCaptain() : null;
    }
    int getPriority();

    default boolean captainOnly(){
        return false;
    }
}
