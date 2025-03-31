package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.*;
import mod.pilot.entomophobia.entity.truepest.CentipedePestEntity;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import mod.pilot.entomophobia.entity.truepest.SpiderPestEntity;
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

        event.put(EntomoEntities.CELESTIAL_CARRION.get(), CelestialCarrionEntity.createAttributes().build());
        event.put(EntomoEntities.HIVE_HEART.get(), HiveHeartEntity.createAttributes().build());

        event.put(EntomoEntities.SPIDER_PEST.get(), SpiderPestEntity.createAttributes().build());
        event.put(EntomoEntities.GRUB_PEST.get(), GrubPestEntity.createAttributes().build());
        event.put(EntomoEntities.COCKROACH_PEST.get(), CockroachPestEntity.createAttributes().build());
        event.put(EntomoEntities.CENTIPEDE_PEST.get(), CentipedePestEntity.createAttributes().build());
    }
}
