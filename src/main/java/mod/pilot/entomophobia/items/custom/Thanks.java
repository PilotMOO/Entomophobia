package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Thanks extends Item {

    public Thanks(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 60;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity target) {
        target.eat(level, stack);
        target.hurt(EntomoDamageTypes.leadPoisoning(target), Float.MAX_VALUE);
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.ANVIL_DESTROY;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
        tooltipComponents.add(Component.translatable("item.entomophobia.tooltip.thanks"));
        super.appendHoverText(pStack, pLevel, tooltipComponents, pIsAdvanced);
    }
}
