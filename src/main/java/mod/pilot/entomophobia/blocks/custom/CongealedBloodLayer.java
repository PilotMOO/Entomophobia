package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CongealedBloodLayer extends SnowLayerBlock {
    private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)0.9F, 1.0D);
    public CongealedBloodLayer(Properties pProperties) {
        super(pProperties);
    }



    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState bState, @NotNull BlockGetter level, @NotNull BlockPos bPos) {
        return Shapes.empty();
    }

    public void entityInside(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, @NotNull Entity entity) {
        if (entity instanceof FallingBlockEntity fb && fb.getBlockState().getBlock() == this){
            fb.setOnGround(false);
            int blockLayers = bState.getValue(LAYERS);
            int fallingLayers = fb.getBlockState().getValue(LAYERS);
            if (blockLayers + fallingLayers <= MAX_HEIGHT){
                level.setBlock(bPos, bState.setValue(LAYERS, blockLayers + fallingLayers), 3);
            }
            else{
                int secondLayerCount = (blockLayers + fallingLayers) - 8;
                level.setBlock(bPos, bState.setValue(LAYERS, MAX_HEIGHT), 3);
                level.setBlock(bPos.above(), this.defaultBlockState().setValue(LAYERS, secondLayerCount), 3);
            }
            fb.discard();
        }
        if (entity instanceof MyiaticBase) return;

        if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
            double slow = 0.2d * Math.min(bState.getValue(LAYERS), 4);
            entity.makeStuckInBlock(bState, new Vec3(1 - slow, 1 - slow, 1 - slow));

            if (level instanceof ServerLevel s){
                if (entity.xOld == entity.getX() && entity.zOld == entity.getZ()) return;

                if (entity.tickCount % 20 == 0){
                    s.playSound(null, bPos, SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.BLOCKS,
                            (float) level.random.nextInt(5, 16) / 10,
                            (float) level.random.nextInt(5, 16) / 10);
                }
            }
            entity.setSprinting(false);

            /*if (level.isClientSide) {
                RandomSource randomsource = level.getRandom();
                boolean flag = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (flag && randomsource.nextBoolean()) {
                    level.addParticle(ParticleTypes.SNOWFLAKE, entity.getX(), (double)(bPos.getY() + 1), entity.getZ(), (double)(Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F), (double)0.05F, (double)(Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F));
                }
            }*/
        }
    }
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState bState, @NotNull BlockGetter level,
                                                 @NotNull BlockPos bPos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entitycollisioncontext) {
            Entity entity = entitycollisioncontext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > 2.5F) {
                    return FALLING_COLLISION_SHAPE;
                }

                boolean flag = entity instanceof FallingBlockEntity;
                if (flag && !context.isDescending()) {
                    return super.getCollisionShape(bState, level, bPos, context);
                }
            }
        }

        return Shapes.empty();
    }

    @Override
    public void onPlace(@NotNull BlockState bState, Level level, BlockPos bPos, @NotNull BlockState oldState, boolean pMovedByPiston) {
        if (level.getBlockState(bPos.below()).isAir()) FallingBlockEntity.fall(level, bPos, bState);
    }

    private static final int layerSpreadThreshold = 3;
    @Override
    public boolean isRandomlyTicking(BlockState bState) {
        return bState.getValue(LAYERS) > layerSpreadThreshold;
    }
    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        for (int i = 0; i < random.nextInt(1, Math.max(2, bState.getValue(LAYERS) - layerSpreadThreshold)); i++){
            Direction towards = Direction.getRandom(random);
            if (towards.getStepY() != 0) continue;

            BlockPos poolPos = bPos.relative(towards);
            BlockState poolState = server.getBlockState(poolPos);
            int tickCounter = 0;
            if (poolState.is(this) && poolState.getValue(LAYERS) - 2 < bState.getValue(LAYERS)){
                if (poolState.getValue(LAYERS) == 8) continue;
                server.setBlock(poolPos, poolState.setValue(LAYERS, poolState.getValue(LAYERS) + 1), 3);
                if (bState.getValue(LAYERS) == MAX_HEIGHT){
                    BlockPos above = bPos.above();
                    BlockState aboveState = server.getBlockState(above);
                    while (aboveState.is(this) && aboveState.getValue(LAYERS) == MAX_HEIGHT && tickCounter < 10){
                        BlockPos above1 = above.above();
                        BlockState aboveState1 = server.getBlockState(above1);
                        if (aboveState1.is(this)){
                            above = above1;
                            aboveState = aboveState1;
                        }
                        tickCounter++;
                    }
                    if (aboveState.is(this)){
                        int aboveLayers = aboveState.getValue(LAYERS);
                        if (aboveLayers == 1){
                            server.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                        }
                        else{
                            server.setBlock(above, aboveState.setValue(LAYERS, aboveState.getValue(LAYERS) - 1), 3);
                        }
                        continue;
                    }
                }
                server.setBlock(bPos, bState.setValue(LAYERS, bState.getValue(LAYERS) - 1), 3);
                continue;
            }
            BlockState beneath = server.getBlockState(poolPos.below());
            if (poolState.canBeReplaced() && beneath.isFaceSturdy(server, poolPos.below(), Direction.UP)
                    || beneath.isAir() || (beneath.is(this) && beneath.getValue(LAYERS) == MAX_HEIGHT)){
                server.setBlock(poolPos, EntomoBlocks.CONGEALED_BLOOD.get().defaultBlockState().setValue(LAYERS, 1), 3);
                if (bState.getValue(LAYERS) == MAX_HEIGHT){
                    BlockPos above = bPos.above();
                    BlockState aboveState = server.getBlockState(above);

                    while (aboveState.is(this) && aboveState.getValue(LAYERS) == MAX_HEIGHT && tickCounter < 10){
                        BlockPos above1 = above.above();
                        BlockState aboveState1 = server.getBlockState(above1);
                        if (aboveState1.is(this)){
                            above = above1;
                            aboveState = aboveState1;
                        }
                        tickCounter++;
                    }
                    if (aboveState.is(this)){
                        int aboveLayers = aboveState.getValue(LAYERS);
                        if (aboveLayers == 1){
                            server.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                        }
                        else{
                            server.setBlock(above, aboveState.setValue(LAYERS, aboveState.getValue(LAYERS) - 1), 3);
                        }
                        continue;
                    }
                }
                server.setBlock(bPos, bState.setValue(LAYERS, bState.getValue(LAYERS) - 1), 3);
            }
        }
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState bState, @NotNull ServerLevel server,
                                @NotNull BlockPos bPos, @NotNull ItemStack itemStack, boolean pDropExperience) {

    }

    @Override
    public void onRemove(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos,
                         @NotNull BlockState newState, boolean pMovedByPiston) {
        if (pMovedByPiston || !newState.isAir()) return;
        int newLayer = bState.getValue(LAYERS) - 1;
        if (newLayer > 0){
            level.setBlock(bPos, this.defaultBlockState().setValue(LAYERS, newLayer), 3);
        }
    }

    public @NotNull VoxelShape getVisualShape(@NotNull BlockState bState, @NotNull BlockGetter level,
                                              @NotNull BlockPos bPos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }
}
