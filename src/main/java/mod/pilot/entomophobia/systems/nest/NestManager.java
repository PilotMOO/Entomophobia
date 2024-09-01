package mod.pilot.entomophobia.systems.nest;

import mod.pilot.entomophobia.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class NestManager {
    public enum NestStates{
        disabled,
        active,
        fully_formed,
        dead
    }

    private static final ArrayList<Nest> ActiveNests = new ArrayList<>();
    public static Nest ConstructNewNest(ServerLevel server, Vec3 start){
        Nest nest = new Nest(server, start, getTickFrequency());
        ActiveNests.add(nest);
        return nest;
    }
    public static void TickAllActiveNests(){
        ArrayList<Nest> nestsToDiscard = new ArrayList<>();
        for (Nest nest : ActiveNests){
            if (!nest.Alive()){
                nestsToDiscard.add(nest);
                continue;
            }
            nest.NestTick();
        }
        for (Nest toRemove : nestsToDiscard){
            ActiveNests.remove(toRemove);
        }
    }

    public static void setNestConstructionDetails(){
        TickFrequency = Config.SERVER.nest_tick_frequency.get();
        NestBuildSpeed = Config.SERVER.nest_build_speed.get();
        NestMaxHardness = Config.SERVER.nest_max_hardness.get();
        NestMaxLayers = Config.SERVER.max_nest_layers.get();

        NestBlocks = new ArrayList<>();
        for (String blockID : Config.SERVER.nest_build_materials.get()){
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockID));
            if (block != null){
                NestBlocks.add(block.defaultBlockState());
            }
        }

        NestSmallChamberMinRadius = Config.SERVER.small_chamber_min_size.get();
        NestSmallChamberMaxRadius = Config.SERVER.small_chamber_max_size.get();
        NestSmallChamberThickness = Config.SERVER.small_chamber_thickness.get();

        NestMediumChamberMinRadius = Config.SERVER.medium_chamber_min_size.get();
        NestMediumChamberMaxRadius = Config.SERVER.medium_chamber_max_size.get();
        NestMediumChamberThickness = Config.SERVER.medium_chamber_thickness.get();

        NestLargeChamberMinRadius = Config.SERVER.large_chamber_min_size.get();
        NestLargeChamberMaxRadius = Config.SERVER.large_chamber_max_size.get();
        NestLargeChamberThickness = Config.SERVER.large_chamber_thickness.get();

        NestMinCorridorLength = Config.SERVER.min_corridor_length.get();
        NestMaxCorridorLength = Config.SERVER.max_corridor_length.get();
    }
    private static int TickFrequency;
    public static int getTickFrequency(){
        return TickFrequency;
    }
    private static double NestBuildSpeed;
    public static double getNestBuildSpeed(){
        return NestBuildSpeed;
    }
    private static int NestMaxHardness;
    public static int getNestMaxHardness(){
        return NestMaxHardness;
    }
    private static int NestMaxLayers;
    public static int getNestMaxLayers(){
        return NestMaxLayers;
    }
    private static ArrayList<BlockState> NestBlocks;
    public static ArrayList<BlockState> getNestBlocks(){
        return NestBlocks;
    }

    private static int NestSmallChamberMinRadius;
    private static int NestSmallChamberMaxRadius;
    public static int getNestSmallChamberMinRadius() {
        return NestSmallChamberMinRadius;
    }
    public static int getNestSmallChamberMaxRadius() {
        return NestSmallChamberMaxRadius;
    }
    public static int getRandomSmallChamberRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestSmallChamberMinRadius(), getNestSmallChamberMaxRadius());
    }
    private static int NestSmallChamberThickness;
    public static int getNestSmallChamberThickness() {
        return NestSmallChamberThickness;
    }

    private static int NestMediumChamberMinRadius;
    private static int NestMediumChamberMaxRadius;
    public static int getNestMediumChamberMinRadius() {
        return NestMediumChamberMinRadius;
    }
    public static int getNestMediumChamberMaxRadius() {
        return NestMediumChamberMaxRadius;
    }
    public static int getRandomMediumChamberRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestMediumChamberMinRadius(), getNestMediumChamberMaxRadius());
    }
    private static int NestMediumChamberThickness;
    public static int getNestMediumChamberThickness() {
        return NestMediumChamberThickness;
    }

    private static int NestLargeChamberMinRadius;
    private static int NestLargeChamberMaxRadius;
    public static int getNestLargeChamberMinRadius() {
        return NestLargeChamberMinRadius;
    }
    public static int getNestLargeChamberMaxRadius() {
        return NestLargeChamberMaxRadius;
    }
    public static int getRandomLargeChamberRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestLargeChamberMinRadius(), getNestLargeChamberMaxRadius());
    }
    private static int NestLargeChamberThickness;
    public static int getNestLargeChamberThickness() {
        return NestLargeChamberThickness;
    }

    private static int NestMinCorridorLength;
    private static int NestMaxCorridorLength;
    public static int getNestMinCorridorLength(){
        return NestMinCorridorLength;
    }
    public static int getNestMaxCorridorLength(){
        return NestMaxCorridorLength;
    }
    public static int getRandomCorridorLength(RandomSource random){
        return random.nextIntBetweenInclusive(getNestMinCorridorLength(), getNestMaxCorridorLength());
    }
}
