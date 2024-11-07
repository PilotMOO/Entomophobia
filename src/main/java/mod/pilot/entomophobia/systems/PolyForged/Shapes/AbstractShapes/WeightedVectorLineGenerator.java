package mod.pilot.entomophobia.systems.PolyForged.Shapes.AbstractShapes;

import mod.pilot.entomophobia.systems.PolyForged.Shapes.VectorLineGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class WeightedVectorLineGenerator extends VectorLineGenerator {
    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end);
        this.weight = weight;
    }

    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end);
        this.weight = weight;
    }

    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end);
        this.weight = weight;
    }

    public final int weight;
}
