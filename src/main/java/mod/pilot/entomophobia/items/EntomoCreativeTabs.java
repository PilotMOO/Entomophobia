package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EntomoCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Entomophobia.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static final RegistryObject<CreativeModeTab> ENTOMOPHOBIA_TAB = CREATIVE_MODE_TABS.register("entomophobia_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3).icon(() -> new ItemStack(Items.ROTTEN_FLESH))
                    .title(Component.translatable("creativetab.entomophobia_tab"))
                    .displayItems((something, register) ->{
                        register.accept(EntomoItems.MYIATIC_ZOMBIE_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_CREEPER_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_SPIDER_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_COW_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_SHEEP_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_PIG_SPAWNEGG.get());
                        register.accept(EntomoItems.MYIATIC_CHICKEN_SPAWNEGG.get());

                        register.accept(EntomoItems.POISONOUS_MILK.get());

                        register.accept(EntomoBlocks.MYIATIC_FLESH.get());
                        register.accept(EntomoBlocks.ROOTED_MYIATIC_FLESH.get());
                        register.accept(EntomoBlocks.TWINED_FLESH.get());
                        register.accept(EntomoBlocks.LUMINOUS_FLESH.get());
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> ENTOMOPHOBIA_DEV_TAB = CREATIVE_MODE_TABS.register("entomophobia_dev_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3).icon(() -> new ItemStack(Items.ARROW))
                    .title(Component.translatable("creativetab.entomophobia_dev_tab"))
                    .displayItems((something, register) ->{
                        register.accept(EntomoItems.SHAPE_WAND.get());
                        register.accept(EntomoItems.NEST_WAND.get());
                        register.accept(EntomoItems.SWARM_WAND.get());
                        register.accept(EntomoItems.FLY_WAND.get());
                        register.accept(EntomoItems.BLOCK_PACKET_WAND.get());
                    })
                    .build());
}
