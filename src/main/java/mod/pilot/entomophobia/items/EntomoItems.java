package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Entomophobia.MOD_ID);

    public static final RegistryObject<Item> MYIATIC_ZOMBIE_SPAWNEGG = ITEMS.register("myiatic_zombie_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_ZOMBIE, 10112885, 1386338, new Item.Properties()));
    public static final RegistryObject<Item> MYIATIC_CREEPER_SPAWNEGG = ITEMS.register("myiatic_creeper_spawnegg",
            () -> new ForgeSpawnEggItem(EntomoEntities.MYIATIC_CREEPER, 10112885, 1386338, new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
