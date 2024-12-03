package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class RootedMyiaticFleshBlock extends MyiaticFleshBlock{
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;
    public RootedMyiaticFleshBlock(Properties pProperties) {
        super(pProperties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(ALIVE, true));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ALIVE);
    }
    @Override
    public boolean isRandomlyTicking(BlockState bState) {
        return bState.getValue(ALIVE);
    }
    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        BlockPos BelowPos = bPos.below();
        BlockState BelowState = server.getBlockState(BelowPos);
        if (BelowState.isAir()){
            server.setBlock(BelowPos, EntomoBlocks.LUMINOUS_FLESH.get().defaultBlockState(), 3);
        }
        server.setBlock(bPos, bState.setValue(ALIVE, false), 2);
    }
    @Override
    public BlockState updateShape(@NotNull BlockState bState, @NotNull Direction direction, @NotNull BlockState neighbor,
                                  @NotNull LevelAccessor level, @NotNull BlockPos bPos, @NotNull BlockPos neighborPos) {
        return direction == Direction.DOWN && level.getBlockState(bPos.below()).isAir() ? bState.setValue(ALIVE, true) : bState;
    }
}
