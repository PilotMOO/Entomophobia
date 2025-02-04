package mod.pilot.entomophobia.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PathfindingWand extends Item {
    public PathfindingWand(Properties pProperties) {
        super(pProperties);
    }

    private Mob target;
    private BlockPos to;

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player,
                                                           @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.sidedSuccess(true);

        if (target instanceof Mob mob){
            if (player.isSecondaryUseActive()){
                this.target = null;
                player.displayClientMessage(Component.literal("Removed Entity as pathfinding target!"), true);
                player.playSound(SoundEvents.ARMOR_EQUIP_CHAIN);
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResult.CONSUME;
            } else {
                this.target = mob;
                player.displayClientMessage(Component.literal("Added Entity as pathfinding target!"), true);
                player.playSound(SoundEvents.CHICKEN_EGG);
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResult.SUCCESS;
            }
        }else{
            player.displayClientMessage(Component.literal("Entity is not a Mob and cannot be managed for pathfinding! :["), true);
            player.playSound(SoundEvents.GENERIC_EAT);
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        Player player = context.getPlayer();
        if (player == null) return super.useOn(context);
        if (player.level().isClientSide()) return InteractionResult.sidedSuccess(true);

        if (player.isSecondaryUseActive()){
            player.displayClientMessage(Component.literal("Removed position " + to + " as pathfinding position!"), true);
            player.playSound(SoundEvents.ARMOR_EQUIP_CHAIN);
            this.to = null;
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResult.CONSUME;
        } else {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            this.to = pos;
            player.displayClientMessage(Component.literal("Set position " + pos + " as pathfinding position!"), true);
            player.playSound(SoundEvents.CHICKEN_EGG);
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player.isSecondaryUseActive()){
            if (this.target != null) target.getNavigation().stop();
            this.target = null;
            this.to = null;

            player.displayClientMessage(Component.literal("Cleared all values and halted pathfinding!"), true);
            player.playSound(SoundEvents.PIG_DEATH);
            player.getCooldowns().addCooldown(this, 10);
        }
        else if (this.to != null && this.target != null){
            player.displayClientMessage(Component.literal("Set entity pathfinding to head to " + to + "!"), true);
            player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME);
            target.getNavigation().moveTo(to.getX(), to.getY(), to.getZ(), 1d);
            player.getCooldowns().addCooldown(this, 10);
        }
        return super.use(level, player, hand);
    }
}
