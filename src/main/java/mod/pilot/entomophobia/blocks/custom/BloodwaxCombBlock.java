package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.items.EntomoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BloodwaxCombBlock extends DirectionalBlock {
    public static final BooleanProperty CORPSEDEW = BooleanProperty.create("corpsedew");
    public BloodwaxCombBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(CORPSEDEW, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(CORPSEDEW);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState bState) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        if (random.nextDouble() > 0.05) return;

        server.setBlock(bPos, bState.setValue(CORPSEDEW, true), 3);
    }

    @Override
    public @NotNull InteractionResult use(BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = false;
        if (bState.getValue(CORPSEDEW)) {
            Item item = itemstack.getItem();
            if (itemstack.is(Items.GLASS_BOTTLE)) {
                itemstack.shrink(1);
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.MUD_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (itemstack.isEmpty()) {
                    player.setItemInHand(hand, new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()));
                } else if (!player.getInventory().add(new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()))) {
                    player.drop(new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()), false);
                }

                flag = true;
                level.setBlock(bPos, bState.setValue(CORPSEDEW, false), 2);
                level.gameEvent(player, GameEvent.FLUID_PICKUP, bPos);
            }

            if (!level.isClientSide() && flag) {
                player.awardStat(Stats.ITEM_USED.get(item));
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
