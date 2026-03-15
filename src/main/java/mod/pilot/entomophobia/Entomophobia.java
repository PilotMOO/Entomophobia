package mod.pilot.entomophobia;

import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.blocks.custom.BloodwaxProtrusions;
import mod.pilot.entomophobia.data.clientsyncing.EntomoPacketSyncer;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.Myiasis;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.PestManager;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.items.EntomoCreativeTabs;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.particles.EntomoParticles;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.data.worlddata.EntoGeneralSaveData;
import mod.pilot.entomophobia.systems.EventStart.EventStart;
import mod.pilot.entomophobia.systems.GenericModelRegistry.GenericModelHub;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.SkyboxModelManager;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.nest.features.FeatureManager;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Entomophobia.MOD_ID)
public class Entomophobia
{
    public static final String MOD_ID = "entomophobia";

    public static EntoGeneralSaveData activeData;
    public static NestSaveData activeNestData;
    public static HiveSaveData activeHiveData;
    public static SwarmSaveData activeSwarmData;

    public Entomophobia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(Entomophobia::FMLCommon);
        modEventBus.addListener(Entomophobia::FMLClient);

        EntomoItems.register(modEventBus);
        EntomoCreativeTabs.register(modEventBus);
        EntomoBlocks.register(modEventBus);
        EntomoSounds.register(modEventBus);
        EntomoMobEffects.register(modEventBus);
        EntomoEntities.register(modEventBus);
        EntomoParticles.register(modEventBus);

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SERVER_SPEC, "entomo_common_config.toml");
        ModConfig.loadConfig(ModConfig.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("entomo_common_config.toml").toString());

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.NEST_SPEC, "entomo_nest_config.toml");
        ModConfig.loadConfig(ModConfig.NEST_SPEC, FMLPaths.CONFIGDIR.get().resolve("entomo_nest_config.toml").toString());
    }

    @SubscribeEvent
    public static void FMLCommon(FMLCommonSetupEvent event){
        System.out.println("Hello from Ento FML Common!");
        EntomoPacketSyncer.registerPackets();

        EventStart.Server.setup();

        HiveNervousSystem.Manager.setup();
        FeatureManager.registerAllFeatures();

        BloodwaxProtrusions.registerAllPriorityBlocks();

        PestManager.registerAll();
        SwarmManager.populateNameHashmap();
        CelestialCarrionEntity.createVoices();

        loadConfigValues();
    }
    @SubscribeEvent
    public static void FMLClient(FMLClientSetupEvent event){
        System.out.println("Hello from Ento FML Client!");

        //wahh wahh
        //GenericModelHub.setup();
        //SkyboxModelManager.setup();

        TextOverlay.setup();

        //disabled cuz we dont need that rn
        //EventStart.Client.setup();
    }

    @SuppressWarnings("unchecked")
    private static void loadConfigValues(){
        System.out.println("Hello from Ento Config Loading!");
        NestManager.setNestConstructionDetails(ModConfig.NEST);

        EventStart.doomsDay = ModConfig.SERVER.doomsday.get();

        MyiaticBase.mobCap = ModConfig.SERVER.mob_cap.get();
        MyiaticBase.playerDespawnRange = ModConfig.SERVER.distance_to_player_until_despawn.get();
        MyiaticBase.blacklist = (java.util.List<String>) ModConfig.SERVER.blacklisted_targets.get();

        Myiasis.convertTime = ModConfig.SERVER.myiatic_convert_timer.get();
        Myiasis.pestCap = ModConfig.SERVER.local_pest_cap.get();
        Myiasis.pestCapDistance = ModConfig.SERVER.pest_cap_distance.get();

        SwarmManager.baseSwarmMaxSize = ModConfig.SERVER.base_swarm_max_members.get();
    }
}
