package mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common;

import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Ghost3D <G extends Ghost3D<G>> extends GhostShape<Ghost3D<G>>{
    protected Ghost3D(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos, int X, int Y, int Z) {
        super(ghosts, pos);
        this.X = X; this.Y = Y; this.Z = Z;
    }
    protected Ghost3D(@Nullable ArrayList<PackagedBlockPos> ghosts,Vec3 pos, int size) {
        super(ghosts, pos);
        this.X = this.Y = this.Z = size;
    }
    public final int X;
    public final int Y;
    public final int Z;

    public abstract G rotate(WorldShapeManager.SignedAxis rotateTowards);
}
