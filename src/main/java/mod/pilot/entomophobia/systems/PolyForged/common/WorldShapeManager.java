package mod.pilot.entomophobia.systems.PolyForged.common;

import mod.pilot.entomophobia.systems.PolyForged.Shapes.*;
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
        UNDEFINED,
        PosX,
        NegX,
        PosY,
        NegY,
        PosZ,
        NegZ,
    }
    public static boolean isPositive(Axis toCheck){
        return toCheck.ordinal() % 2 == 1;
    }
    public static boolean isPositive(byte toCheck){
        return toCheck % 2 == 1;
    }
    public static boolean isX(byte check){
        return check > 0 && check < 3;
    }
    public static boolean isY(byte check){
        return check > 2 && check < 5;
    }
    public static boolean isZ(byte check){
        return check > 4 && check < 7;
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

    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, Axis excluded){
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, excluded);
        circle.Enable();
        return circle;
    }
    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, Axis excluded) {
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius, excluded);
        circle.Enable();
        return circle;
    }
    public static CircleGenerator CreateCircle(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius, Axis excluded){
        CircleGenerator circle = new CircleGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, excluded);
        circle.Enable();
        return circle;
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

    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness, boolean trueHollow){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness, boolean trueHollow) {
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static HollowSphereGenerator CreateHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes,Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius, int thickness, boolean trueHollow){
        HollowSphereGenerator sphere = new HollowSphereGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness, trueHollow);
        sphere.Enable();
        return sphere;
    }

    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow){
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }
    public static RandomizedHollowSphereGenerator CreateRandomizedHollowSphere(ServerLevel server, double buildSpeed, List<BlockState> blockTypes,Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius, int thickness, double buildChance, boolean trueHollow){
        RandomizedHollowSphereGenerator sphere = new RandomizedHollowSphereGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness, buildChance, trueHollow);
        sphere.Enable();
        return sphere;
    }

    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end){
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end);
        line.Enable();
        return line;
    }
    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end) {
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, maxHardness, start, end);
        line.Enable();
        return line;
    }
    public static VectorLineGenerator CreateLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, Vec3 start, Vec3 end){
        VectorLineGenerator line = new VectorLineGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end);
        line.Enable();
        return line;
    }

    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight){
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight);
        line.Enable();
        return line;
    }
    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight) {
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes, maxHardness, start, end, weight);
        line.Enable();
        return line;
    }
    public static WeightedSquareVectorLineGenerator CreateWeightedSquareLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight){
        WeightedSquareVectorLineGenerator line = new WeightedSquareVectorLineGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight);
        line.Enable();
        return line;
    }

    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight){
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight);
        line.Enable();
        return line;
    }
    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight) {
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes, maxHardness, start, end, weight);
        line.Enable();
        return line;
    }
    public static WeightedCircleVectorLineGenerator CreateWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight){
        WeightedCircleVectorLineGenerator line = new WeightedCircleVectorLineGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight);
        line.Enable();
        return line;
    }

    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight, int thickness){
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight, thickness);
        line.Enable();
        return line;
    }
    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight, int thickness) {
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes, maxHardness, start, end, weight, thickness);
        line.Enable();
        return line;
    }
    public static HollowWeightedCircleLineGenerator CreateHollowWeightedCircleLine(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight, int thickness){
        HollowWeightedCircleLineGenerator line = new HollowWeightedCircleLineGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight, thickness);
        line.Enable();
        return line;
    }

    public static TunnelGenerator CreateTunnel(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight, int thickness){
        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }
    public static TunnelGenerator CreateTunnel(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight, int thickness) {
        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes, maxHardness, start, end, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }
    public static TunnelGenerator CreateTunnel(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight, int thickness){

        TunnelGenerator tunnel = new TunnelGenerator(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight, thickness);
        tunnel.Enable();
        return tunnel;
    }

    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow){
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
    public static ChamberGenerator CreateChamber(ServerLevel server, double buildSpeed, List<BlockState> blockTypes,Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius, int thickness, double buildChance, boolean trueHollow){
        ChamberGenerator chamber = new ChamberGenerator(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness, buildChance, trueHollow);
        chamber.Enable();
        return chamber;
    }
}
