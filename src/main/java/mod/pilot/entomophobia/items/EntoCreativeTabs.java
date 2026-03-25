package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.blocks.EntoBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EntoCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Entomophobia.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static final RegistryObject<CreativeModeTab> ENTOMOPHOBIA_TAB = CREATIVE_MODE_TABS.register("entomophobia_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3).icon(() -> new ItemStack(Items.ROTTEN_FLESH))
                    .title(Component.translatable("creativetab.entomophobia_tab"))
                    .displayItems((something, register) ->{
                        register.accept(EntoItems.MYIATIC_ZOMBIE_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_CREEPER_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_SPIDER_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_COW_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_SHEEP_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_PIG_SPAWNEGG.get());
                        register.accept(EntoItems.MYIATIC_CHICKEN_SPAWNEGG.get());
                        register.accept(EntoItems.SPIDER_PEST_SPAWN.get());
                        register.accept(EntoItems.GRUB_SPAWN.get());
                        register.accept(EntoItems.COCK_SPAWN.get());
                        register.accept(EntoItems.CENTIPEDE_SPAWN.get());

                        register.accept(EntoBlocks.MYIATIC_FLESH.get());
                        register.accept(EntoBlocks.ROOTED_MYIATIC_FLESH.get());
                        register.accept(EntoBlocks.INFESTED_MYIATIC_FLESH.get());
                        register.accept(EntoBlocks.WAXY_MYIATIC_FLESH.get());
                        register.accept(EntoBlocks.BLOODWAX_COMB.get());
                        register.accept(EntoBlocks.BLOODWAX_PROTRUSIONS.get());
                        register.accept(EntoBlocks.TWINED_FLESH.get());
                        register.accept(EntoBlocks.LUMINOUS_FLESH.get());
                        register.accept(EntoBlocks.CONGEALED_BLOOD.get());

                        register.accept(EntoItems.POISONOUS_MILK.get());

                        register.accept(EntoItems.LUSTROUS_TISSUE.get());
                        register.accept(EntoItems.BOTTLED_CORPSEDEW.get());
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> ENTOMOPHOBIA_DEV_TAB = CREATIVE_MODE_TABS.register("entomophobia_dev_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3).icon(() -> new ItemStack(Items.ARROW))
                    .title(Component.translatable("creativetab.entomophobia_dev_tab"))
                    .displayItems((something, register) ->{
                        register.accept(EntoItems.SHAPE_WAND.get());
                        register.accept(EntoItems.NEST_WAND.get());
                        register.accept(EntoItems.SWARM_WAND.get());
                        /*register.accept(EntomoItems.FLY_WAND.get());
                        register.accept(EntomoItems.BLOCK_PACKET_WAND.get());
                        register.accept(EntomoItems.FEATURE_WAND.get());
                        register.accept(EntomoItems.BLOOD_DRIP_WAND.get());*/
                    })
                    .build());
}
