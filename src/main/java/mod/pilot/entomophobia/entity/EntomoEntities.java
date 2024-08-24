package mod.pilot.entomophobia.entity;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.*;
import mod.pilot.entomophobia.entity.pheromones.PheromoneFrenzyEntity;
import mod.pilot.entomophobia.entity.pheromones.PheromonePreyHuntEntity;
import mod.pilot.entomophobia.entity.projectile.StringGrappleProjectile;
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
                    .sized(1.5f, 0.5f).build("myiatic_spider"));
    public static final RegistryObject<EntityType<MyiaticCowEntity>> MYIATIC_COW =
            ENTITY_TYPES.register("myiatic_cow", () -> EntityType.Builder.of(MyiaticCowEntity::new, MobCategory.MONSTER)
                    .sized(1.25f, 1.5f).build("myiatic_cow"));
    public static final RegistryObject<EntityType<MyiaticSheepEntity>> MYIATIC_SHEEP =
            ENTITY_TYPES.register("myiatic_sheep", () -> EntityType.Builder.of(MyiaticSheepEntity::new, MobCategory.MONSTER)
                    .sized(1f, 1.25f).build("myiatic_sheep"));
    public static final RegistryObject<EntityType<MyiaticPigEntity>> MYIATIC_PIG =
            ENTITY_TYPES.register("myiatic_pig", () -> EntityType.Builder.of(MyiaticPigEntity::new, MobCategory.MONSTER)
                    .sized(1.25f, 0.75f).build("myiatic_pig"));
    public static final RegistryObject<EntityType<MyiaticChickenEntity>> MYIATIC_CHICKEN =
            ENTITY_TYPES.register("myiatic_chicken", () -> EntityType.Builder.of(MyiaticChickenEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 0.5f).build("myiatic_chicken"));

    //Pheromone Entities
    public static final RegistryObject<EntityType<PheromonePreyHuntEntity>> PREYHUNT =
            ENTITY_TYPES.register("pheromone_prey/hunt", () -> EntityType.Builder.of(PheromonePreyHuntEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f).build("pheromone_prey/hunt"));
    public static final RegistryObject<EntityType<PheromoneFrenzyEntity>> FRENZY =
            ENTITY_TYPES.register("pheromone_null/frenzy", () -> EntityType.Builder.of(PheromoneFrenzyEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f).build("pheromone_null/frenzy"));

    //Projectiles
    public static final RegistryObject<EntityType<StringGrappleProjectile>> STRING_GRAPPLE =
            ENTITY_TYPES.register("string_grapple", () -> EntityType.Builder.of(StringGrappleProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("string_grapple"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
