package mod.pilot.entomophobia;

import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.items.EntomoCreativeTabs;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.particles.EntomoParticles;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.systems.nest.features.FeatureManager;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Entomophobia.MOD_ID)
public class Entomophobia
{
    public static final String MOD_ID = "entomophobia";

    public static EntomoGeneralSaveData activeData;
    public static NestSaveData activeNestData;
    public static SwarmSaveData activeSwarmData;

    public Entomophobia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        EntomoItems.register(modEventBus);
        EntomoCreativeTabs.register(modEventBus);
        EntomoBlocks.register(modEventBus);
        EntomoSounds.register(modEventBus);
        EntomoMobEffects.register(modEventBus);
        EntomoEntities.register(modEventBus);
        EntomoParticles.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC, "entomo_common_config.toml");
        Config.loadConfig(Config.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("entomo_common_config.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.NEST_SPEC, "entomo_nest_config.toml");
        Config.loadConfig(Config.NEST_SPEC, FMLPaths.CONFIGDIR.get().resolve("entomo_nest_config.toml").toString());

        SwarmManager.PopulateNameHashmap();
        FeatureManager.RegisterAllFeatures();
        TextOverlay.Setup();
        CelestialCarrionEntity.CreateVoices();
    }
}
