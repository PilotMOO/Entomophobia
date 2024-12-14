package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.blocks.EntomoBlockStateProperties;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class WaxyMyiaticFleshBlock extends MyiaticFleshBlock{
    public static final BooleanProperty ALIVE = EntomoBlockStateProperties.ALIVE;
    public WaxyMyiaticFleshBlock(Properties pProperties) {
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
        return bState.getValue(ALIVE);
    }
    @Override
    public void randomTick(@NotNull BlockState bState, @NotNull ServerLevel server, @NotNull BlockPos bPos, @NotNull RandomSource random) {
        Direction facing = Direction.getRandom(random);
        BlockPos adjacentPos = bPos.relative(facing);
        BlockState adjacentState = server.getBlockState(adjacentPos);
        if (adjacentState.isAir()){
            server.setBlock(adjacentPos,
                    EntomoBlocks.BLOODWAX_PROTRUSIONS.get().defaultBlockState()
                            .setValue(BloodwaxProtrusions.FACING,facing.getOpposite()),
                    3);
            server.setBlock(bPos, bState.setValue(ALIVE, random.nextDouble() > 0.25), 2);
            server.playSound(null, bPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 0.5f, 0.75f);
        }
    }


    @Override
    public @NotNull InteractionResult use(@NotNull BlockState bState, @NotNull Level level, @NotNull BlockPos bPos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof AxeItem axe){
            level.playSound(null, bPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1f, 0.75f);
            level.setBlock(bPos, EntomoBlocks.MYIATIC_FLESH.get().defaultBlockState(), 3);

            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(axe));
                player.swing(hand);
                if (!player.isCreative()){
                    item.hurt(1, level.random, player instanceof ServerPlayer s ? s : null);
                }
            }

            RandomSource random = level.random;
            Vec3 center = bPos.getCenter();
            for (int i = 0; i < random.nextInt(8, 16); i++){
                double x = center.x + random.nextDouble() * (random.nextBoolean() ? 1 : -1);
                double y = center.y + random.nextDouble() * (random.nextBoolean() ? 1 : -1);
                double z = center.z + random.nextDouble() * (random.nextBoolean() ? 1 : -1);

                level.addParticle(ParticleTypes.WAX_OFF, x, y, z, 0, -0.1, 0);
            }
        }

        return InteractionResult.PASS;
    }
}
