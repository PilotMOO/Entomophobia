package mod.pilot.entomophobia.systems.PolyForged.utility;

import mod.pilot.entomophobia.systems.PolyForged.shapes.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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

    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               boolean replaceableOnly, int size, Axis excluded){
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, size, excluded);
        square.Enable();
        return square;
    }
    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               int maxHardness, int size, Axis excluded) {
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, size, excluded);
        square.Enable();
        return square;
    }
    public static SquareGenerator CreateSquare(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                               int size, Axis excluded){
        SquareGenerator square = new SquareGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, size, excluded);
        square.Enable();
        return square;
    }

    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               boolean replaceableOnly, int radius, Axis excluded){
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, radius, excluded);
        circle.Enable();
        return circle;
    }
    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               int maxHardness, int radius, Axis excluded) {
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, radius, excluded);
        circle.Enable();
        return circle;
    }
    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                               int radius, Axis excluded){
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, radius, excluded);
        circle.Enable();
        return circle;
    }

    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                         boolean replaceableOnly, int X, int Y, int Z){
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }
    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                         int maxHardness, int X, int Y, int Z) {
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }
    public static QuadrilateralGenerator CreateRectangle(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                         @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                         int X, int Y, int Z){
        QuadrilateralGenerator rectangle = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, X, Y, Z);
        rectangle.Enable();
        return rectangle;
    }

    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                    boolean replaceableOnly, int size){
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, size, size, size);
        cube.Enable();
        return cube;
    }
    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                    int maxHardness, int size) {
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, size, size, size);
        cube.Enable();
        return cube;
    }
    public static QuadrilateralGenerator CreateCube(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                    @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int size){
        QuadrilateralGenerator cube = new QuadrilateralGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, size, size, size);
        cube.Enable();
        return cube;
    }

    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               boolean replaceableOnly, int radius){
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, radius);
        sphere.Enable();
        return sphere;
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               int maxHardness, int radius) {
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, radius);
        sphere.Enable();
        return sphere;
    }
    public static SphereGenerator CreateSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int radius){
        SphereGenerator sphere = new SphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, radius);
        sphere.Enable();
        return sphere;
    }

    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                           boolean replaceableOnly, int radius, int thickness, boolean trueHollow){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                replaceableOnly, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                           int maxHardness, int radius, int thickness, boolean trueHollow) {
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                maxHardness, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                           @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                           int radius, int thickness, boolean trueHollow){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                whitelist, blacklist, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }

    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                               Vec3 pos, boolean hydrophobic, boolean replaceableOnly,
                                                                               int radius, int thickness, double buildChance, boolean trueHollow){
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                replaceableOnly, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                               Vec3 pos, boolean hydrophobic, int maxHardness,
                                                                               int radius, int thickness, double buildChance, boolean trueHollow) {
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                maxHardness, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                               Vec3 pos, boolean hydrophobic,
                                                                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                                               int radius, int thickness, double buildChance, boolean trueHollow){
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                whitelist, blacklist, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }

    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, boolean replaceableOnly,
                                                 Vec3 start, Vec3 end, boolean hydrophobic){
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end, hydrophobic);
        line.Enable();
        return line;
    }
    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, int maxHardness,
                                                 Vec3 start, Vec3 end, boolean hydrophobic) {
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, maxHardness, start, end, hydrophobic);
        line.Enable();
        return line;
    }
    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                 @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                 Vec3 start, Vec3 end, boolean hydrophobic){
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, hydrophobic);
        line.Enable();
        return line;
    }

    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             boolean replaceableOnly, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                             int weight){
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly,
                start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }
    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             int maxHardness, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                             int weight) {
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes, maxHardness,
                start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }
    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight){
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes,
                whitelist, blacklist, start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }

    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             boolean replaceableOnly, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                             int weight){
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly,
                start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }
    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             int maxHardness, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                             int weight) {
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes, maxHardness,
                start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }
    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                             @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                                             Vec3 start, Vec3 end, boolean hydrophobic,
                                                                             int weight){
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes,
                whitelist, blacklist, start, end, hydrophobic, weight);
        line.Enable();
        return line;
    }

    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                                   boolean replaceableOnly, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                                   int weight, int thickness){
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes, replaceableOnly,
                start, end, hydrophobic, weight, thickness);
        line.Enable();
        return line;
    }
    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                                   int maxHardness, Vec3 start, Vec3 end, boolean hydrophobic,
                                                                                   int weight, int thickness) {
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes, maxHardness,
                start, end, hydrophobic, weight, thickness);
        line.Enable();
        return line;
    }
    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                                                                   @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                                                   Vec3 start, Vec3 end, boolean hydrophobic,
                                                                                   int weight, int thickness){
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes,
                whitelist, blacklist, start, end, hydrophobic, weight, thickness);
        line.Enable();
        return line;
    }

    public static TunnelGenerator createTunnel(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, boolean replaceableOnly,
                                               Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness){
        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes, replaceableOnly,
                start, end, hydrophobic, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }
    public static TunnelGenerator createTunnel(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, int maxHardness,
                                               Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes, maxHardness,
                start, end, hydrophobic, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }
    public static TunnelGenerator createTunnel(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                               Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness){
        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes,
                whitelist, blacklist, start, end, hydrophobic, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }

    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                 boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow){
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                replaceableOnly, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                                 int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                maxHardness, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,Vec3 pos, boolean hydrophobic,
                                                 @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                                 int radius, int thickness, double buildChance, boolean trueHollow){
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, hydrophobic,
                whitelist, blacklist, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
}
