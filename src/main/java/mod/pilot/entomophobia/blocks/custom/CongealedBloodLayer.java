package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.projectile.CongealedBloodProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
                level.playSound(null, bPos, this.soundType.getBreakSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
            }
            else{
                int secondLayerCount = (blockLayers + fallingLayers) - 8;
                level.setBlock(bPos, bState.setValue(LAYERS, MAX_HEIGHT), 3);
                level.setBlock(bPos.above(), this.defaultBlockState().setValue(LAYERS, secondLayerCount), 3);
                level.playSound(null, bPos, this.soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
            }
            fb.discard();
        } else if (entity instanceof CongealedBloodProjectile CBP){
            int blockLayers = bState.getValue(LAYERS);
            if (blockLayers < MAX_HEIGHT){
                level.setBlock(bPos, bState.setValue(LAYERS, blockLayers + 1), 3);
                level.playSound(null, bPos, this.soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
                CBP.discard();
            }
            else{
                Vec3 projDelta = CBP.getDeltaMovement();
                Direction towards = Direction.getNearest(projDelta.x, projDelta.y, projDelta.z).getOpposite();
                BlockPos adjacentBPos = bPos.relative(towards);
                BlockState adjacentBState = level.getBlockState(adjacentBPos);
                if (adjacentBState.canBeReplaced()){
                    level.setBlock(adjacentBPos, this.defaultBlockState(), 3);
                    level.playSound(null, bPos, this.soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
                    CBP.discard();
                }
                else if (adjacentBState.is(this) && adjacentBState.getValue(LAYERS) < MAX_HEIGHT){
                    level.setBlock(adjacentBPos, adjacentBState.setValue(LAYERS, adjacentBState.getValue(LAYERS) + 1), 3);
                    level.playSound(null, bPos, this.soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
                    CBP.discard();
                }
            }
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
    public void onPlace(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos,
                        @NotNull BlockState oldState, boolean pMovedByPiston) {
        if (oldState.canBeReplaced() || oldState.is(this)){
            BlockState belowState = level.getBlockState(bPos.below());
            if (belowState.isAir()
                    || (belowState.is(this) && belowState.getValue(LAYERS) < MAX_HEIGHT)) {
                FallingBlockEntity.fall(level, bPos, bState);
            }
        }
    }

/*    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState bState, @NotNull Direction from, @NotNull BlockState fromState,
                                           LevelAccessor level, BlockPos bPos, @NotNull BlockPos fromPos) {
        BlockState belowState = level.getBlockState(bPos.below());
        if (belowState.canBeReplaced() || belowState.is(this)){
            if (belowState.isAir()
                    || (belowState.is(this) && belowState.getValue(LAYERS) < MAX_HEIGHT)) {
                FallingBlockEntity.fall((Level)level, bPos, bState);
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(bState, from, fromState, level, bPos, fromPos);
    }*/

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
            if ((poolState.canBeReplaced()) && (beneath.isFaceSturdy(server, poolPos.below(), Direction.UP)
                    || beneath.isAir() || (beneath.is(this)))){
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
    public void onRemove(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos,
                         @NotNull BlockState newState, boolean pMovedByPiston) {
        if (pMovedByPiston || !newState.isAir()) return;
        if (level.getBlockState(bPos.above()).is(this)){
            BlockPos top = getTopLayer(bPos, level);
            BlockState topState = level.getBlockState(top);
            if (!topState.is(this)) return;
            int newLayer = topState.getValue(LAYERS) - 1;
            if (bPos.getX() == top.getX() && bPos.getY() == top.getY() && bPos.getZ() == top.getZ()){
                level.setBlock(bPos,
                        newLayer > 0 ? this.defaultBlockState().setValue(LAYERS, newLayer) : Blocks.AIR.defaultBlockState(),
                        3);
            }
            else{
                level.setBlock(bPos, bState, 3);
                level.setBlock(top,
                        newLayer > 0 ? this.defaultBlockState().setValue(LAYERS, newLayer) : Blocks.AIR.defaultBlockState(),
                        3);
            }
        }
        else{
            int newLayer = bState.getValue(LAYERS) - 1;
            level.setBlock(bPos,
                    newLayer > 0 ? this.defaultBlockState().setValue(LAYERS, newLayer) : Blocks.AIR.defaultBlockState(),
                    3);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos bPos, Player player, boolean willHarvest, FluidState fluid) {
        if (level instanceof ServerLevel && !player.isCreative() && !player.isSpectator()){
            if (willHarvest && level.random.nextBoolean()){
                Vec3 pos = getTopLayer(bPos, level).getCenter();
                ItemEntity item = new ItemEntity(level, pos.x, pos.y, pos.z,
                        new ItemStack(EntomoBlocks.CONGEALED_BLOOD.get()), 0, 0.1, 0);
                level.addFreshEntity(item);
            }
        }
        return super.onDestroyedByPlayer(state, level, bPos, player, willHarvest, fluid);
    }

    public @NotNull VoxelShape getVisualShape(@NotNull BlockState bState, @NotNull BlockGetter level,
                                              @NotNull BlockPos bPos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(@NotNull BlockState bState, @NotNull LevelReader level, @NotNull BlockPos bPos) {
        return true;
    }

    private BlockPos getTopLayer(BlockPos bPos, Level level){
        BlockPos.MutableBlockPos mBPos = bPos.mutable();
        while (level.getBlockState(mBPos.offset(0, 1, 0)).is(this)){
            mBPos.move(0, 1, 0);
        }
        return mBPos.immutable();
    }

    public static class CongealedBloodItem extends BlockItem {
        public CongealedBloodItem(Properties pProperties) {
            super(EntomoBlocks.CONGEALED_BLOOD.get(), pProperties);
        }

        public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pHand) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                CongealedBloodProjectile cbp = new CongealedBloodProjectile(EntomoEntities.CONGEALED_BLOOD.get(), pLevel);
                cbp.copyPosition(pPlayer);
                cbp.move(MoverType.SELF, new Vec3(0, pPlayer.getEyeHeight(), 0));
                cbp.setOwner(pPlayer);
                cbp.setItem(itemstack);
                cbp.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                pLevel.addFreshEntity(cbp);
            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("block.entomophobia.tooltip.congealed_blood"));
            super.appendHoverText(stack, pLevel, tooltip, flag);
        }
    }
}
