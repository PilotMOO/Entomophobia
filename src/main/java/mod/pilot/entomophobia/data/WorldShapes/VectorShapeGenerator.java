package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class VectorShapeGenerator extends ShapeGenerator{
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly);
        Vectors = vectors;
    }
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, maxHardness);
        Vectors = vectors;
    }
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist);
        Vectors = vectors;
    }

    public final List<Vec3> Vectors;

    protected Vec3 directionFromAToB(Vec3 A, Vec3 B){
        return B.subtract(A).normalize();
    }
}
