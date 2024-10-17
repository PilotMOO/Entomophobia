package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HomophobicRandomStrollGoal extends RandomStrollGoal {
    private final MyiaticBase parent;
    private Vec3 lastPos;
    private final int Radius;
    private final int MaxY;
    public HomophobicRandomStrollGoal(MyiaticBase parent, double speed, int interval, int radius, int maxYSearch) {
        super(parent, speed, interval);
        this.parent = parent;
        this.Radius = radius;
        MaxY = maxYSearch;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (lastPos == null) return lastPos = DefaultRandomPos.getPos(this.mob, Radius, MaxY);
        else{
            return lastPos = DefaultRandomPos.getPosAway(parent, Radius, MaxY, lastPos);
        }
    }
}
