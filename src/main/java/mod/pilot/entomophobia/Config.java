package mod.pilot.entomophobia;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final NestConfig NEST;
    public static final ForgeConfigSpec NEST_SPEC;

    public static class Server{
        public final ForgeConfigSpec.ConfigValue<Integer> time_until_shit_gets_real;
        public final ForgeConfigSpec.ConfigValue<Integer> start_spread_aoe;

        public final ForgeConfigSpec.ConfigValue<Integer> mob_cap;
        public final ForgeConfigSpec.ConfigValue<Integer> distance_to_player_until_despawn;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklisted_targets;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> myiatic_conversion_list;
        public final ForgeConfigSpec.ConfigValue<Integer> myiatic_convert_timer;

        public final ForgeConfigSpec.ConfigValue<Integer> hunt_bonus_range;

        public final ForgeConfigSpec.ConfigValue<Integer> myiatic_creeper_explode_radius;

        public final ForgeConfigSpec.ConfigValue<Integer> base_swarm_max_members;


        public Server(ForgeConfigSpec.Builder builder){
            builder.push("Entomophobia Config");

            builder.push("Mob Targeting");
            this.blacklisted_targets = builder.defineList("Mobs the Myiatic Ignore",
                    Lists.newArrayList(
                            "minecraft:squid","minecraft:bat","minecraft:armor_stand") , o -> o instanceof String);
            this.myiatic_conversion_list = builder.defineList("Mobs and their Myiatic forms [key = \"Base>Myiatic\"]",
                    Lists.newArrayList(
                            "minecraft:zombie>entomophobia:myiatic_zombie", "minecraft:creeper>entomophobia:myiatic_creeper",
                            "minecraft:spider>entomophobia:myiatic_spider", "minecraft:cow>entomophobia:myiatic_cow",
                            "minecraft:sheep>entomophobia:myiatic_sheep", "minecraft:pig>entomophobia:myiatic_pig",
                            "minecraft:chicken>entomophobia:myiatic_chicken") , o -> o instanceof String);
            builder.pop();

            builder.push("General Infection values");
            this.time_until_shit_gets_real = builder.defineInRange("Time, in ticks, until the infestation starts", 48000, 0, Integer.MAX_VALUE);
            this.start_spread_aoe = builder.defineInRange("How far from each player the Myiasis effect will spread once the infection starts", 200, 0, Integer.MAX_VALUE);
            this.mob_cap = builder.defineInRange("Max amount of mobs allowed in the world at once until encouraged despawning", 50, 0, Integer.MAX_VALUE);
            this.distance_to_player_until_despawn = builder.defineInRange("Distance from the closest player until despawning is encouraged", 128, 0, Integer.MAX_VALUE);
            this.myiatic_convert_timer = builder.define("Time (in ticks) for Myiasis to convert mobs",
                    600);
            builder.pop();

            builder.push("Swarm Configuration");
            this.base_swarm_max_members = builder.defineInRange("Maximum amount of members inside of a base swarm", 20, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Pheromone values");
            this.hunt_bonus_range = builder.defineInRange("Pheromone Prey/Hunt bonus searchrange", 64, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Myiatic Stats");
            this.myiatic_creeper_explode_radius = builder.defineInRange("Myiatic Creeper explosion range", 3, 1, Integer.MAX_VALUE);
            builder.pop();
        }
    }
    public static class NestConfig{
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> nest_build_materials;
        public final ForgeConfigSpec.ConfigValue<Integer> nest_tick_frequency;
        public final ForgeConfigSpec.ConfigValue<Double> nest_build_speed;
        public final ForgeConfigSpec.ConfigValue<Integer> nest_max_hardness;
        public final ForgeConfigSpec.ConfigValue<Integer> small_chamber_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> small_chamber_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> small_chamber_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_chamber_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_chamber_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_chamber_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> large_chamber_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> large_chamber_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> large_chamber_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> max_nest_layers;
        public final ForgeConfigSpec.ConfigValue<Integer> small_corridor_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> small_corridor_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> small_corridor_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_corridor_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_corridor_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> medium_corridor_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> large_corridor_min_size;
        public final ForgeConfigSpec.ConfigValue<Integer> large_corridor_max_size;
        public final ForgeConfigSpec.ConfigValue<Integer> large_corridor_thickness;
        public final ForgeConfigSpec.ConfigValue<Integer> min_corridor_length;
        public final ForgeConfigSpec.ConfigValue<Integer> max_corridor_length;
        public final ForgeConfigSpec.ConfigValue<Integer> max_corridor_extension;
        public final ForgeConfigSpec.ConfigValue<Double> corridor_extension_chance;
        public final ForgeConfigSpec.ConfigValue<Integer> nest_y_build_priority;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> mobs_from_flesh_blocks;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> nest_spawn_messages;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> nest_joke_spawn_messages;
        public final ForgeConfigSpec.ConfigValue<Boolean> enable_joke_messages;
        public final ForgeConfigSpec.ConfigValue<Double> joke_message_chance;


        public NestConfig(ForgeConfigSpec.Builder builder){
            builder.comment("DO NOT PROCEED UNLESS YOU KNOW WHAT YOU ARE DOING!");
            builder.comment("IT IS VERY EASY TO ENTIRELY BREAK THE NEST SYSTEM OR CAUSE A LOT OF PERFORMANCE ISSUES IF YOU ARE NOT CAREFUL");

            builder.push("Entomophobia Nest Config");

            builder.push("General Nest Configuration");
            this.nest_build_materials = builder.defineList("Blocks nests are built out of (key: \"modid:block;weight\") NOTE! Weights are how commonly that block appears, it is NOT X out of 100, a weight of 50 is NOT 50%, it's a fraction of the total cumulative weights (Ergo weight 10 out of a total of 50 is 20% [weight/total = percentChance%/100])",
                    Lists.newArrayList(
                            "entomophobia:myiatic_flesh;95",
                            "entomophobia:rooted_myiatic_flesh;5"
                    ), o -> o instanceof String);
            this.nest_tick_frequency = builder.defineInRange("The frequency at which nests tick (lower = faster, 1 is every tick, 2 is every other, etc)", 100, 1, Integer.MAX_VALUE);
            this.nest_build_speed = builder.defineInRange("The rate at which the nests build per build tick (Supports decimals but any decimal position higher than point 5 will be rounded up)", 20, 0, Double.MAX_VALUE);
            this.nest_max_hardness = builder.defineInRange("Maximum hardness of blocks that the nest can replace while building", 5, 0, Integer.MAX_VALUE);
            this.max_nest_layers = builder.defineInRange("The maximum amount of layers a given branch of a nest can generate", 2, 1, Integer.MAX_VALUE);
            this.nest_y_build_priority = builder.defineInRange("The Y level at which nests will prioritize building up or down if below or above, respectfully", 0, -64, 320);

            this.mobs_from_flesh_blocks = builder.defineList("Mobs that have a small chance to spawn from breaking myiatic flesh blocks",
                    Lists.newArrayList(
                            "minecraft:silverfish"
                    ), o -> o instanceof String);

            builder.push("Nest Message Configuration");
            this.nest_spawn_messages = builder.defineList("Messages displayed in chat when a nest is formed",
                    Lists.newArrayList(
                            "You feel the world pulse as if something alive is buried nearby...",
                            "Something foul takes root...",
                            "The sickly-sweet smell of rot emanates from the ground",
                            "It grows.",
                            "A faint yet aggressive rustling permeates throughout the area"
                    ), o -> o instanceof String);
            this.enable_joke_messages = builder.define("should there be a a small chance to select and display joke messages for nests forming?",
                    false);
            this.joke_message_chance = builder.defineInRange("Chance for a joke message to be displayed instead of a normal one (if enabled)",
                    0.05, 0, 1);
            this.nest_joke_spawn_messages = builder.defineList("Messages with a very small chance to be displayed in chat when a nest is formed " +
                            "(joke messages)",
                    Lists.newArrayList(
                            "It's Joever",
                            "NEST!!!!!",
                            "Erm what the sigma",
                            "Fuck It Wii Ball™",
                            "*Gulp* it's right behind me, isn't it?",
                            "Ah lads not again."
                    ), o -> o instanceof String);
            builder.pop(2);

            builder.push("Chamber Configuration");
            builder.push("Small Chamber Configuration");
            this.small_chamber_min_size = builder.defineInRange("Minimum radius size for small chambers", 7, 0, Integer.MAX_VALUE);
            this.small_chamber_max_size = builder.defineInRange("Maximum radius size for small chambers", 9, 0, Integer.MAX_VALUE);
            this.small_chamber_thickness = builder.defineInRange("Thickness of the walls for small chambers", 1, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Medium Chamber Configuration");
            this.medium_chamber_min_size = builder.defineInRange("Minimum radius size for medium chambers", 8, 0, Integer.MAX_VALUE);
            this.medium_chamber_max_size = builder.defineInRange("Maximum radius size for medium chambers", 10, 0, Integer.MAX_VALUE);
            this.medium_chamber_thickness = builder.defineInRange("Thickness of the walls for medium chambers", 2, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Large Chamber Configuration");
            this.large_chamber_min_size = builder.defineInRange("Minimum radius size for large chambers", 10, 0, Integer.MAX_VALUE);
            this.large_chamber_max_size = builder.defineInRange("Maximum radius size for large chambers", 12, 0, Integer.MAX_VALUE);
            this.large_chamber_thickness = builder.defineInRange("Thickness of the walls for large chambers", 3, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.pop();

            builder.push("Corridor Configuration");

            builder.push("General Corridor Configuration");
            this.min_corridor_length = builder.defineInRange("Minimum length of corridors", 16, 1, Integer.MAX_VALUE);
            this.max_corridor_length = builder.defineInRange("Maximum length of corridors", 48, 1, Integer.MAX_VALUE);
            this.max_corridor_extension = builder.defineInRange("Max amount of corridor extensions one corridor can have", 3, 1, Integer.MAX_VALUE);
            this.corridor_extension_chance = builder.defineInRange("Chance for a corridor to get an extension", 0.4, 0, 1);
            builder.pop();

            builder.push("Small Corridor Configuration");
            this.small_corridor_min_size = builder.defineInRange("Minimum radius size for small corridors", 9, 0, Integer.MAX_VALUE);
            this.small_corridor_max_size = builder.defineInRange("Maximum radius size for small corridors", 10, 0, Integer.MAX_VALUE);
            this.small_corridor_thickness = builder.defineInRange("Thickness of the walls for small corridors", 2, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Medium Chamber Configuration");
            this.medium_corridor_min_size = builder.defineInRange("Minimum radius size for medium corridors", 9, 0, Integer.MAX_VALUE);
            this.medium_corridor_max_size = builder.defineInRange("Maximum radius size for medium corridors", 11, 0, Integer.MAX_VALUE);
            this.medium_corridor_thickness = builder.defineInRange("Thickness of the walls for medium corridors", 2, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Large Chamber Configuration");
            this.large_corridor_min_size = builder.defineInRange("Minimum radius size for large corridors", 11, 0, Integer.MAX_VALUE);
            this.large_corridor_max_size = builder.defineInRange("Maximum radius size for large corridors", 13, 0, Integer.MAX_VALUE);
            this.large_corridor_thickness = builder.defineInRange("Thickness of the walls for large corridors", 3, 1, Integer.MAX_VALUE);
            builder.pop(3);
        }
    }


    static {
        Pair<Server, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = commonSpecPair.getLeft();
        SERVER_SPEC = commonSpecPair.getRight();

        Pair<NestConfig, ForgeConfigSpec> nestSpecPair = new ForgeConfigSpec.Builder().configure(NestConfig::new);
        NEST = nestSpecPair.getLeft();
        NEST_SPEC = nestSpecPair.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}