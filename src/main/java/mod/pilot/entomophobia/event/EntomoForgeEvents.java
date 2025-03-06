package mod.pilot.entomophobia.event;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.blocks.custom.BloodwaxProtrusions;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.IStackingEffect;
import mod.pilot.entomophobia.entity.PestManager;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.truepest.PestBase;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.world.ForgeChunkManager;
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

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoForgeEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof MyiaticBase  && !(event.getEntity() instanceof PestBase)
                && event.getLevel() instanceof ServerLevel s && s.getServer().isReady()){
            Entomophobia.activeData.AddToMyiaticCount();
            System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity LE) ||
                MyiaticBase.isInsideOfTargetBlacklist(LE)) return;

        if (event.getEntity() instanceof Animal animal){
            animal.targetSelector.addGoal(1, new AvoidEntityGoal<>(animal, MyiaticBase.class, EntomoForgeEvents::NotCarrion,
                    16, 1.0D, 1.3D, (e) -> true));
        }
        else if (event.getEntity() instanceof AbstractVillager villager){
            villager.targetSelector.addGoal(1, new AvoidEntityGoal<>(villager, MyiaticBase.class, EntomoForgeEvents::NotCarrion,
                    16, 0.8D, 1.0D, (e) -> true));
        }
    }
    private static boolean NotCarrion(LivingEntity le){
        return !(le instanceof CelestialCarrionEntity);
    }


    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        if (event.getLevel() instanceof ServerLevel EServer){
            if (!EServer.getServer().isRunning()) return;

            Entity E = event.getEntity();
            if (E instanceof MyiaticBase M && !(E instanceof PestBase)){
                if (!M.isDeadOrDying()){
                    System.out.println("Adding " + M.getEncodeId() + " to storage!");
                    Entomophobia.activeData.AddToStorage(M.getEncodeId());
                }
                Entomophobia.activeData.RemoveFromMyiaticCount();
                System.out.println("MyiaticCount is " + EntomoGeneralSaveData.GetMyiaticCount());
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

    private static ServerLevel server;
    public static ServerLevel getServer(){
        return server;
    }
    @SubscribeEvent
    public static void ServerStarting(ServerStartingEvent event){
        NestManager.setNestConstructionDetails();
        SwarmManager.setSwarmDetails();
        PestManager.RegisterAll();
        BloodwaxProtrusions.registerAllPriorityBlocks();

        server = event.getServer().overworld();
    }
    @SubscribeEvent
    public static void ServerDataSetup(ServerStartedEvent event){
        ServerLevel server = event.getServer().overworld();
        EntomoGeneralSaveData.SetActiveData(server);
        NestSaveData.SetActiveNestData(server);
        SwarmSaveData.SetActiveSwarmData(server);
        System.out.println("Amount of myiatics in storage: " + Entomophobia.activeData.getTotalInStorage());
    }
    @SubscribeEvent
    public static void PostServerCleanup(ServerStoppedEvent event){
        System.out.println("[NEST MANAGER] Clearing out all nests!");
        NestManager.ClearNests();
        System.out.println("[SWARM MANAGER] Clearing out all swarms!");
        SwarmManager.PurgeAllSwarms();
        System.out.println("[PEST MANAGER] Clearing out all registered pests!");
        PestManager.FlushList();
    }



    @SubscribeEvent
    public static void StackingPotionApplication(MobEffectEvent.Added event){
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
    public static void StackingPotionExpiration(MobEffectEvent.Expired event){
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && effect.getEffect() instanceof IStackingEffect stacking
                && stacking.isDegradable() && effect.getAmplifier() > 0){
            event.getEntity().removeEffect(effect.getEffect());
            event.getEntity().addEffect(new MobEffectInstance(effect.getEffect(),
                    stacking.getDegradeDuration(), effect.getAmplifier() - 1));
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
    private static int nextSwitch = 0;
    @SubscribeEvent
    public static void OverlayTicker(TickEvent.ServerTickEvent event){
        if (nextSwitch == 0){
            regenerateOverlayHashmap();
            nextSwitch = 20 + random.nextInt(-10, 50);
        } else nextSwitch--;
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

    private static final ResourceLocation OVERSTIM_EFFECT_OVERLAY = new ResourceLocation(Entomophobia.MOD_ID,
            "textures/gui/overstimulated_heart_overlay.png");
    private static final ResourceLocation NEURO_EFFECT_OVERLAY = new ResourceLocation(Entomophobia.MOD_ID,
            "textures/gui/neuro_heart_overlays.png");
    private static final NeuroHeartOverlayPackage[] overlays = new NeuroHeartOverlayPackage[10];
    public static void regenerateOverlayHashmap(){
        for (int i = 0; i < 10; i++){
            overlays[i] = NeuroHeartOverlayPackage.generateRandom();
        }
    }

    private static final RandomSource random = RandomSource.create();


    @SubscribeEvent
    public static void disableHeartRendering(RenderGuiOverlayEvent.Pre event){
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                && Minecraft.getInstance().gameMode.canHurtPlayer()
                && Minecraft.getInstance().getCameraEntity() instanceof Player player
                && player.hasEffect(EntomoMobEffects.NEUROINTOXICATION.get())){
            int leftHeight = 39;
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            int left = width / 2 - 91;
            int top = height - leftHeight;
            event.getGuiGraphics().enableScissor(left, top, left - 81, top - 9);
        }
    }
    @SubscribeEvent
    public static void renderEffectOverlays(RenderGuiOverlayEvent.Post event){
        //This is directly stolen from Alex's Caves irradiated heart rendering.
        // Credit where credit is due, thank you Mr Alex for having a public GitHub, that was a godsend
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                && Minecraft.getInstance().gameMode.canHurtPlayer()
                && Minecraft.getInstance().getCameraEntity() instanceof Player player) {

            //Neurointox. overlay management
            if (player.hasEffect(EntomoMobEffects.NEUROINTOXICATION.get())) {
                event.getGuiGraphics().disableScissor();

                int leftHeight = 39;
                int width = event.getWindow().getGuiScaledWidth();
                int height = event.getWindow().getGuiScaledHeight();
                int forgeGuiTick = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? forgeGui.getGuiTicks() : 0;
                float healthMax = 20;

                int rowHeight = 11;

                int left = width / 2 - 91;
                int top = height - leftHeight;
                int regen = -1;
                if (player.hasEffect(MobEffects.REGENERATION)) {
                    regen = forgeGuiTick % Mth.ceil(healthMax + 5.0F);
                }

                event.getGuiGraphics().pose().pushPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, NEURO_EFFECT_OVERLAY);
                for (int i = 9; i >= 0; --i) {
                    NeuroHeartOverlayPackage nPackage = overlays[i];
                    int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                    int x = left + i % 10 * 8;
                    int y = top - row * rowHeight;
                    if (nPackage.shaking) {
                        y += random.nextInt(2);
                    }
                    if (i == regen) {
                        y -= 2;
                    }

                    if (nPackage.isSolo){
                        //Blit Solo
                        event.getGuiGraphics().blit(NEURO_EFFECT_OVERLAY, x, y, 50,
                                nPackage.soloH ,NeuroHeartOverlayPackage.soloV,
                                9, 9, 81, 81);
                    }
                    else{
                        //Blit background
                        event.getGuiGraphics().blit(NEURO_EFFECT_OVERLAY, x, y, 50,
                                nPackage.heartBackH ,NeuroHeartOverlayPackage.heartBackV,
                                9, 9, 81, 81);
                        //Blit base
                        event.getGuiGraphics().blit(NEURO_EFFECT_OVERLAY, x, y, 50,
                                nPackage.heartBaseH, nPackage.heartBaseV,
                                nPackage.halved ? 5 : 9, 9, 81, 81);
                        //Blit overlay
                        event.getGuiGraphics().blit(NEURO_EFFECT_OVERLAY, x, y, 50,
                                nPackage.heartOverlayH, NeuroHeartOverlayPackage.heartOverlayV,
                                9, 9, 81, 81);
                    }
                }
                event.getGuiGraphics().blit(NEURO_EFFECT_OVERLAY, left, top, 50,
                        0, 45, 81, 9, 81, 81);

                event.getGuiGraphics().pose().popPose();
            }
            //Over. Stim. overlay management
            else if (player.hasEffect(EntomoMobEffects.OVERSTIMULATION.get())){
                int leftHeight = 39;
                int width = event.getWindow().getGuiScaledWidth();
                int height = event.getWindow().getGuiScaledHeight();
                int health = Mth.ceil(player.getHealth());
                int forgeGuiTick = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? forgeGui.getGuiTicks() : 0;
                AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
                float healthMax = (float) attrMaxHealth.getValue();
                float absorb = (float) Math.ceil(player.getAbsorptionAmount());

                int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
                int rowHeight = Math.max(10 - (healthRows - 2), 3);

                //ClientProxy.random.setSeed(forgeGuiTick * 312871L);
                int left = width / 2 - 91;
                int top = height - leftHeight;
                int regen = -1;
                if (player.hasEffect(MobEffects.REGENERATION)) {
                    regen = forgeGuiTick % Mth.ceil(healthMax + 5.0F);
                }
                final int heartV = player.level().getLevelData().isHardcore() ? 9 : 0;
                int heartU = 0;
                float absorbRemaining = absorb;
                event.getGuiGraphics().pose().pushPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, OVERSTIM_EFFECT_OVERLAY);
                for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                    int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                    int x = left + i % 10 * 8;
                    int y = top - row * rowHeight;
                    if (health <= 4) {
                        y += random.nextInt(2);
                    }
                    if (i == regen) {
                        y -= 2;
                    }
                    event.getGuiGraphics().blit(OVERSTIM_EFFECT_OVERLAY, x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
                    if (absorbRemaining > 0.0F) {
                        if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                            event.getGuiGraphics().blit(OVERSTIM_EFFECT_OVERLAY, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                            absorbRemaining -= 1.0F;
                        } else {
                            event.getGuiGraphics().blit(OVERSTIM_EFFECT_OVERLAY, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                            absorbRemaining -= 2.0F;
                        }
                    } else {
                        if (i * 2 + 1 < health) {
                            event.getGuiGraphics().blit(OVERSTIM_EFFECT_OVERLAY, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                        } else if (i * 2 + 1 == health) {
                            event.getGuiGraphics().blit(OVERSTIM_EFFECT_OVERLAY, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                        }
                    }
                }
                event.getGuiGraphics().pose().popPose();
            }
        }
    }

    private static class NeuroHeartOverlayPackage{
        private static final RandomSource random = RandomSource.create();

        public static final int heartBackV = 9 * 2;
        public final int heartBackH;
        public final int heartBaseV;
        public final int heartBaseH;
        public static final int heartOverlayV = 9 * 3;
        public final int heartOverlayH;
        public final boolean isSolo;
        public static final int soloV = 9 * 4;
        public final int soloH;
        public final boolean shaking;
        public final boolean halved;

        private NeuroHeartOverlayPackage(int soloH, boolean shaking) {
            this(-1, -1, -1, -1, true, soloH, shaking, false);
        }
        private NeuroHeartOverlayPackage(int heartBackH, int heartBaseV, int heartBaseH, int heartOverlayH, boolean shaking, boolean halved) {
            this(heartBackH, heartBaseV, heartBaseH, heartOverlayH, false, -1, shaking, halved);
        }
        private NeuroHeartOverlayPackage(int heartBackH, int heartBaseV, int heartBaseH, int heartOverlayH,
                                         boolean isSolo, int soloH, boolean shaking, boolean halved) {
            this.heartBackH = heartBackH;
            this.heartBaseV = heartBaseV;
            this.heartBaseH = heartBaseH;
            this.heartOverlayH = heartOverlayH;
            this.isSolo = isSolo;
            this.soloH = soloH;
            this.shaking = shaking;
            this.halved = halved;
        }

        public static NeuroHeartOverlayPackage generateRandom(){
            NeuroHeartOverlayPackage toReturn;

            boolean shaking = random.nextInt(4) == 0;
            boolean halved = random.nextBoolean();
            boolean isSolo = random.nextDouble() < 0.1;
            if (isSolo) toReturn = new NeuroHeartOverlayPackage(random.nextInt(2) * 9, shaking);
            else{
                toReturn = new NeuroHeartOverlayPackage(
                        random.nextInt(3) * 9,
                        random.nextInt(2) * 9,
                        random.nextInt(5) * 9,
                        random.nextInt(3) * 9, shaking, halved);
            }
            return toReturn;
        }
    }
}
