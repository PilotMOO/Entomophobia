package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntoEntities;
import mod.pilot.entomophobia.entity.celestial.CarrioniteEntity;
import mod.pilot.entomophobia.entity.celestial.CelestialCarrionEntity;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.*;
import mod.pilot.entomophobia.entity.testing.SkeletonPuppetTestEntity;
import mod.pilot.entomophobia.entity.truepest.CentipedePestEntity;
import mod.pilot.entomophobia.entity.truepest.CockroachPestEntity;
import mod.pilot.entomophobia.entity.truepest.GrubPestEntity;
import mod.pilot.entomophobia.entity.truepest.SpiderPestEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntoEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntoEntities.MYIATIC_ZOMBIE.get(), MyiaticZombieEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_CREEPER.get(), MyiaticCreeperEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_SPIDER.get(), MyiaticSpiderEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_COW.get(), MyiaticCowEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_SHEEP.get(), MyiaticSheepEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_PIG.get(), MyiaticPigEntity.createAttributes().build());
        event.put(EntoEntities.MYIATIC_CHICKEN.get(), MyiaticChickenEntity.createAttributes().build());

        event.put(EntoEntities.CELESTIAL_CARRION.get(), CelestialCarrionEntity.createAttributes().build());
        event.put(EntoEntities.HIVE_HEART.get(), HiveHeartEntity.createAttributes().build());
        event.put(EntoEntities.CARRIONITE.get(), CarrioniteEntity.createAttributes().build());

        event.put(EntoEntities.SPIDER_PEST.get(), SpiderPestEntity.createAttributes().build());
        event.put(EntoEntities.GRUB_PEST.get(), GrubPestEntity.createAttributes().build());
        event.put(EntoEntities.COCKROACH_PEST.get(), CockroachPestEntity.createAttributes().build());
        event.put(EntoEntities.CENTIPEDE_PEST.get(), CentipedePestEntity.createAttributes().build());

        event.put(EntoEntities.TEST_PUPPET.get(), SkeletonPuppetTestEntity.createAttributes().build());
    }
}
