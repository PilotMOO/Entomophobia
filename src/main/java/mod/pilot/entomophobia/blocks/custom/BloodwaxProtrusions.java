package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BloodwaxProtrusions extends DirectionalBlock {
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    public BloodwaxProtrusions(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState bState, @NotNull BlockGetter level,
                                        @NotNull BlockPos bPos, @NotNull CollisionContext context) {
        return switch (bState.getValue(FACING)) {
            case UP -> UP_AABB;
            case DOWN -> DOWN_AABB;
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
        };
    }
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState bState, @NotNull Direction from, @NotNull BlockState nState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos bPos, @NotNull BlockPos nPos) {
        Direction priority = getPriority(nState, from,
                level.getBlockState(bPos.relative(bState.getValue(FACING))), bState.getValue(FACING),
                bState);

        if (bState.getValue(FACING) == from && level.getBlockState(bPos.relative(bState.getValue(FACING))).isAir()){
            return Blocks.AIR.defaultBlockState();
        }
        if (priority == bState.getValue(FACING)){
            return bState;
        }
        return bState.setValue(FACING, priority);
    }

    private static final VoxelShape OCCLUDE = Block.box(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState bState, @NotNull BlockGetter level, @NotNull BlockPos bPos) {
        return OCCLUDE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        return Arrays.stream(pContext.getNearestLookingDirections()).map((direction)
                -> this.getStateForPlacement(blockstate, blockpos, level, direction))
                .filter(Objects::nonNull).findFirst().orElse(null);
    }

    public @Nullable BlockState getStateForPlacement(BlockState bState, BlockPos bPos, Level level, Direction facing) {
        BlockPos relative = bPos.relative(facing);
        if (level.getBlockState(relative).isFaceSturdy(level, relative, facing.getOpposite())){
            BlockState blockstate;
            if (bState.is(this)) {
                blockstate = bState;
            } else {
                blockstate = this.defaultBlockState();
            }

            return blockstate.setValue(FACING, facing);
        }
        return null;
    }
    public static Direction getPriority(@NotNull BlockState bState1, @NotNull Direction b1Relative,
                                       @NotNull BlockState bState2, @NotNull Direction b2Relative,
                                        @NotNull BlockState parentState){
       Direction facing = parentState.getValue(FACING);

        if (blacklist.contains(defaultFrom(bState1))){
            if (blacklist.contains(defaultFrom(bState2))) return facing;
            else return b2Relative;
        }
        else if (blacklist.contains(defaultFrom(bState2))){
            return b1Relative;
        }

        if (primaryList.contains(defaultFrom(bState1))){
            if (!primaryList.contains(defaultFrom(bState2))) return b1Relative;
            else return b2Relative == facing ? b2Relative : b1Relative;
        }
        else if (secondaryList.contains(defaultFrom(bState1))){
            if (primaryList.contains(defaultFrom(bState2))) return b2Relative;
            else if (!secondaryList.contains(defaultFrom(bState2))) return b1Relative;
            else return b2Relative == facing ? b2Relative : b1Relative;
        }
        else if (ternaryList.contains(defaultFrom(bState1))){
            if (primaryList.contains(defaultFrom(bState2))) return b2Relative;
            else if (secondaryList.contains(defaultFrom(bState2))) return b2Relative;
            else if (!ternaryList.contains(defaultFrom(bState2))) return b1Relative;
            else return b2Relative == facing ? b2Relative : b1Relative;
        }
        else return facing;
    }
    @Override
    public boolean canBeReplaced(@NotNull BlockState bState, @NotNull Fluid fluid) {return true;}
    @Override
    public boolean canBeReplaced(@NotNull BlockState bState, @NotNull BlockPlaceContext context) {return true;}

    private static BlockState defaultFrom(BlockState bState){
        return bState.getBlock().defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState bState, @NotNull LevelReader level, BlockPos bPos) {
        BlockPos relative = bPos.relative(bState.getValue(FACING));
        return !blacklist.contains(defaultFrom(bState)) && level.getBlockState(relative)
                .isFaceSturdy(level, relative, bState.getValue(FACING));
    }

    public static final ArrayList<BlockState> primaryList = new ArrayList<>();
    public static final ArrayList<BlockState> secondaryList = new ArrayList<>();
    public static final ArrayList<BlockState> ternaryList = new ArrayList<>();
    public static final ArrayList<BlockState> blacklist = new ArrayList<>();

    public static void registerAllPriorityBlocks(){
        primaryList.clear();
        secondaryList.clear();
        ternaryList.clear();

        primaryList.add(EntomoBlocks.WAXY_MYIATIC_FLESH.get().defaultBlockState());
        primaryList.add(EntomoBlocks.BLOODWAX_COMB.get().defaultBlockState());

        secondaryList.add(EntomoBlocks.MYIATIC_FLESH.get().defaultBlockState());
        secondaryList.add(EntomoBlocks.ROOTED_MYIATIC_FLESH.get().defaultBlockState());
        secondaryList.add(EntomoBlocks.INFESTED_MYIATIC_FLESH.get().defaultBlockState());
    }
}
