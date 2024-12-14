package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.particles.EntomoParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BloodDripWand extends Item {
    public BloodDripWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (level.isClientSide){
            level.addParticle(EntomoParticles.BLOOD_HANG_PARTICLE.get(), pPlayer.getX(), pPlayer.getY() + 2, pPlayer.getZ(),
                    0, 0, 0);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, new ItemStack(this));
    }
}
