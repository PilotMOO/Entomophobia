package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TwinedFleshBlock extends CaveVinesPlantBlock {
    private static final RandomSource random = RandomSource.create();
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;

    public TwinedFleshBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ALIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ALIVE);
    }

    @Override
    protected @NotNull GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock)EntomoBlocks.LUMINOUS_FLESH.get();
    }

    @Override
    protected @NotNull BlockState updateHeadAfterConvertedFromBody(@NotNull BlockState bodyState, @NotNull BlockState headState) {
        return headState.setValue(BlockStateProperties.LIT, true)
                .setValue(LuminousFleshBlock.MIRRORED, random.nextBoolean())
                .setValue(LuminousFleshBlock.ALIVE, bodyState.getValue(ALIVE))
                .setValue(LuminousFleshBlock.AGE, 0);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        return new ItemStack(EntomoBlocks.TWINED_FLESH.get());
    }
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult bHitResult) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos bPos, @NotNull BlockState bState, boolean client) {
        return false;
    }

    @Override
    public boolean isRandomlyTicking(BlockState bState) {
        return bState.getValue(ALIVE);
    }

    @Override
    public void tick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        BlockPos BelowPos = bPos.below();
        BlockState BelowState = server.getBlockState(BelowPos);
        if (BelowState.isAir()){
            BlockState newHead = updateHeadAfterConvertedFromBody(bState, getHeadBlock().defaultBlockState());
            server.setBlock(bPos, newHead, 3);
        }
        else{
            server.setBlock(bPos, bState.setValue(ALIVE, false), 2);
        }

        super.tick(bState, server, bPos, random);
    }


    /*@Override
    public void performBonemeal(@NotNull ServerLevel server, @NotNull RandomSource random, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        if (server.getBlockState(bPos.below()).isAir() && bState.is(getBodyBlock())){
            server.setBlock(bPos, getHeadBlock().defaultBlockState(), 3);
        }
        else{
            BlockPos growSpot = getEmptyGrowthPos(bPos, server);
            if (growSpot == null) return;
            server.setBlock(growSpot, getHeadBlock().defaultBlockState(), 3);
        }
    }*/

    public static int getTotalLength(BlockPos bPos, Level level){
        int totalLength = 0;
        BlockPos currentPos = bPos;
        while (level.getBlockState(currentPos).is(EntomoBlocks.TWINED_FLESH.get())){
            currentPos = currentPos.below();
        }
        if (level.getBlockState(currentPos).is(EntomoBlocks.LUMINOUS_FLESH.get())) totalLength++;
        currentPos = currentPos.above();
        while (level.getBlockState(currentPos).is(EntomoBlocks.TWINED_FLESH.get())){
            totalLength++;
            currentPos = currentPos.above();
        }

        return totalLength;
    }
    public static @Nullable BlockPos getEmptyGrowthPos(BlockPos startPos, Level level){
        if (getTotalLength(startPos, level) >= 5) return null;
        else{
            BlockPos currentPos = startPos.below();
            BlockState currentState = level.getBlockState(currentPos);
            while (currentState.is(EntomoBlocks.TWINED_FLESH.get())
                    || currentState.is(EntomoBlocks.LUMINOUS_FLESH.get())){
                currentPos = startPos.below();
                currentState = level.getBlockState(currentPos);
            }
            if (currentState.isAir()) return currentPos;
        }
        return null;
    }
}
