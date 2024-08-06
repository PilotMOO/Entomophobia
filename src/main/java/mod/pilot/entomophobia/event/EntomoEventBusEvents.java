package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntomoEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntomoEntities.MYIATIC_ZOMBIE.get(), MyiaticZombieEntity.createAttributes().build());
        event.put(EntomoEntities.MYIATIC_CREEPER.get(), MyiaticCreeperEntity.createAttributes().build());
        event.put(EntomoEntities.PREYHUNT.get(), PheromonePreyHuntEntity.createAttributes().build());
        event.put(EntomoEntities.FRENZY.get(), PheromonePreyHuntEntity.createAttributes().build());
    }
}
