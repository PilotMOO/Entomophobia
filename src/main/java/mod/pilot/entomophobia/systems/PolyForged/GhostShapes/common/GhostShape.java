package mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public abstract class GhostShape <G extends GhostShape<G>> {
    protected GhostShape(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos){
        this.Ghosts = ghosts;
        this.origin = pos;
    }
    protected final @Nullable ArrayList<PackagedBlockPos> Ghosts;
    public final ArrayList<PackagedBlockPos> getGhosts(){
        return new ArrayList<>(Dud() ? Collections.emptyList() : Ghosts);
    }
    public boolean Dud(){
        return Ghosts == null;
    }
    private final Vec3 origin;
    public Vec3 getOrigin(){
        return origin;
    }

    public abstract G scale(double size);
    public abstract G move(int XCount, int YCount, int ZCount);
}
