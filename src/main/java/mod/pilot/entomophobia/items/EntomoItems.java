package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntomoItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Entomophobia.MOD_ID);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
