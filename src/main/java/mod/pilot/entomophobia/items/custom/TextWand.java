package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.screentextdisplay.PhantomTextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.awt.*;

public class TextWand extends Item {
    public TextWand(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide()){
            if (pPlayer.isSecondaryUseActive()){
                TextOverlay.instance.textInstances.add(PhantomTextInstance.create("nah", 3)
                        .updateRate(30 + pLevel.random.nextInt(-5, 10))
                        .withAlphaDifference(75)
                        .withIncrementalSize(0.8f)
                        .at(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height))
                        .withMaxAgeOf(200)
                        .withColor(new Color(0, 255, 0, 255))
                        .ofSize(pLevel.random.nextFloat() + 1)
                        .ShiftPosition(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height), true)
                        .ShiftColor(Color.BLUE, 100));
            } else {
                TextOverlay.instance.textInstances.add(TextInstance.create("yuh")
                        .at(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height))
                        .ofSize(1.5f)
                        .withMaxAgeOf(80)
                        .withColor(new Color(255, 255, 0, 255))
                        .withShaking(3)
                        .ShiftPosition(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height), true)
                        .ShiftColor(Color.GREEN, 40));
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
