package mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes;

import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class VectorShapeGenerator extends ShapeGenerator{
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                   boolean replaceableOnly, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly);
        Vectors = vectors;
    }
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                   int maxHardness, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness);
        Vectors = vectors;
    }
    protected VectorShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                   @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, List<Vec3> vectors) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist);
        Vectors = vectors;
    }

    public final List<Vec3> Vectors;

    protected Vec3 directionFromAToB(Vec3 A, Vec3 B){
        return B.subtract(A).normalize();
    }
}
