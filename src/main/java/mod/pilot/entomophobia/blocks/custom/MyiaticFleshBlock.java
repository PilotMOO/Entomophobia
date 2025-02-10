package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.entity.PestManager;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyiaticFleshBlock extends Block {
    public MyiaticFleshBlock(Properties pProperties) {
        super(pProperties);
    }

    public static Block getDecayed(){
        //ToDo Add dead flesh block
        return null;
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        if (level.getRandom().nextDouble() <= 0.25 && level instanceof ServerLevel){
            SpawnPestFromBlock(bPos, (Level)level, null);
        }
    }

    public static void SpawnPestFromBlock(@NotNull BlockPos bPos, @NotNull Level level, @Nullable LivingEntity target){
        PestManager.createPestAt(level, bPos.getCenter(), 1, 2800, target);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public void fallOn(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockPos pPos, @NotNull Entity pEntity, float pFallDistance) {
        if (!(pEntity instanceof MyiaticBase)) super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
    }
}
