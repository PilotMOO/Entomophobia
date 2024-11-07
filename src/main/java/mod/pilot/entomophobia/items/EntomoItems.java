package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.items.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Entomophobia.MOD_ID);

    public static final RegistryObject<Item> MYIATIC_ZOMBIE_SPAWNEGG = ITEMS.register("myiatic_zombie_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_ZOMBIE, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_CREEPER_SPAWNEGG = ITEMS.register("myiatic_creeper_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_CREEPER, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_SPIDER_SPAWNEGG = ITEMS.register("myiatic_spider_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_SPIDER, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_COW_SPAWNEGG = ITEMS.register("myiatic_cow_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_COW, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_SHEEP_SPAWNEGG = ITEMS.register("myiatic_sheep_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_SHEEP, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_PIG_SPAWNEGG = ITEMS.register("myiatic_pig_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_PIG, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_CHICKEN_SPAWNEGG = ITEMS.register("myiatic_chicken_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_CHICKEN, -1, -1, new Item.Properties()));


    public static final RegistryObject<Item> POISONOUS_MILK = ITEMS.register("milk_bucket",
            () -> new DangerousMilk(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> SHAPE_WAND = ITEMS.register("shape_wand",
            () -> new ShapeWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEST_WAND = ITEMS.register("nest_wand",
            () -> new NestWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SWARM_WAND = ITEMS.register("swarm_wand",
            () -> new SwarmWand(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
