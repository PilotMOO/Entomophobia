package mod.pilot.entomophobia;

import com.mojang.logging.LogUtils;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.items.EntomoCreativeTabs;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(Entomophobia.MOD_ID)
public class Entomophobia
{
    public static final String MOD_ID = "entomophobia";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static EntomoGeneralSaveData activeData;
    public static NestSaveData activeNestData;

    public Entomophobia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        EntomoItems.register(modEventBus);
        EntomoCreativeTabs.register(modEventBus);
        EntomoBlocks.register(modEventBus);
        EntomoSounds.register(modEventBus);
        EntomoMobEffects.register(modEventBus);
        EntomoEntities.register(modEventBus);

        //System.out.println("Color: " + getIntFromColor(255, 255, 255));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC, "Entomophobia_Config.toml");
        Config.loadConfig(Config.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("Entomophobia_Config.toml").toString());

        SwarmManager.PopulateNameHashmap();
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000;
        Green = (Green << 8) & 0x0000FF00;
        Blue = Blue & 0x000000FF;

        return 0xFF000000 | Red | Green | Blue;
    }
}
