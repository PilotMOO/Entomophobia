package mod.pilot.entomophobia.entity.projectile;

import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.entity.EntomoEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class CongealedBloodProjectile extends ThrowableItemProjectile {

    public CongealedBloodProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EntomoBlocks.CONGEALED_BLOOD.get().asItem();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        /*BlockPos directBPos = result.getBlockPos();
        BlockState directBState = level().getBlockState(directBPos);*/

        BlockPos adjacentBPos = result.getBlockPos().relative(result.getDirection());
        BlockState adjacentBState = level().getBlockState(adjacentBPos);

        /*if (directBState.is(EntomoBlocks.CONGEALED_BLOOD.get())){
            int layers = directBState.getValue(BlockStateProperties.LAYERS);
            if (layers < SnowLayerBlock.MAX_HEIGHT){
                level().setBlock(directBPos, directBState.setValue(BlockStateProperties.LAYERS, layers + 1), 3);
                discard();
                return;
            }
        }*/

        if (adjacentBState.canBeReplaced()){
            level().setBlock(adjacentBPos, EntomoBlocks.CONGEALED_BLOOD.get().defaultBlockState(), 3);
            level().playSound(null, adjacentBPos, EntomoBlocks.CONGEALED_BLOOD.get().getSoundType(adjacentBState).getPlaceSound(),
                    SoundSource.BLOCKS,1f, 1f);
            discard();
        }
    }
}
