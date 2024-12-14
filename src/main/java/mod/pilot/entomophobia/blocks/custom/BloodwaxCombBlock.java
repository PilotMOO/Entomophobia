package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
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
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;
    public BloodwaxCombBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(CORPSEDEW, false)
                .setValue(ALIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(CORPSEDEW);
        builder.add(ALIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState bState) {
        return bState.getValue(ALIVE) || !bState.getValue(CORPSEDEW);
    }

    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        if (random.nextDouble() <= 0.05 && !bState.getValue(CORPSEDEW)){
            server.setBlock(bPos, bState.setValue(CORPSEDEW, true), 3);
            server.playSound(null, bPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 0.5f);
        }

        if (bState.getValue(ALIVE) && random.nextBoolean()){
            Direction facing = Direction.getRandom(random);
            if (facing == bState.getValue(FACING) || facing == bState.getValue(FACING).getOpposite()) return;

            BlockPos adjacentPos = bPos.relative(facing);
            BlockState adjacentState = server.getBlockState(adjacentPos);
            if (adjacentState.isAir()){
                server.setBlock(adjacentPos,
                        EntomoBlocks.BLOODWAX_PROTRUSIONS.get().defaultBlockState()
                                .setValue(BloodwaxProtrusions.FACING,facing.getOpposite()),
                        3);
                server.setBlock(bPos, bState.setValue(ALIVE, random.nextBoolean()), 2);
                server.playSound(null, bPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 0.5f, 0.75f);
            }
        }
    }

    @Override
    public @NotNull InteractionResult use(BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (bState.getValue(CORPSEDEW) && itemstack.is(Items.GLASS_BOTTLE)) {
            itemstack.shrink(1);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.MUD_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (itemstack.isEmpty()) {
                player.setItemInHand(hand, new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()));
            } else if (!player.getInventory().add(new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()))) {
                player.drop(new ItemStack(EntomoItems.BOTTLED_CORPSEDEW.get()), false);
            }

            level.setBlock(bPos, bState.setValue(CORPSEDEW, false), 3);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, bPos);

            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(item));
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
