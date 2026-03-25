package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntoEntities;
import mod.pilot.entomophobia.entity.client.celestial.CarrioniteRenderer;
import mod.pilot.entomophobia.entity.client.celestial.CelestialCarrionRenderer;
import mod.pilot.entomophobia.entity.client.celestial.HiveHeartRenderer;
import mod.pilot.entomophobia.entity.client.myiatic.*;
import mod.pilot.entomophobia.entity.client.pheromones.PheromoneFrenzyRenderer;
import mod.pilot.entomophobia.entity.client.pheromones.PheromonePreyHuntRenderer;
import mod.pilot.entomophobia.entity.client.projectile.StringGrappleRenderer;
import mod.pilot.entomophobia.entity.client.truepest.CentipedePestRenderer;
import mod.pilot.entomophobia.entity.client.truepest.CockroachPestRenderer;
import mod.pilot.entomophobia.entity.client.truepest.GrubPestRenderer;
import mod.pilot.entomophobia.entity.client.truepest.SpiderPestRenderer;
import mod.pilot.entomophobia.particles.BloodDripParticle;
import mod.pilot.entomophobia.particles.EntoParticles;
import mod.pilot.entomophobia.particles.FlyParticle;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModManager {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntoEntities.MYIATIC_ZOMBIE.get(), MyiaticZombieRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_CREEPER.get(), MyiaticCreeperRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_SPIDER.get(), MyiaticSpiderRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_COW.get(), MyiaticCowRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_SHEEP.get(), MyiaticSheepRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_PIG.get(), MyiaticPigRenderer::new);
        event.registerEntityRenderer(EntoEntities.MYIATIC_CHICKEN.get(), MyiaticChickenRenderer::new);

        event.registerEntityRenderer(EntoEntities.CELESTIAL_CARRION.get(), CelestialCarrionRenderer::new);
        event.registerEntityRenderer(EntoEntities.HIVE_HEART.get(), HiveHeartRenderer::new);
        event.registerEntityRenderer(EntoEntities.CARRIONITE.get(), CarrioniteRenderer::new);

        event.registerEntityRenderer(EntoEntities.SPIDER_PEST.get(), SpiderPestRenderer::new);
        event.registerEntityRenderer(EntoEntities.GRUB_PEST.get(), GrubPestRenderer::new);
        event.registerEntityRenderer(EntoEntities.COCKROACH_PEST.get(), CockroachPestRenderer::new);
        event.registerEntityRenderer(EntoEntities.CENTIPEDE_PEST.get(), CentipedePestRenderer::new);

        event.registerEntityRenderer(EntoEntities.PREYHUNT.get(), PheromonePreyHuntRenderer::new);
        event.registerEntityRenderer(EntoEntities.FRENZY.get(), PheromoneFrenzyRenderer::new);

        event.registerEntityRenderer(EntoEntities.STRING_GRAPPLE.get(), StringGrappleRenderer::new);

        event.registerEntityRenderer(EntoEntities.CONGEALED_BLOOD.get(), ThrownItemRenderer::new);
        /*
        event.registerBlockEntityRenderer(EntomoBlockEntities.EXAMPLE_BLOCK_ENTITY.get(),
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ExampleBlockEntityRenderer());
         */
    }

    @SubscribeEvent
    public static void registerParticle(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(EntoParticles.FLY_PARTICLE.get(),
                FlyParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(EntoParticles.BLOOD_LAND_PARTICLE.get(),
                BloodDripParticle.LandProvider::new);
        Minecraft.getInstance().particleEngine.register(EntoParticles.BLOOD_FALL_PARTICLE.get(),
                BloodDripParticle.FallProvider::new);
        Minecraft.getInstance().particleEngine.register(EntoParticles.BLOOD_HANG_PARTICLE.get(),
                BloodDripParticle.HangProvider::new);
    }

    private static final String OverlayID = "entomo_text_overlay";
    @SubscribeEvent
    public static void registerOverlayTest(RegisterGuiOverlaysEvent event){
        TextOverlay.instance = new TextOverlay();
        event.registerBelowAll(OverlayID, TextOverlay.instance);
    }
}
