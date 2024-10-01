package mod.pilot.entomophobia.entity.AI.Interfaces;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.goal.Goal;

public interface ISwarmOrder {
    Goal Relay(MyiaticBase M);
    Goal ReplaceCaptain(MyiaticBase toReplace);
    MyiaticBase getParent();
    MyiaticBase getCaptain();
    int getPriority();
}
