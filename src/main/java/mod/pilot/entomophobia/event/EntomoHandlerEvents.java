package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoHandlerEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof MyiaticBase && event.getLevel() instanceof ServerLevel server && server.getServer().isReady()){
            Entomophobia.activeData.AddToMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
        }
        else if (event.getEntity() instanceof Animal animal){
            animal.targetSelector.addGoal(1, new AvoidEntityGoal<>(animal, MyiaticBase.class, 16, 1.0D, 1.3D));
        }
        else if (event.getEntity() instanceof AbstractVillager villager){
            villager.targetSelector.addGoal(1, new AvoidEntityGoal<>(villager, MyiaticBase.class, 16, 0.8D, 1.0D));
        }
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof MyiaticBase M && M.level() instanceof ServerLevel){
            Entomophobia.activeData.RemoveFromMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        if (event.getLevel() instanceof ServerLevel EServer){
            if (!EServer.getServer().isReady()) return;

            Entity E = event.getEntity();
            if (E instanceof MyiaticBase M){
                if (!M.isDeadOrDying()){
                    System.out.println("Adding " + M.getEncodeId() + " to storage!");
                    Entomophobia.activeData.AddToStorage(M.getEncodeId());
                    Entomophobia.activeData.RemoveFromMyiaticCount();
                    System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
                }
                else{
                    Entomophobia.activeData.RemoveFromMyiaticCount();
                    System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
                }
            }
        }
    }

    @SubscribeEvent
    public static void ServerStart(ServerStartedEvent event){
        EntomoGeneralSaveData.SetActiveData(event.getServer().overworld());
        NestSaveData.SetActiveNestData(event.getServer().overworld());
        System.out.println("Amount of myiatics in storage: " + Entomophobia.activeData.GetTotalInStorage());
    }
    @SubscribeEvent
    public static void ServerStarting(ServerStartingEvent event){
        NestManager.setNestConstructionDetails();
        SwarmManager.setSwarmDetails();
        server = event.getServer().overworld();
    }

    private static ServerLevel server;
    public static ServerLevel getServer(){
        return server;
    }

    @SubscribeEvent
    public static void PostServerEnd(ServerStoppedEvent event){
        System.out.println("Clearing out all nests!");
        NestManager.ClearNests();
    }

    @SubscribeEvent
    public static void PotionApplication(MobEffectEvent.Added event){
        MobEffectInstance oldEffect = event.getOldEffectInstance();
        MobEffectInstance newEffect = event.getEffectInstance();
        if (oldEffect != null && oldEffect.getEffect() instanceof StackingEffectBase stacking){
            LivingEntity target = event.getEntity();
            int CumulativeDuration = oldEffect.getDuration() + newEffect.getDuration();
            int amp = (int)Mth.absMax(oldEffect.getAmplifier(), newEffect.getAmplifier());

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
                List<? extends LivingEntity> nearbyInfectables = player.level().getEntitiesOfClass(LivingEntity.class, spreadAABB, (LivingEntity Le) -> EntomoDataManager.GetConvertedFor(Le.getEncodeId()) != null);
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
}
