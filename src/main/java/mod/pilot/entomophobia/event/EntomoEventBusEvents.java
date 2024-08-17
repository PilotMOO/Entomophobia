package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.*;
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
        event.put(EntomoEntities.MYIATIC_SPIDER.get(), MyiaticSpiderEntity.createAttributes().build());
        event.put(EntomoEntities.MYIATIC_COW.get(), MyiaticCowEntity.createAttributes().build());
        event.put(EntomoEntities.MYIATIC_SHEEP.get(), MyiaticSheepEntity.createAttributes().build());
        event.put(EntomoEntities.MYIATIC_PIG.get(), MyiaticPigEntity.createAttributes().build());
        event.put(EntomoEntities.MYIATIC_CHICKEN.get(), MyiaticChickenEntity.createAttributes().build());
        event.put(EntomoEntities.PREYHUNT.get(), PheromonePreyHuntEntity.createAttributes().build());
        event.put(EntomoEntities.FRENZY.get(), PheromonePreyHuntEntity.createAttributes().build());
    }
}
