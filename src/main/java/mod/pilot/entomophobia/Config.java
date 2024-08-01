package mod.pilot.entomophobia;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static class Server{
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklisted_targets;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> myiatic_conversion_list;
        public final ForgeConfigSpec.ConfigValue<Integer> myiatic_convert_timer;

        public Server(ForgeConfigSpec.Builder builder){
            builder.push("General knowledge");
            builder.pop();
            builder.push("1 second = 20 ticks");
            builder.pop();
            builder.push("Mod ID: entomophobia");
            builder.pop();
            builder.push("Mob IDs:");
            builder.push(new ArrayList<>(Arrays.asList(
                "Myiatics:",
                    "myiatic_zombie",
                    "-",
                "Evolved Forms:",
                    "-",
                "Pheromones:",
                    "pheromone_prey/hunt"
            )));
            builder.pop(2);

            builder.push("Mob Targeting");
            this.blacklisted_targets = builder.defineList("Mobs the Myiatic Ignore",
                    Lists.newArrayList(
                            "minecraft:creeper","minecraft:squid","minecraft:bat","minecraft:armor_stand") , o -> o instanceof String);            builder.push("Mob Targeting");
            this.myiatic_conversion_list = builder.defineList("Mobs and their Myiatic forms [key = \"Base>Myiatic\"",
                    Lists.newArrayList(
                            "minecraft:zombie>entomophobia:myiatic_zombie") , o -> o instanceof String);
            builder.pop();

            builder.push("General Infection values");
            this.myiatic_convert_timer = builder.define("Time (in ticks) for Myiasis to convert mobs",
                    4000);
        }
    }

    static {
        Pair<Server, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = commonSpecPair.getLeft();
        SERVER_SPEC = commonSpecPair.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}
