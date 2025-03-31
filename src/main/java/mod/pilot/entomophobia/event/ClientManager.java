package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
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
import mod.pilot.entomophobia.particles.EntomoParticles;
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
public class ClientManager {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntomoEntities.MYIATIC_ZOMBIE.get(), MyiaticZombieRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_CREEPER.get(), MyiaticCreeperRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_SPIDER.get(), MyiaticSpiderRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_COW.get(), MyiaticCowRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_SHEEP.get(), MyiaticSheepRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_PIG.get(), MyiaticPigRenderer::new);
        event.registerEntityRenderer(EntomoEntities.MYIATIC_CHICKEN.get(), MyiaticChickenRenderer::new);

        event.registerEntityRenderer(EntomoEntities.CELESTIAL_CARRION.get(), CelestialCarrionRenderer::new);
        event.registerEntityRenderer(EntomoEntities.HIVE_HEART.get(), HiveHeartRenderer::new);

        event.registerEntityRenderer(EntomoEntities.SPIDER_PEST.get(), SpiderPestRenderer::new);
        event.registerEntityRenderer(EntomoEntities.GRUB_PEST.get(), GrubPestRenderer::new);
        event.registerEntityRenderer(EntomoEntities.COCKROACH_PEST.get(), CockroachPestRenderer::new);
        event.registerEntityRenderer(EntomoEntities.CENTIPEDE_PEST.get(), CentipedePestRenderer::new);

        event.registerEntityRenderer(EntomoEntities.PREYHUNT.get(), PheromonePreyHuntRenderer::new);
        event.registerEntityRenderer(EntomoEntities.FRENZY.get(), PheromoneFrenzyRenderer::new);

        event.registerEntityRenderer(EntomoEntities.STRING_GRAPPLE.get(), StringGrappleRenderer::new);

        event.registerEntityRenderer(EntomoEntities.CONGEALED_BLOOD.get(), ThrownItemRenderer::new);
        /*
        event.registerBlockEntityRenderer(EntomoBlockEntities.EXAMPLE_BLOCK_ENTITY.get(),
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ExampleBlockEntityRenderer());
         */
    }

    @SubscribeEvent
    public static void registerParticle(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(EntomoParticles.FLY_PARTICLE.get(),
                FlyParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(EntomoParticles.BLOOD_LAND_PARTICLE.get(),
                BloodDripParticle.LandProvider::new);
        Minecraft.getInstance().particleEngine.register(EntomoParticles.BLOOD_FALL_PARTICLE.get(),
                BloodDripParticle.FallProvider::new);
        Minecraft.getInstance().particleEngine.register(EntomoParticles.BLOOD_HANG_PARTICLE.get(),
                BloodDripParticle.HangProvider::new);
    }

    private static final String OverlayID = "entomo_text_overlay";
    @SubscribeEvent
    public static void registerOverlayTest(RegisterGuiOverlaysEvent event){
        TextOverlay.instance = new TextOverlay();
        event.registerBelowAll(OverlayID, TextOverlay.instance);
    }
}
