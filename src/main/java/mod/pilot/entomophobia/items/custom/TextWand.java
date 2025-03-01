package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.screentextdisplay.TextDisplayManager;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TextWand extends Item {
    public TextWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide()){
            TextDisplayManager.textOverlay.textInstances.add(new TextOverlay.TextInstance("yuh", 6, 6));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
