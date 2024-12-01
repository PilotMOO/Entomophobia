package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.blocks.custom.MyiaticFleshBlock;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.StackingEffectBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoForgeEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof MyiaticBase && event.getLevel() instanceof ServerLevel server && server.getServer().isReady()){
            Entomophobia.activeData.AddToMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity LE) ||
                MyiaticBase.isInsideOfTargetBlacklist(LE)) return;

        if (event.getEntity() instanceof Animal animal){
            animal.targetSelector.addGoal(1, new AvoidEntityGoal<>(animal, MyiaticBase.class, 16, 1.0D, 1.3D));
        }
        else if (event.getEntity() instanceof AbstractVillager villager){
            villager.targetSelector.addGoal(1, new AvoidEntityGoal<>(villager, MyiaticBase.class, 16, 0.8D, 1.0D));
        }
    }
    @SubscribeEvent
    public static void HandleSwarmUnpacking(EntityJoinLevelEvent event){
        if (!(event.getLevel() instanceof ServerLevel s)) return;

        if (Entomophobia.activeSwarmData != null && Entomophobia.activeSwarmData.toUnpack.size() != 0){
            SwarmSaveData.CleanPackagedSwarms();
            if (s.getGameTime() > 200) Entomophobia.activeSwarmData.toUnpack.clear();
            for (SwarmSaveData.SwarmPackager.PackagedSwarm pSwarm : Entomophobia.activeSwarmData.toUnpack){
                if (pSwarm.awaitingApplication.size() != 0){
                    pSwarm.EvaluateQueuedApplications();
                }

                if (event.getEntity() instanceof MyiaticBase M){
                    if (M.getUUID().equals(pSwarm.captainUUID)){
                        pSwarm.UnpackSwarm(M);
                        return;
                    }
                    else{
                        if (pSwarm.UnpackAndAddUnit(M, true) != 0) return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        if (event.getLevel() instanceof ServerLevel EServer){
            if (!EServer.getServer().isRunning()) return;

            Entity E = event.getEntity();
            if (E instanceof MyiaticBase M){
                if (!M.isDeadOrDying()){
                    System.out.println("Adding " + M.getEncodeId() + " to storage!");
                    Entomophobia.activeData.AddToStorage(M.getEncodeId());
                }
                Entomophobia.activeData.RemoveFromMyiaticCount();
                System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
            }
        }
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof MyiaticBase M && M.level() instanceof ServerLevel){
            Entomophobia.activeData.RemoveFromMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
        }
    }


    private static ServerLevel server;
    public static ServerLevel getServer(){
        return server;
    }
    @SubscribeEvent
    public static void ServerStarting(ServerStartingEvent event){
        NestManager.setNestConstructionDetails();
        SwarmManager.setSwarmDetails();
        server = event.getServer().overworld();

        for (String ID : Config.NEST.mobs_from_flesh_blocks.get()){
            MyiaticFleshBlock.RegisterAsFleshBlockPest(ID);
        }
    }
    @SubscribeEvent
    public static void ServerStartDataSetup(ServerStartedEvent event){
        ServerLevel server = event.getServer().overworld();
        EntomoGeneralSaveData.SetActiveData(server);
        NestSaveData.SetActiveNestData(server);
        SwarmSaveData.SetActiveSwarmData(server);
        System.out.println("Amount of myiatics in storage: " + Entomophobia.activeData.getTotalInStorage());
    }
    @SubscribeEvent
    public static void PostServerCleanup(ServerStoppedEvent event){
        System.out.println("Clearing out all nests!");
        NestManager.ClearNests();
        System.out.println("Clearing out all swarms!");
        SwarmManager.PurgeAllSwarms();
    }



    @SubscribeEvent
    public static void PotionApplication(MobEffectEvent.Added event){
        MobEffectInstance oldEffect = event.getOldEffectInstance();
        MobEffectInstance newEffect = event.getEffectInstance();
        if (oldEffect != null && oldEffect.getEffect() instanceof StackingEffectBase stacking){
            LivingEntity target = event.getEntity();
            int CumulativeDuration = oldEffect.getDuration() + newEffect.getDuration();
            int amp = (int) Mth.absMax(oldEffect.getAmplifier(), newEffect.getAmplifier());

            target.removeEffect(oldEffect.getEffect());
            target.addEffect(new MobEffectInstance(oldEffect.getEffect(), CumulativeDuration, amp));
        }
    }

    @SubscribeEvent
    public static void MilkTheEvilCow(PlayerInteractEvent.EntityInteract event){
        if (event.getTarget() instanceof MyiaticCowEntity MCow){
            Player player = event.getEntity();
            if (player.getMainHandItem().is(Items.BUCKET)){
                player.level().playSound(MCow, MCow.blockPosition(), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.getMainHandItem().shrink(1);
                player.getInventory().add(new ItemStack(EntomoItems.POISONOUS_MILK.get()));
            }
        }
    }

    @SubscribeEvent
    public static void InvasionStartManager(TickEvent.ServerTickEvent event){
        Entomophobia.activeData.ageWorld();

        if (!EntomoGeneralSaveData.hasStarted() && EntomoGeneralSaveData.getWorldAge() > Config.SERVER.time_until_shit_gets_real.get()){
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                AABB spreadAABB = player.getBoundingBox().inflate(Config.SERVER.start_spread_aoe.get());
                List<? extends LivingEntity> nearbyInfectables = player.level().getEntitiesOfClass(LivingEntity.class, spreadAABB, (LivingEntity Le) -> EntomoDataManager.getConvertedFor(Le.getEncodeId()) != null);
                int amountInfected = 0;
                for (LivingEntity entity : nearbyInfectables){
                    if (amountInfected < nearbyInfectables.size() / 6){
                        entity.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), -1, 2));
                        amountInfected++;
                    }
                    else if (player.getRandom().nextDouble() < 0.15){
                        entity.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), -1, 2));
                        amountInfected++;
                    }
                }
            }

            event.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("entomophobia.system.infection_start"), false);
            Entomophobia.activeData.setHasStarted(true);
        }
    }
    @SubscribeEvent
    public static void NestTicker(TickEvent.ServerTickEvent event){
        if (EntomoGeneralSaveData.getWorldAge() % NestManager.getTickFrequency() == 0){
            NestManager.TickAllActiveNests();
        }
    }

    private static final int LoadRadius = 1;

    @SubscribeEvent
    public static void LoadCaptain(EntityEvent.EnteringSection event){
        Entity E = event.getEntity();
        if (!(E.level() instanceof ServerLevel)
                || !(E instanceof MyiaticBase M)
                || !event.didChunkChange()
                || !M.amITheCaptain()
                || event.getNewPos() == event.getOldPos()) return;

        ChunkPos captainCPos = M.chunkPosition();
        ArrayList<ChunkPos> loadedChunkTracker = new ArrayList<>();

        //Loading new chunks
        for (int x = -LoadRadius; x <= LoadRadius; x++){
            for (int z = -LoadRadius; z <= LoadRadius; z++){
                ChunkPos cPos = new ChunkPos(captainCPos.x + x, captainCPos.z + z);

                ForgeChunkManager.forceChunk((ServerLevel) M.level(), Entomophobia.MOD_ID, M,
                        cPos.x, cPos.z, true, false);
                loadedChunkTracker.add(cPos);
            }
        }
        //Unloading old chunks
        for (int x = -LoadRadius; x <= LoadRadius; x++){
            for (int z = -LoadRadius; z <= LoadRadius; z++){
                ChunkPos cPos = event.getOldPos().offset(x, 0, z).chunk();
                if (loadedChunkTracker.contains(cPos)) continue;

                ForgeChunkManager.forceChunk((ServerLevel) M.level(), Entomophobia.MOD_ID, M,
                        cPos.x, cPos.z, false, false);
            }
        }
    }
}