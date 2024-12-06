package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class InfestedMyiaticFleshBlock extends MyiaticFleshBlock{
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;
    public InfestedMyiaticFleshBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ALIVE, true));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ALIVE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState bState) {
        return !bState.getValue(ALIVE);
    }
    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        if (server.random.nextDouble() < 0.01){
            server.setBlock(bPos, bState.setValue(ALIVE, true), 2);
        }
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        if (level instanceof ServerLevel){
            for (int i = 0; i < level.getRandom().nextInt(4); i++){
                SpawnPestFromBlock(bPos, (Level)level, null);
            }
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos bPos, @NotNull BlockState bState, @NotNull Entity entity) {
        if (!bState.getValue(ALIVE)) return;
        if (level.random.nextDouble() < 0.75) return;
        if (entity instanceof LivingEntity LE && !(LE instanceof MyiaticBase)){
            if (MyiaticBase.isInsideOfTargetBlacklist(LE)) return;

            SpawnPestFromBlock(bPos.above(), level, LE instanceof Creeper ? null : LE);
            level.setBlock(bPos, bState.setValue(ALIVE, false), 2);
        }
    }
}
