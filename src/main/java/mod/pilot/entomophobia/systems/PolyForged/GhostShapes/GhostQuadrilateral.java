package mod.pilot.entomophobia.systems.PolyForged.GhostShapes;

import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common.Ghost3D;
import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common.PackagedBlockPos;
import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GhostQuadrilateral extends Ghost3D<GhostQuadrilateral> {
    protected GhostQuadrilateral(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos, int X, int Y, int Z) {
        super(ghosts, pos, X, Y, Z);
    }
    protected GhostQuadrilateral(@Nullable ArrayList<PackagedBlockPos> ghosts, Vec3 pos, int size) {
        super(ghosts, pos, size);
    }

    public boolean isCube(){
        return X == Y && X == Z;
    }

    public static GhostQuadrilateral Generate(int X, int Y, int Z, Vec3 origin){
        ArrayList<PackagedBlockPos> ghosts = new ArrayList<>();
        for(int x = 0; x < X; ++x) {
            for(int y = 0; y < Y; ++y) {
                for(int z = 0; z < Z; ++z) {
                    ghosts.add(new PackagedBlockPos((int)(origin.x + (x - (X / 2)) - 1),
                            (int)(origin.y + (y - (Y / 2)) - 1),
                            (int)(origin.z + (z - (Z / 2))) - 1));
                }
            }
        }
        return new GhostQuadrilateral(ghosts, origin, X, Y, Z);
    }
    private GhostQuadrilateral Generate(int X, int Y, int Z){
        Vec3 origin = getOrigin();
        ArrayList<PackagedBlockPos> ghosts = new ArrayList<>();
        for(int x = 0; x < X; ++x) {
            for(int y = 0; y < Y; ++y) {
                for(int z = 0; z < Z; ++z) {
                    ghosts.add(new PackagedBlockPos((int)(origin.x + (x - (X / 2)) - 1),
                            (int)(origin.y + (y - (Y / 2)) - 1),
                            (int)(origin.z + (z - (Z / 2))) - 1));
                }
            }
        }
        return new GhostQuadrilateral(ghosts, origin, X, Y, Z);
    }
    public static GhostQuadrilateral GenerateCube(int size, Vec3 origin){
        return Generate(size, size, size, origin);
    }
    @Override
    public GhostQuadrilateral rotate(WorldShapeManager.SignedAxis rotateTowards) {
        if (isCube()) return GenerateCube(X, getOrigin());
        boolean isX = WorldShapeManager.isX((byte)rotateTowards.ordinal());
        boolean isY = WorldShapeManager.isY((byte)rotateTowards.ordinal());
        boolean isZ = WorldShapeManager.isZ((byte)rotateTowards.ordinal());
        int newX = isX ? X : isY ? Z : Y;
        int newY = isY ? Y : isX ? Z : X;
        int newZ = isZ ? Z : isX ? Y : X;
        return Generate(newX, newY, newZ);
    }

    @Override
    public Ghost3D<GhostQuadrilateral> scale(double size) {
        return Generate((int)(X * size), (int)(Y * size), (int)(Z * size));
    }

    @Override
    public Ghost3D<GhostQuadrilateral> move(int XCount, int YCount, int ZCount) {
        return Generate(X, Y, Z, getOrigin().add(XCount, YCount, ZCount));
    }
}
