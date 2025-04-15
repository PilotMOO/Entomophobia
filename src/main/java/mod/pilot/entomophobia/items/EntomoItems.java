package mod.pilot.entomophobia.items;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.items.custom.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntomoItems {
    /*Food Properties*/
    public static final FoodProperties LUSTROUS_TISSUE_FOOD = new FoodProperties.Builder().alwaysEat().fast()
            .nutrition(1).saturationMod(0f)
            .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 200), 0.75f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 60), 1f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 1), 1f)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 120, 1), 0.9f)
            .build();
    public static final FoodProperties CORPSEDEW_FOOD = new FoodProperties.Builder().alwaysEat()
            .nutrition(6).saturationMod(1.5f)
            .effect(() -> new MobEffectInstance(EntomoMobEffects.OVERSTIMULATION.get(), 100), 1f)
            .build();
    public static final FoodProperties LEAD_FOOD = new FoodProperties.Builder().alwaysEat()
            .nutrition(0).saturationMod(0f).build();

    /*Item registration*/
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

    public static final RegistryObject<Item> LUSTROUS_TISSUE = ITEMS.register("lustrous_tissue",
            () -> new Item(new Item.Properties().food(LUSTROUS_TISSUE_FOOD)){
                @Override
                public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
                    tooltipComponents.add(Component.translatable("item.entomophobia.tooltip.lustrous_tissue"));
                    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
                }
            });
    public static final RegistryObject<Item> BOTTLED_CORPSEDEW = ITEMS.register("bottled_corpsedew",
            () -> new BottledCorpsedewItem(new Item.Properties().food(CORPSEDEW_FOOD).stacksTo(8)));

    public static final RegistryObject<Item> THANKS = ITEMS.register("thanks",
            () -> new Thanks(new Item.Properties().food(LEAD_FOOD).stacksTo(1)));

    public static final RegistryObject<Item> SHAPE_WAND = ITEMS.register("shape_wand",
            () -> new ShapeWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEST_WAND = ITEMS.register("nest_wand",
            () -> new NestWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SWARM_WAND = ITEMS.register("swarm_wand",
            () -> new SwarmWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLY_WAND = ITEMS.register("fly_wand",
            () -> new FlyWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOCK_PACKET_WAND = ITEMS.register("block_packet_wand",
            () -> new BlockPacketTestWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FEATURE_WAND = ITEMS.register("feature_wand",
            () -> new FeatureWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_DRIP_WAND = ITEMS.register("blood_drip_wand",
            () -> new BloodDripWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PEST_WAND = ITEMS.register("pest_wand",
            () -> new RandomPestWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEST_NAV_WAND = ITEMS.register("nest_nav_wand",
            () -> new NestNavWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PATHFINDING_WAND = ITEMS.register("pathfinding_wand",
            () -> new PathfindingWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TEXT_WAND = ITEMS.register("text_wand",
            () -> new TextWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SKYCRAB_WAND = ITEMS.register("skycrab_wand",
            () -> new SkyCrabWand(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
