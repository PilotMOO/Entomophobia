package mod.pilot.entomophobia.systems.nest;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class NestManager {
    public enum NestStates{
        disabled,
        active,
        fully_formed,
        dead
    }

    private static final RandomSource random = RandomSource.create();

    private static final ArrayList<Nest> ActiveNests = new ArrayList<>();
    public static void addToActiveNests(Nest toAdd){
        ActiveNests.add(toAdd);
        NestSaveData.Dirty();
    }
    public static int AmountOfActiveNests(){
        return ActiveNests.size();
    }
    public static ArrayList<Nest> getActiveNests(){
        return new ArrayList<>(ActiveNests);
    }
    public static void ClearNests(){
        ActiveNests.clear();
    }
    public static Nest ConstructNewNest(ServerLevel server, Vec3 start, boolean quiet){
        Nest nest = new Nest(server, start);
        addToActiveNests(nest);
        NestSaveData.Dirty();
        if (!quiet){
            server.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal(getRandomNestMessageForChatDisplay(server.getRandom())), false);
        }
        return nest;
    }
    public static Nest ConstructFromBlueprint(ServerLevel server, Vec3 start, byte state, Nest.Chamber mainChamber){
        Nest toReturn = Nest.ConstructFromBlueprint(server, start, state, mainChamber);
        addToActiveNests(toReturn);
        return toReturn;
    }
    public static void TickAllActiveNests(){
        ArrayList<Nest> nestsToDiscard = new ArrayList<>();
        for (Nest nest : ActiveNests){
            if (nest.Dead()){
                nestsToDiscard.add(nest);
                continue;
            }
            nest.NestTick();
        }
        for (Nest toRemove : nestsToDiscard){
            ActiveNests.remove(toRemove);
        }
    }
    public static void ActivateAllNests() {
        for (Nest nest : ActiveNests){
            if (nest.Dead()) continue;
            nest.Enable();
        }
    }

    public static Vec3 getNewNestPosition(Vec3 start, int offsetScale, boolean YOnly){
        int xOffset = YOnly ? 0 : random.nextIntBetweenInclusive(-offsetScale, offsetScale);
        int yOffset = random.nextIntBetweenInclusive(-offsetScale, offsetScale);
        int zOffset = YOnly ? 0 : random.nextIntBetweenInclusive(-offsetScale, offsetScale);
        return new Vec3(start.x + xOffset, getNestYBuildPriority() + yOffset, start.z + zOffset);
    }

    public static void setNestConstructionDetails(){
        TickFrequency = Config.NEST.nest_tick_frequency.get();
        NestBuildSpeed = Config.NEST.nest_build_speed.get();
        NestMaxHardness = Config.NEST.nest_max_hardness.get();
        NestMaxLayers = Config.NEST.max_nest_layers.get();

        HashMap<BlockState, Integer> NestBlocksHashmap = new HashMap<>();
        for (String ConfigEntry : Config.NEST.nest_build_materials.get()){
            String[] split = ConfigEntry.split(";");
            System.out.println("Splicing " + ConfigEntry + "into " + split[0] + " and " + split[1]);
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0]));
            int weight = Integer.parseInt(split[1]);
            System.out.println("Weight for " + block + " is " + weight);
            if (block != null){
                NestBlocksHashmap.put(block.defaultBlockState(), weight);
            }
        }
        NestBlocks = new GeneratorBlockPacket(NestBlocksHashmap, 10);

        NestSmallChamberMinRadius = Config.NEST.small_chamber_min_size.get();
        NestSmallChamberMaxRadius = Config.NEST.small_chamber_max_size.get();
        NestSmallChamberThickness = Config.NEST.small_chamber_thickness.get();

        NestMediumChamberMinRadius = Config.NEST.medium_chamber_min_size.get();
        NestMediumChamberMaxRadius = Config.NEST.medium_chamber_max_size.get();
        NestMediumChamberThickness = Config.NEST.medium_chamber_thickness.get();

        NestLargeChamberMinRadius = Config.NEST.large_chamber_min_size.get();
        NestLargeChamberMaxRadius = Config.NEST.large_chamber_max_size.get();
        NestLargeChamberThickness = Config.NEST.large_chamber_thickness.get();

        NestSmallCorridorMinRadius = Config.NEST.small_corridor_min_size.get();
        NestSmallCorridorMaxRadius = Config.NEST.small_corridor_max_size.get();
        NestSmallCorridorThickness = Config.NEST.small_corridor_thickness.get();

        NestMediumCorridorMinRadius = Config.NEST.medium_corridor_min_size.get();
        NestMediumCorridorMaxRadius = Config.NEST.medium_corridor_max_size.get();
        NestMediumCorridorThickness = Config.NEST.medium_corridor_thickness.get();

        NestLargeCorridorMinRadius = Config.NEST.large_corridor_min_size.get();
        NestLargeCorridorMaxRadius = Config.NEST.large_corridor_max_size.get();
        NestLargeCorridorThickness = Config.NEST.large_corridor_thickness.get();

        NestMinCorridorLength = Config.NEST.min_corridor_length.get();
        NestMaxCorridorLength = Config.NEST.max_corridor_length.get();
        NestMaxCorridorExtensions = Config.NEST.max_corridor_extension.get();

        NestCorridorExtensionChance = Config.NEST.corridor_extension_chance.get();

        NestYBuildPriority = Config.NEST.nest_y_build_priority.get();

        NestMessages = new ArrayList<>(Config.NEST.nest_spawn_messages.get());

        JokeMessagesEnabled = Config.NEST.enable_joke_messages.get();
        JokeMessageChance = Config.NEST.joke_message_chance.get();
        JokeNestMessages = new ArrayList<>(Config.NEST.nest_joke_spawn_messages.get());
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
    private static GeneratorBlockPacket NestBlocks;
    public static GeneratorBlockPacket getNestBlocks(){
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

    private static int NestSmallCorridorMinRadius;
    private static int NestSmallCorridorMaxRadius;
    public static int getNestSmallCorridorMinRadius() {
        return NestSmallCorridorMinRadius;
    }
    public static int getNestSmallCorridorMaxRadius() {
        return NestSmallCorridorMaxRadius;
    }
    public static int getRandomSmallCorridorRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestSmallCorridorMinRadius(), getNestSmallCorridorMaxRadius());
    }
    private static int NestSmallCorridorThickness;
    public static int getNestSmallCorridorThickness() {
        return NestSmallCorridorThickness;
    }

    private static int NestMediumCorridorMinRadius;
    private static int NestMediumCorridorMaxRadius;
    public static int getNestMediumCorridorMinRadius() {
        return NestMediumCorridorMinRadius;
    }
    public static int getNestMediumCorridorMaxRadius() {
        return NestMediumCorridorMaxRadius;
    }
    public static int getRandomMediumCorridorRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestMediumCorridorMinRadius(), getNestMediumCorridorMaxRadius());
    }
    private static int NestMediumCorridorThickness;
    public static int getNestMediumCorridorThickness() {
        return NestMediumCorridorThickness;
    }

    private static int NestLargeCorridorMinRadius;
    private static int NestLargeCorridorMaxRadius;
    public static int getNestLargeCorridorMinRadius() {
        return NestLargeCorridorMinRadius;
    }
    public static int getNestLargeCorridorMaxRadius() {
        return NestLargeCorridorMaxRadius;
    }
    public static int getRandomLargeCorridorRadius(RandomSource random){
        return random.nextIntBetweenInclusive(getNestLargeCorridorMinRadius(), getNestLargeCorridorMaxRadius());
    }
    private static int NestLargeCorridorThickness;
    public static int getNestLargeCorridorThickness() {
        return NestLargeCorridorThickness;
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

    private static int NestMaxCorridorExtensions;
    public static int getNestMaxCorridorExtensions(){
        return NestMaxCorridorExtensions;
    }

    private static double NestCorridorExtensionChance;
    public static double getNestCorridorExtensionChance() {
        return NestCorridorExtensionChance;
    }

    private static int NestYBuildPriority;
    public static int getNestYBuildPriority() {
        return NestYBuildPriority;
    }

    private static ArrayList<String> NestMessages;
    public static ArrayList<String> getNestMessages(){
        return NestMessages;
    }
    public static String getRandomNestMessage(RandomSource random){
        if (NestMessages == null || NestMessages.size() == 0) return "";
        return NestMessages.get(random.nextIntBetweenInclusive(0, NestMessages.size()));
    }

    private static boolean JokeMessagesEnabled;
    public static boolean isJokeMessagesEnabled(){
        return JokeMessagesEnabled;
    }

    private static double JokeMessageChance;
    public static double getJokeMessageChance(){
        return isJokeMessagesEnabled() ? JokeMessageChance : 0;
    }
    private static ArrayList<String> JokeNestMessages;
    public static ArrayList<String> getJokeNestMessages(){
        return JokeNestMessages;
    }
    public static String getRandomJokeNestMessage(RandomSource random){
        if (JokeNestMessages == null || JokeNestMessages.size() == 0 || !isJokeMessagesEnabled()) return "";
        return JokeNestMessages.get(random.nextIntBetweenInclusive(0, JokeNestMessages.size()));
    }

    public static String getRandomNestMessageForChatDisplay(RandomSource random){
        if (random.nextDouble() <= getJokeMessageChance()){
            return getRandomJokeNestMessage(random);
        }
        return getRandomNestMessage(random);
    }
}
