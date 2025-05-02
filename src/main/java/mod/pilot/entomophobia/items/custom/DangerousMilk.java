package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.InputReader;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DangerousMilk extends MilkBucketItem {
    public DangerousMilk(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide){
            GiveEffectsTo(pEntityLiving);
        }
        if (pEntityLiving instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, pStack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (pEntityLiving instanceof Player && !((Player)pEntityLiving).getAbilities().instabuild) {
            pStack.shrink(1);
        }

        return pStack.isEmpty() ? new ItemStack(Items.BUCKET) : pStack;
    }

    private void GiveEffectsTo(LivingEntity target){
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 6000, 5));
        target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1000, 2));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 500));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 500));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag pIsAdvanced) {
        if (InputReader.leftControl()){
            tooltip.add(Component.translatable("item.entomophobia.tooltip.milk_bucket_advanced"));
        } else {
            tooltip.add(Component.translatable("item.entomophobia.tooltip.milk_bucket"));
        }
        super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
    }
}
