package mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.VectorLineGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class WeightedVectorLineGenerator extends VectorLineGenerator {
    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                       boolean replaceableOnly, Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, hydrophobic);
        this.weight = weight;
    }

    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                       int maxHardness, Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, hydrophobic);
        this.weight = weight;
    }

    public WeightedVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                       @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                       Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, hydrophobic);
        this.weight = weight;
    }

    public final int weight;
}
