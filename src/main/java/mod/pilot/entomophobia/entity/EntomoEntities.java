package mod.pilot.entomophobia.entity;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCreeperEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticSpiderEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import mod.pilot.entomophobia.entity.pheromones.PheromoneFrenzyEntity;
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

    //Myiatic Entities
    public static final RegistryObject<EntityType<MyiaticZombieEntity>> MYIATIC_ZOMBIE =
            ENTITY_TYPES.register("myiatic_zombie", () -> EntityType.Builder.of(MyiaticZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 2f).build("myiatic_zombie"));
    public static final RegistryObject<EntityType<MyiaticCreeperEntity>> MYIATIC_CREEPER =
            ENTITY_TYPES.register("myiatic_creeper", () -> EntityType.Builder.of(MyiaticCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 1.5f).build("myiatic_creeper"));
    public static final RegistryObject<EntityType<MyiaticSpiderEntity>> MYIATIC_SPIDER =
            ENTITY_TYPES.register("myiatic_spider", () -> EntityType.Builder.of(MyiaticSpiderEntity::new, MobCategory.MONSTER)
                    .sized(2f, 0.5f).build("myiatic_spider"));
    public static final RegistryObject<EntityType<MyiaticCowEntity>> MYIATIC_COW =
            ENTITY_TYPES.register("myiatic_cow", () -> EntityType.Builder.of(MyiaticCowEntity::new, MobCategory.MONSTER)
                    .sized(1.25f, 1.5f).build("myiatic_cow"));

    //Pheromone Entities
    public static final RegistryObject<EntityType<PheromonePreyHuntEntity>> PREYHUNT =
            ENTITY_TYPES.register("pheromone_prey/hunt", () -> EntityType.Builder.of(PheromonePreyHuntEntity::new, MobCategory.MONSTER)
                    .sized(0.1f, 0.1f).build("pheromone_prey/hunt"));
    public static final RegistryObject<EntityType<PheromoneFrenzyEntity>> FRENZY =
            ENTITY_TYPES.register("pheromone_null/frenzy", () -> EntityType.Builder.of(PheromoneFrenzyEntity::new, MobCategory.MONSTER)
                    .sized(0.1f, 0.1f).build("pheromone_null/frenzy"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
