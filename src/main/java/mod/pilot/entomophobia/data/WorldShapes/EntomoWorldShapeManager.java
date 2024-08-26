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


    public static CubeGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int size){
        CubeGenerator cube = new CubeGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, size);
        cube.Enable();
        return cube;
    }
    public static CubeGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int size) {
        CubeGenerator cube = new CubeGenerator(server, buildSpeed, blockTypes, pos, maxHardness, size);
        cube.Enable();
        return cube;
    }
    public static CubeGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int size){
        CubeGenerator cube = new CubeGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, size);
        cube.Enable();
        return cube;
    }

    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius){
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius);
        sphere.Enable();
        return sphere;
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius) {
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius);
        sphere.Enable();
        return sphere;
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius){
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius);
        sphere.Enable();
        return sphere;
    }

    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness) {
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius, int thickness){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness);
        sphere.Enable();
        return sphere;
    }
}
