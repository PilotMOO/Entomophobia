package mod.pilot.entomophobia.blocks;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.items.EntomoItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EntomoBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Entomophobia.MOD_ID);

    private static <T extends net.minecraft.world.level.block.Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        RegisterBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends net.minecraft.world.level.block.Block>RegistryObject<Item> RegisterBlockItem(String name, RegistryObject<T> block) {
        return EntomoItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
