package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.ModConfig;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.damagetypes.EntoDamageTypes;
import mod.pilot.entomophobia.data.EntoDataManager;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.effects.EntoMobEffects;
import mod.pilot.entomophobia.effects.IStackingEffect;
import mod.pilot.entomophobia.entity.PestManager;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.truepest.PestBase;
import mod.pilot.entomophobia.items.EntoItems;
import mod.pilot.entomophobia.data.worlddata.EntoGeneralSaveData;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.EnumArgument;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntoForgeEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        Entity E = event.getEntity();
        if (E instanceof HiveHeartEntity hh) {
            BlockPos entityPos = hh.blockPosition();
            ForgeChunkManager.forceChunk(server, Entomophobia.MOD_ID, hh,
                    entityPos.getX() >> 16, entityPos.getZ() >> 16,
                    true, false);
        }
        else if (E instanceof MyiaticBase  && !(E instanceof PestBase)
                && event.getLevel() instanceof ServerLevel s && s.getServer().isReady()){
            EntoGeneralSaveData.activeData().addToMyiaticCount();
            //System.out.println("MyiaticCount is " + EntomoGeneralSaveData.getMyiaticCount());
            return;
        }

        if (!(E instanceof LivingEntity LE) ||
                MyiaticBase.isInsideOfTargetBlacklist(LE)) return;

        if (E instanceof Animal animal){
            animal.targetSelector.addGoal(1, new AvoidEntityGoal<>(animal, MyiaticBase.class, EntoForgeEvents::notCarrion,
                    16, 1.0D, 1.3D, (e) -> true));
        }
        else if (E instanceof AbstractVillager villager){
            villager.targetSelector.addGoal(1, new AvoidEntityGoal<>(villager, MyiaticBase.class, EntoForgeEvents::notCarrion,
                    16, 0.8D, 1.0D, (e) -> true));
        }
    }
    private static boolean notCarrion(LivingEntity le){
        return !(le instanceof CelestialCarrionEntity);
    }


    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        if (event.getLevel() instanceof ServerLevel EServer){
            if (!EServer.getServer().isRunning()) return;

            Entity E = event.getEntity();
            if (E instanceof HiveHeartEntity hh){
                BlockPos entityPos = hh.blockPosition();
                ForgeChunkManager.forceChunk(server, Entomophobia.MOD_ID, hh,
                        entityPos.getX() >> 16, entityPos.getZ() >> 16,
                        false, false);
            }
            else if (E instanceof MyiaticBase M && !(E instanceof PestBase)){
                if (!M.isDeadOrDying()){

                    Pair<HiveSaveData.Packet, HiveHeartEntity> pair = HiveSaveData.locateClosestDataAndAccessor(M.position());
                    HiveSaveData.Packet packet = pair.getA();
                    if (packet != null){
                        packet.addToStorage(M).thenSync(pair.getB());
                    }
                }
                EntoGeneralSaveData.activeData().removeFromMyiaticCount();
            }
        }
    }
    /*@SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof MyiaticBase M && M.level() instanceof ServerLevel){
            Entomophobia.activeData.RemoveFromMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
        }
    }*/
    @SubscribeEvent
    public static void handleSwarmUnpacking(EntityJoinLevelEvent event){
        if (!(event.getLevel() instanceof ServerLevel s)) return;

        SwarmSaveData.assertValidData();
        if (Entomophobia.activeSwarmData != null && !Entomophobia.activeSwarmData.toUnpack.isEmpty()){
            SwarmSaveData.cleanPackagedSwarms();
            if (s.getGameTime() > 200) Entomophobia.activeSwarmData.toUnpack.clear();
            for (SwarmSaveData.SwarmPackager.PackagedSwarm pSwarm : Entomophobia.activeSwarmData.toUnpack){
                if (!pSwarm.awaitingApplication.isEmpty()){
                    pSwarm.evaluateQueuedApplications();
                }

                if (event.getEntity() instanceof MyiaticBase M){
                    if (M.getUUID().equals(pSwarm.captainUUID)){
                        pSwarm.unpackSwarm(M);
                        return;
                    }
                    else if (pSwarm.unpackAndAddUnit(M, true) != 0) return;
                }
            }
        }
    }

    private static ServerLevel server;
    public static ServerLevel getServer(){
        return server;
    }
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event){
        server = event.getServer().overworld();
    }
    @SubscribeEvent
    public static void serverDataSetup(ServerStartedEvent event){
        ServerLevel server = event.getServer().overworld();
        EntoGeneralSaveData.setActiveData(server);
        NestSaveData.setActiveNestData(server);
        HiveSaveData.setActiveHiveData(server);
        SwarmSaveData.setActiveSwarmData(server);

        EntoDamageTypes.buildMultitranslatables(server);
    }
    @SubscribeEvent
    public static void postServerCleanup(ServerStoppedEvent event){
        System.out.println("[NEST MANAGER] Clearing out all nests!");
        NestManager.clearNests();
        System.out.println("[SWARM MANAGER] Clearing out all swarms!");
        SwarmManager.purgeAllSwarms();
        System.out.println("[PEST MANAGER] Clearing out all registered pests!");
        PestManager.flushList();
    }



    @SubscribeEvent
    public static void stackingPotionApplication(MobEffectEvent.Added event){
        MobEffectInstance oldEffect = event.getOldEffectInstance();
        MobEffectInstance newEffect = event.getEffectInstance();
        if (oldEffect != null && oldEffect.getEffect() instanceof IStackingEffect stacking){
            LivingEntity target = event.getEntity();
            int cumulativeDuration = oldEffect.getDuration() + newEffect.getDuration();
            int amp = (int) Mth.absMax(oldEffect.getAmplifier(), newEffect.getAmplifier());
            while (cumulativeDuration > stacking.getWrapAroundThreshold()){
                if (stacking.hasCap() && stacking.getMaxCap() <= amp) break;

                cumulativeDuration -= stacking.getWrapAroundThreshold();
                amp++;
            }
            if (!stacking.hasCap() || stacking.canDurationExtendIfCapped() || stacking.getMaxCap() > amp){
                cumulativeDuration = Math.max(cumulativeDuration, stacking.getMinimumWrapDuration());
            }
            else{
                cumulativeDuration = Math.min(stacking.getWrapAroundThreshold(),
                        Math.max(cumulativeDuration, stacking.getMinimumWrapDuration()));
            }

            target.removeEffect(oldEffect.getEffect());
            target.addEffect(new MobEffectInstance(oldEffect.getEffect(), cumulativeDuration, amp));
        }
    }
    @SubscribeEvent
    public static void stackingPotionExpiration(MobEffectEvent.Expired event){
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && effect.getEffect() instanceof IStackingEffect stacking
                && stacking.isDegradable() && effect.getAmplifier() > 0){
            event.getEntity().removeEffect(effect.getEffect());
            event.getEntity().addEffect(new MobEffectInstance(effect.getEffect(),
                    stacking.getDegradeDuration(), effect.getAmplifier() - 1));
        }
    }

    //This is dumb, I should have used the interact method inside of the entity itself, but I didn't know about that when making this lmao. oops
    @SubscribeEvent
    public static void milkTheEvilCow(PlayerInteractEvent.EntityInteract event){
        if (event.getTarget() instanceof MyiaticCowEntity MCow){
            Player player = event.getEntity();
            if (player.getMainHandItem().is(Items.BUCKET)){
                player.level().playSound(MCow, MCow.blockPosition(), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.getMainHandItem().shrink(1);
                player.getInventory().add(new ItemStack(EntoItems.POISONOUS_MILK.get()));
            }
        }
    }

    @SubscribeEvent
    public static void invasionStartManager(TickEvent.ServerTickEvent event){
        EntoGeneralSaveData.activeData().ageWorld();

        if (!EntoGeneralSaveData.hasStarted() && EntoGeneralSaveData.getWorldAge() > ModConfig.SERVER.time_until_shit_gets_real.get()){
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                AABB spreadAABB = player.getBoundingBox().inflate(ModConfig.SERVER.start_spread_aoe.get());
                List<? extends LivingEntity> nearbyInfectables = player.level().getEntitiesOfClass(LivingEntity.class, spreadAABB, (LivingEntity Le) -> EntoDataManager.getConvertedFor(Le.getEncodeId()) != null);
                int amountInfected = 0;
                for (LivingEntity entity : nearbyInfectables){
                    if (amountInfected < nearbyInfectables.size() / 6){
                        entity.addEffect(new MobEffectInstance(EntoMobEffects.MYIASIS.get(), -1, 2));
                        amountInfected++;
                    }
                    else if (player.getRandom().nextDouble() < 0.15){
                        entity.addEffect(new MobEffectInstance(EntoMobEffects.MYIASIS.get(), -1, 2));
                        amountInfected++;
                    }
                }
            }

            event.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("entomophobia.system.infection_start"), false);
            Entomophobia.activeData.setHasStarted(true);
        }
    }
    @SubscribeEvent
    public static void nestTicker(TickEvent.ServerTickEvent event){
        if (EntoGeneralSaveData.getWorldAge() % NestManager.getTickFrequency() == 0){
            NestManager.tickAllActiveNests();
        }
    }

    private static final int loadRadius = 1;

    @SubscribeEvent
    public static void loadCaptain(EntityEvent.EnteringSection event){
        Entity E = event.getEntity();
        if (!(E.level() instanceof ServerLevel serverLevel)
                || !(E instanceof MyiaticBase M)
                || !event.didChunkChange()
                || !M.amITheCaptain()
                || event.getNewPos() == event.getOldPos()) return;

        ChunkPos captainCPos = M.chunkPosition();
        ArrayList<ChunkPos> loadedChunkTracker = new ArrayList<>();

        //Loading new chunks
        for (int x = -loadRadius; x <= loadRadius; x++){
            for (int z = -loadRadius; z <= loadRadius; z++){
                ChunkPos cPos = new ChunkPos(captainCPos.x + x, captainCPos.z + z);

                ForgeChunkManager.forceChunk(serverLevel, Entomophobia.MOD_ID, M,
                        cPos.x, cPos.z, true, false);
                loadedChunkTracker.add(cPos);
            }
        }
        //Unloading old chunks
        for (int x = -loadRadius; x <= loadRadius; x++){
            for (int z = -loadRadius; z <= loadRadius; z++){
                ChunkPos cPos = event.getOldPos().offset(x, 0, z).chunk();
                if (loadedChunkTracker.contains(cPos)) continue;

                ForgeChunkManager.forceChunk(serverLevel, Entomophobia.MOD_ID, M,
                        cPos.x, cPos.z, false, false);
            }
        }
    }

    private static final String IDPrepend = Entomophobia.MOD_ID + ":";
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal(IDPrepend + "thanks")
                .then(Commands.argument("ToThank", EnumArgument.enumArgument(thanks.class))
                        .executes(arguments ->{
                    thanks t = arguments.getArgument("ToThank", thanks.class);
                    String print = switch (t){
                        case Pilot -> "§dThe creator of the mod, yeah I make a thanks command for myself :]";
                        case Moist -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for all of his feedback for the mod and giving me motivation to continue to work";
                        case Enter -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for feedback, ideas, and suggestions regarding the mod during early days of development";
                        case Chili -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for help with moderating the discord server as well as helping with the mod in a few ways";
                        case Isha -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for bug testing and giving feedback on private betas of the mod";
                        case Xplosion -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for early bug testing and playing of private betas";
                        case Wizzy -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for his endless spew of bug information for cooking up ideas for myiatic forms";
                        case Harby -> "§dSpecial thanks to " + t.id + " " + t.quote() + " for creating a majority of the sound effects in the mod as well as assisting with coding";
                    };
                    Entity e = arguments.getSource().getEntity();
                    if (e instanceof Player player){
                        player.displayClientMessage(Component.literal(print), false);
                        Level l = arguments.getSource().getLevel();
                        ItemEntity item = new ItemEntity(EntityType.ITEM, l);
                        item.setItem(new ItemStack(EntoItems.THANKS.get()));
                        item.moveTo(player.position());
                        l.addFreshEntity(item);
                        return 1;
                    } else {
                        System.err.println("[SPECIAL THANKS] Oops! Somehow, this command was ran by an entity that wasn't a player! How did that happen...");
                        return 0;
                    }
                })));

        event.getDispatcher().register(Commands.literal(IDPrepend + "nest")
                .then(Commands.argument("Position", Vec3Argument.vec3())
                        .executes(arguments ->{
                    Vec3 pos = arguments.getArgument("Position", Coordinates.class).getPosition(arguments.getSource());
                    NestManager.constructNewNest(arguments.getSource().getLevel(), pos);
                    if (arguments.getSource().getEntity() instanceof Player p){
                        p.displayClientMessage(Component.literal("Creating a new nest at " + pos), false);
                    }
                    return 1;
                })));

        event.getDispatcher().register(Commands.literal(IDPrepend + "nest_actions")
                .then(Commands.argument("ActionType", EnumArgument.enumArgument(ActionType.class))
                        .executes(arguments -> {
                    Vec3 from = arguments.getSource().getPosition();
                    Nest nest = NestManager.getClosestNest(from);

                    Player player = null;
                    if (arguments.getSource().getEntity() instanceof Player player1){
                        player = player1;
                    }
                    if (nest == null){
                        if (player != null) {
                            player.displayClientMessage(
                                    Component.literal(
                                            "§4Oops! Can't execute that command because there wasn't a nest to execute it on!"),
                                    false);
                        }
                        return -1;
                    }

                    switch (arguments.getArgument("ActionType", ActionType.class)){
                        case Kill -> {
                            nest.kill(true);
                            HiveHeartEntity hh;
                            if ((hh = nest.accessHiveHeart()) != null) hh.kill();
                            if (player != null) {
                                player.displayClientMessage(
                                        Component.literal(
                                                "§7Killing closest nest. Nest at " + nest.mainChamber.getPosition()),
                                        false);
                            }
                            return 1;
                        }
                        case Disable -> {
                            nest.disable();
                            if (player != null) {
                                player.displayClientMessage(
                                        Component.literal(
                                                "§7Disabling closest nest. Nest at " + nest.mainChamber.getPosition()),
                                        false);
                            }
                            return 1;
                        }
                        case Enable -> {
                            if (nest.getNestState() == 1){
                                if (player != null) {
                                    player.displayClientMessage(
                                            Component.literal("§4You can't enable a nest that's already enabled!"),
                                            false);
                                }
                                return -1;
                            }
                            nest.enable();
                            if (player != null) {
                                player.displayClientMessage(
                                        Component.literal("§7Enabled closest nest. Nest at " + nest.mainChamber.getPosition()),
                                        false);
                            }
                            return 1;
                        }
                        case Locate -> {
                            if (player != null){
                                player.displayClientMessage(
                                        Component.literal("§7Closest nest at: " + nest.mainChamber.getPosition()), false
                                );
                            }
                            HiveHeartEntity hh;
                            if ((hh = nest.accessHiveHeart()) != null){
                                hh.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600));
                            }
                            return 1;
                        }
                        case Read_Data -> {
                            HiveHeartEntity hh;
                            if (player == null) return -1;
                            else if ((hh = nest.accessHiveHeart()) == null){
                                player.displayClientMessage(Component.literal("§4Oops! This nest doesn't seem to have a living Hive Heart to access data from!"), false);
                                return -1;
                            }
                            player.displayClientMessage(
                                    Component.literal("§6_____[  ACCESSING NEST DATA  ]_____"), false
                            );
                            player.displayClientMessage(
                                    Component.literal(">§9 Accessing from logical §a" + (arguments.getSource().getLevel().isClientSide ? "CLIENT" : "SERVER")), false
                            );
                            player.displayClientMessage(
                                    Component.literal("§6[  NERVOUS SYSTEM  ]"), false
                            );
                            if (hh.nervousSystem != null){
                                player.displayClientMessage(
                                        Component.literal(">§9 UUID of hive heart: §a[ " + hh.nervousSystem.hiveHeartUUID + " ]"), false
                                );
                                player.displayClientMessage(
                                        Component.literal(">§9 Associated nest: §a[ " + hh.nervousSystem.nest + " ]"), false
                                );
                                player.displayClientMessage(
                                        Component.literal(">§9 ServerLevel: §a[ " + hh.nervousSystem.serverLevel + " ]"), false
                                );
                            } else {
                                player.displayClientMessage(
                                        Component.literal("§4--Hive heart did NOT have an active Nervous System!"), false
                                );
                            }

                            player.displayClientMessage(
                                    Component.literal("§6[  HIVE DATA PACKET  ]"), false
                            );
                            HiveSaveData.Packet packet = nest.accessData();
                            if (packet != null){
                                player.displayClientMessage(
                                        Component.literal(">§9 Corpsedew: §a[" + packet.corpseDew + "]"), false
                                );
                                player.displayClientMessage(
                                        Component.literal("§5--Saved entities: ["), false
                                );
                                for (String s : packet.storedEntities.keySet()){
                                    player.displayClientMessage(
                                            Component.literal(">§9 Entity §a" + s + "§9; count: §a" + packet.getCountInStorage(s)),
                                            false
                                    );
                                }
                                player.displayClientMessage(
                                        Component.literal("§5]"), false
                                );
                            }
                            else{
                                player.displayClientMessage(
                                        Component.literal("§4--Nest did NOT have an active data packet!"), false
                                );
                            }

                            player.displayClientMessage(
                                    Component.literal("§6_____[  END DATA  ]_____"), false
                            );
                            return 1;
                        }
                        default -> {
                            return -1;
                        }
                    }
                })));
    }
    private enum thanks{
        Pilot("pilotmoo"),
        Moist("thickmoistmeatshoes"),
        Enter("entergamer227"),
        Chili("cooldamian20"),
        Isha("ishax21"),
        Xplosion("bigxplosion"),
        Wizzy("wizzythewizkid"),
        Harby("harbinger5641");

        public String quote(){
            return "\"" + this.name() + "\"";
        }
        thanks(String commandID){
            this.id = commandID;
        }
        public final String id;
    }
    private enum ActionType{
        Kill, Disable, Enable, Locate, Read_Data
    }

}
