package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WorldShapeManager {
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
    public enum Axis{
        X,
        Y,
        Z
    }
    public enum SignedAxis{
        PosX,
        NegX,
        PosY,
        NegY,
        PosZ,
        NegZ,
    }


    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int X, int Y, int Z){
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }
    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int X, int Y, int Z) {
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, maxHardness, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }
    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int X, int Y, int Z){
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }

    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int size){
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, size, size, size);
        cube.Enable();
        return cube;
    }
    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int size) {
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, maxHardness, size, size, size);
        cube.Enable();
        return cube;
    }
    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int size){
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, size, size, size);
        cube.Enable();
        return cube;
    }

    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int size, Axis excluded){
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, size, excluded);
        square.Enable();
        return square;
    }
    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int size, Axis excluded) {
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, maxHardness, size, excluded);
        square.Enable();
        return square;
    }
    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int size, Axis excluded){
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, size, excluded);
        square.Enable();
        return square;
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
