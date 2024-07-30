package mod.pilot.entomophobia;

import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.client.myiatic.MyiaticZombieRenderer;
import mod.pilot.entomophobia.entity.client.pheromones.PheromonePreyHuntRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CilentManager {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntomoEntities.MYIATIC_ZOMBIE.get(), MyiaticZombieRenderer::new);
        event.registerEntityRenderer(EntomoEntities.PREYHUNT.get(), PheromonePreyHuntRenderer::new);

        /*
        event.registerBlockEntityRenderer(EntomoBlockEntities.EXAMPLE_BLOCK_ENTITY.get(),
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ExampleBlockEntityRenderer());
         */
    }
}
