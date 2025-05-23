package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NestWand extends Item {
    public NestWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        if (context.getLevel() instanceof ServerLevel server){
            if (context.getPlayer().isSecondaryUseActive()){
                NestManager.constructNewNest(server, context.getClickLocation());
            }
            else {
                NestManager.constructNewNest(server, NestManager.getNewNestPosition(context.getClickLocation(), 20, true));
            }
        }
        context.getPlayer().displayClientMessage(Component.literal("Makin' a new nest!"), true);
        context.getPlayer().getCooldowns().addCooldown(this, 5);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand pUsedHand) {
        if (player.isSecondaryUseActive()){
            Nest closest = null;
            double distance = Double.MAX_VALUE;
            for (Nest nest : NestManager.getActiveNests()){
                if (closest == null){
                    closest = nest;
                    distance = player.position().distanceTo(nest.origin);
                }
                else{
                    if (player.position().distanceTo(nest.origin) < distance){
                        closest = nest;
                        distance = player.position().distanceTo(nest.origin);
                    }
                }
            }
            if (closest != null){
                player.displayClientMessage(Component.literal("Killing closest nest!"), true);
                player.playSound(SoundEvents.ANVIL_BREAK, 10, 2);
                closest.kill(true);
            }
            else{
                player.displayClientMessage(Component.literal("There isn't a nearby nest to kill :["), true);
                player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 10, 2);
            }
        }
        player.getCooldowns().addCooldown(this, 5);
        return super.use(level, player, pUsedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, @NotNull List<Component> components, @NotNull TooltipFlag isAdvanced) {
        components.add(Component.translatable("item.entomophobia.tooltip.dev_wand"));
        components.add(Component.translatable("item.entomophobia.tooltip.nest_wand"));
        super.appendHoverText(stack, pLevel, components, isAdvanced);
    }
}
