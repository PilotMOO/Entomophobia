package mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common;

import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Ghost2D <G extends Ghost2D<G>> extends GhostShape<Ghost2D<G>>{
    protected Ghost2D(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos, int X, int Y, WorldShapeManager.Axis excluded) {
        super(ghosts, pos);
        this.X = X; this.Y = Y;
        this.Excluded = excluded;
    }
    protected Ghost2D(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos, int size, WorldShapeManager.Axis excluded) {
        super(ghosts, pos);
        this.X = this.Y = size;
        this.Excluded = excluded;
    }
    public final int X;
    public final int Y;
    public final WorldShapeManager.Axis Excluded;

    public abstract G rotate(WorldShapeManager.Axis excluded);
}
