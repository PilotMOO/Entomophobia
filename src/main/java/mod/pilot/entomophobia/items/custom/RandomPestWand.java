package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.entity.PestManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RandomPestWand extends Item {
    public RandomPestWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        PestManager.createPestAt(pLevel, pPlayer.position(), pPlayer);
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
