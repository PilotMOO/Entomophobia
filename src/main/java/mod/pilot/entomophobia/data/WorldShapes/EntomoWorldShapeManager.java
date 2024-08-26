package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntomoWorldShapeManager {
    public enum GeneratorStates{
        disabled,
        active,
        done
    }
    public enum PlacementDetails {
        ReplaceableOnly,
        HardnessCapped,
        Specified,
        Any
    }

    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius){
        return new SphereGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius);
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius) {
        return new SphereGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius);
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius){
        return new SphereGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius);
    }
}
