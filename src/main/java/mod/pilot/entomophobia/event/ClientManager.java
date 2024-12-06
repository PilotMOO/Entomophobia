package mod.pilot.entomophobia.event;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.client.myiatic.*;
import mod.pilot.entomophobia.entity.client.pheromones.PheromoneFrenzyRenderer;
import mod.pilot.entomophobia.entity.client.pheromones.PheromonePreyHuntRenderer;
import mod.pilot.entomophobia.entity.client.projectile.StringGrappleRenderer;
import mod.pilot.entomophobia.particles.EntomoParticles;
import mod.pilot.entomophobia.particles.FlyParticle;
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
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
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

        event.registerEntityRenderer(EntomoEntities.PREYHUNT.get(), PheromonePreyHuntRenderer::new);
        event.registerEntityRenderer(EntomoEntities.FRENZY.get(), PheromoneFrenzyRenderer::new);

        event.registerEntityRenderer(EntomoEntities.STRING_GRAPPLE.get(), StringGrappleRenderer::new);

        /*
        event.registerBlockEntityRenderer(EntomoBlockEntities.EXAMPLE_BLOCK_ENTITY.get(),
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ExampleBlockEntityRenderer());
         */
    }

    @SubscribeEvent
    public static void registerParticle(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(EntomoParticles.FLY_PARTICLE.get(),
                FlyParticle.Provider::new);
    }
}
