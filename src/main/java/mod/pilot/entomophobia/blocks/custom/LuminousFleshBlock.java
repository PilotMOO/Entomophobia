package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.particles.EntomoParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuminousFleshBlock extends CaveVinesBlock {
    public static final BooleanProperty MIRRORED = EntomoBlockStateProperties.MIRRORED;
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;
    public static final BooleanProperty BLOODY = EntomoBlockStateProperties.BLOODY;
    public LuminousFleshBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.LIT, true)
                .setValue(BERRIES, false)
                .setValue(BlockStateProperties.LEVEL, 5)
                .setValue(BlockStateProperties.INVERTED, false)
                .setValue(MIRRORED, false)
                .setValue(ALIVE, true)
                .setValue(BLOODY, true)
                .setValue(AGE, 0));
    }
    @Override
    protected @NotNull Block getBodyBlock() {
        return EntomoBlocks.TWINED_FLESH.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.LIT);
        builder.add(BlockStateProperties.LEVEL);
        builder.add(BlockStateProperties.INVERTED);
        builder.add(MIRRORED);
        builder.add(ALIVE);
        builder.add(BLOODY);
    }

    @Override
    protected @NotNull BlockState updateBodyAfterConvertedFromHead(@NotNull BlockState bodyState, @NotNull BlockState headState) {
        return bodyState.setValue(ALIVE, false);
    }

    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        return new ItemStack(EntomoBlocks.LUMINOUS_FLESH.get());
    }
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult bHitResult) {
        return InteractionResult.PASS;
    }
    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos bPos, @NotNull BlockState bState, boolean client) {
        return false;
    }

    /*@Override
    public void performBonemeal(@NotNull ServerLevel server, @NotNull RandomSource random, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        BlockPos growSpot = getEmptyGrowthPos(bPos, server);
        if (growSpot == null) return;

        if (server.getBlockState(growSpot).isAir() && bState.is(getHeadBlock())){
            server.setBlock(growSpot, getHeadBlock().defaultBlockState(), 3);
            server.setBlock(bPos.above(), getBodyBlock().defaultBlockState(), 3);
        }
    }*/

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState bState) {
        return bState.getValue(ALIVE) || bState.getValue(BLOODY);
    }

    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        BlockPos below = bPos.below();
        BlockState belowState = server.getBlockState(below);

        if (bState.getValue(BLOODY) && random.nextDouble() < 0.1){
            BlockPos aboveGroundOrBlood = getAboveGroundOrCongealedBlood(bPos, server);
            BlockState groundState = server.getBlockState(aboveGroundOrBlood);
            if (groundState.is(EntomoBlocks.CONGEALED_BLOOD.get())){
                server.setBlock(aboveGroundOrBlood, groundState.setValue(BlockStateProperties.LAYERS,
                        groundState.getValue(BlockStateProperties.LAYERS) + 1), 3);
            }
            else if (groundState.isAir() && server.getBlockState(aboveGroundOrBlood.below())
                    .isFaceSturdy(server, aboveGroundOrBlood.below(), Direction.UP)){
                server.setBlock(aboveGroundOrBlood, EntomoBlocks.CONGEALED_BLOOD.get().defaultBlockState(), 3);
            }

            BlockState checkBeneath = server.getBlockState(aboveGroundOrBlood.below());
            if ((checkBeneath.is(EntomoBlocks.CONGEALED_BLOOD.get()) && checkBeneath.getValue(BlockStateProperties.LAYERS) == 8)
                    || random.nextDouble() < 0.025){
                server.setBlock(bPos, bState.setValue(BLOODY, false), 2);
            }
        }

        if (!bState.getValue(ALIVE)) return;

        if (bState.getValue(AGE) == 0){
            int lengthToGround = getLengthToGround(bPos, server);
            int preRandomMaxGrowth = Math.max(Math.min(lengthToGround, 8), 2);
            int origin;
            if (preRandomMaxGrowth < 3) origin = 1;
            else{
                origin = preRandomMaxGrowth;
                do{origin--;}
                while (origin > 6);
            }
            bState = bState.setValue(AGE, random.nextInt(origin, preRandomMaxGrowth));
            server.setBlock(bPos, bState, 2);
        }

        int totalLength = getTotalLength(bPos, server);
        int age = bState.getValue(AGE);

        if (getLengthToGround(bPos, server) < random.nextInt(3, 8)){
            server.setBlock(bPos, bState.setValue(ALIVE, false), 2);
            return;
        }

        if (totalLength < age && belowState.canBeReplaced()){
            server.setBlock(below, bState, 3);
            server.setBlock(bPos, updateBodyAfterConvertedFromHead(getBodyBlock().defaultBlockState(), bState), 3);
        }else{
            server.setBlock(bPos, bState.setValue(ALIVE, false), 3);
        }
    }

    private static BlockPos getAboveGroundOrCongealedBlood(BlockPos bPos, Level level) {
        bPos = bPos.below();
        BlockState bState = level.getBlockState(bPos);
        while (bState.isAir() && bPos.getY() > -64){
            bPos = bPos.below();
            bState = level.getBlockState(bPos);
            if (bState.is(EntomoBlocks.CONGEALED_BLOOD.get())){
                return bState.getValue(BlockStateProperties.LAYERS) == 8 ? getBloodTop(bPos, level) : bPos;
            }
        }
        return bPos.above();
    }

    private static BlockPos getBloodTop(BlockPos bPos, Level level) {
        BlockState bState = level.getBlockState(bPos);
        while (bState.is(EntomoBlocks.CONGEALED_BLOOD.get()) && bState.getValue(BlockStateProperties.LAYERS) == 8){
            bPos = bPos.above();
            bState = level.getBlockState(bPos);
        }
        return bPos;
    }

    @Override
    public void animateTick(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        if (random.nextDouble() <= 0.075){
            for (int i = 0; i < random.nextInt(2, 5); i++){
                double x = (bPos.getX() + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25) + 0.5;
                double y = (bPos.getY() + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25);
                double z = (bPos.getZ() + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25) + 0.5;

                level.addParticle(EntomoParticles.FLY_PARTICLE.get(), x, y, z, 0, 0, 0);
            }
        }
        if (random.nextDouble() <= 0.1 && bState.getValue(BLOODY)){
            for (int i = 0; i < random.nextInt(2, 5); i++){
                double x = (bPos.getX() + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25) + 0.5;
                double y = bPos.getY();
                double z = (bPos.getZ() + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25) + 0.5;

                level.addParticle(EntomoParticles.BLOOD_HANG_PARTICLE.get(), x, y, z, 0, 0, 0);
            }
        }


        if (!bState.getValue(BlockStateProperties.LIT)) return;
        if (random.nextBoolean()) return;

        int lightStage = bState.getValue(BlockStateProperties.LEVEL);
        boolean inverted = bState.getValue(BlockStateProperties.INVERTED);
        if (inverted){
            lightStage -= (random.nextBoolean() ? 1 : random.nextInt(1,3));
            if (lightStage < 5) lightStage = 5;
            level.setBlock(bPos, bState.setValue(BlockStateProperties.LEVEL, lightStage), 2);
            bState = level.getBlockState(bPos);
            if (lightStage == 5 || random.nextDouble() < 0.25) {
                level.setBlock(bPos, bState.setValue(BlockStateProperties.INVERTED, false), 2);
                //bState = level.getBlockState(bPos);
            }
        }
        else{
            lightStage += (random.nextBoolean() ? 1 : random.nextInt(1,3));
            if (lightStage > 12) lightStage = 12;
            level.setBlock(bPos, bState.setValue(BlockStateProperties.LEVEL, lightStage), 2);
            bState = level.getBlockState(bPos);
            if (lightStage == 12 || level.getRandom().nextDouble() < 0.25) {
                level.setBlock(bPos, bState.setValue(BlockStateProperties.INVERTED, true), 2);
                //bState = level.getBlockState(bPos);
            }
        }
    }

    @Override
    public int getLightEmission(BlockState bState, BlockGetter level, BlockPos bPos) {
        return bState.getValue(BlockStateProperties.LIT) ? bState.getValue(BlockStateProperties.LEVEL) : 0;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos bPos, @NotNull BlockState bState,
                            @Nullable LivingEntity placer, @NotNull ItemStack itemStack) {
        level.setBlock(bPos, bState.setValue(MIRRORED, level.getRandom().nextBoolean()), 2);
    }

    public static int getTotalLength(BlockPos bPos, Level level){
        int totalLength = 1;
        BlockPos currentPos = bPos.above();
        while (level.getBlockState(currentPos).is(EntomoBlocks.TWINED_FLESH.get())){
            totalLength++;
            currentPos = currentPos.above();
        }
        return totalLength;
    }

    public static int getLengthToGround(BlockPos bPos, Level level){
        bPos = bPos.below();
        int toReturn = 0;
        while (level.getBlockState(bPos).canBeReplaced() && bPos.getY() > -64){
            toReturn++;
            bPos = bPos.below();
        }
        return toReturn;
    }
}
