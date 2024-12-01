package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.particles.EntomoParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlyWand extends Item {
    public FlyWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (level instanceof ServerLevel server){
            server.sendParticles(EntomoParticles.FLY_PARTICLE.get(), pPlayer.getX(), pPlayer.getY(),pPlayer.getZ(),
                    5, 0, 0, 0, 1);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, new ItemStack(this));
    }
}
