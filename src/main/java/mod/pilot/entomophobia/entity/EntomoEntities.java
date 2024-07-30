package mod.pilot.entomophobia.entity;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Entomophobia.MOD_ID);

    public static final RegistryObject<EntityType<MyiaticZombieEntity>> MYIATIC_ZOMBIE =
            ENTITY_TYPES.register("myiatic_zombie", () -> EntityType.Builder.of(MyiaticZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 2f).build("myiatic_zombie"));

    //Pheromone Entities
    public static final RegistryObject<EntityType<PheromonePreyHuntEntity>> PREYHUNT =
            ENTITY_TYPES.register("pheromone_prey/hunt", () -> EntityType.Builder.of(PheromonePreyHuntEntity::new, MobCategory.MONSTER)
                    .sized(0.1f, 0.1f).build("pheromone_prey/hunt"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
