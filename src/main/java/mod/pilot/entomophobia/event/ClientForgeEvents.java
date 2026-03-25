package mod.pilot.entomophobia.event;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.EntoMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static int nextSwitch = 0;
    @SubscribeEvent
    public static void overlayTicker(TickEvent.ClientTickEvent event){
        if (nextSwitch == 0){
            regenerateOverlayHashmap();
            nextSwitch = 20 + random.nextInt(-10, 50);
        } else nextSwitch--;
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

    @SubscribeEvent
    public static void disableHeartRendering(RenderGuiOverlayEvent.Pre event){
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                && Minecraft.getInstance().gameMode.canHurtPlayer()
                && Minecraft.getInstance().getCameraEntity() instanceof Player player
                && player.hasEffect(EntoMobEffects.NEUROINTOXICATION.get())){
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
            if (player.hasEffect(EntoMobEffects.NEUROINTOXICATION.get())) {
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
            else if (player.hasEffect(EntoMobEffects.OVERSTIMULATION.get())){
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

    private static final RandomSource random = RandomSource.create();
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
