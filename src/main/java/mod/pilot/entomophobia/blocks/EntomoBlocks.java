package mod.pilot.entomophobia.blocks;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.blocks.custom.*;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.sound.EntomoSounds;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EntomoBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Entomophobia.MOD_ID);

    public static RegistryObject<Block> MYIATIC_FLESH = registryBlock("myiatic_flesh",
            () -> new MyiaticFleshBlock(BlockBehaviour.Properties.copy(Blocks.MUD).requiresCorrectToolForDrops()
                    .destroyTime(0.5f)
            ));
    public static RegistryObject<Block> ROOTED_MYIATIC_FLESH = registryBlock("rooted_myiatic_flesh",
            () -> new RootedMyiaticFleshBlock(BlockBehaviour.Properties.copy(Blocks.MUD).requiresCorrectToolForDrops()
                    .destroyTime(1f)
            ));
    public static RegistryObject<Block> INFESTED_MYIATIC_FLESH = registryBlock("infested_myiatic_flesh",
            () -> new InfestedMyiaticFleshBlock(BlockBehaviour.Properties.copy(Blocks.MUD).requiresCorrectToolForDrops()
                    .destroyTime(0.25f)
            ));
    public static RegistryObject<Block> WAXY_MYIATIC_FLESH = registryBlock("waxy_myiatic_flesh",
            () -> new WaxyMyiaticFleshBlock(BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK).requiresCorrectToolForDrops()
                    .destroyTime(6f)
            ));
    public static RegistryObject<Block> BLOODWAX_COMB = registryBlock("bloodwax_comb",
            () -> new BloodwaxCombBlock(BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK).requiresCorrectToolForDrops()
                    .destroyTime(10f)
            ));
    public static RegistryObject<Block> BLOODWAX_PROTRUSIONS = registryBlock("bloodwax_protrusions",
            () -> new BloodwaxProtrusions(BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK).requiresCorrectToolForDrops()
                    .destroyTime(0.3f).sound(EntomoSounds.BLOODWAX_PROTRUSION_STYPE)
            ));


    public static RegistryObject<Block> TWINED_FLESH = registryBlock("twined_flesh",
            () -> new TwinedFleshBlock(BlockBehaviour.Properties.copy(Blocks.CAVE_VINES).sound(EntomoSounds.TWINED_FLESH_STYPE)
            ));
    public static RegistryObject<Block> LUMINOUS_FLESH = registryBlock("luminous_flesh",
            () -> new LuminousFleshBlock(BlockBehaviour.Properties.copy(Blocks.CAVE_VINES).sound(EntomoSounds.TWINED_FLESH_STYPE)
            ));

    public static RegistryObject<Block> CONGEALED_BLOOD = registryBlock("congealed_blood",
            () -> new CongealedBloodLayer(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).sound(EntomoSounds.CONGEALED_BLOOD_STYPE)
                    .destroyTime(0.25f)
            ));

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
