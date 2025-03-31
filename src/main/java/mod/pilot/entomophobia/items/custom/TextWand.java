package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.screentextdisplay.PhantomTextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import mod.pilot.entomophobia.systems.screentextdisplay.keyframes.ColorKeyframe;
import mod.pilot.entomophobia.systems.screentextdisplay.keyframes.PositionalKeyframe;
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
                TextInstance instance =PhantomTextInstance.create("nah", 3)
                        .updateRate(30 + pLevel.random.nextInt(-5, 10))
                        .withAlphaDifference(75)
                        .withIncrementalSize(0.8f)
                        .at(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height))
                        .aged(200)
                        .withColor(new Color(0, 255, 0, 255))
                        .ofSize(pLevel.random.nextFloat() + 1)
                        .shiftPosition(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height), true)
                        .shiftColor(Color.BLUE, 100);
                instance.addKeyframe(new ColorKeyframe(instance, 100,
                        new Color(instance.color.getRed(),
                                instance.color.getBlue(),
                                instance.color.getGreen(),
                                0), 100));
                TextOverlay.instance.textInstances.add(instance);
            } else {
                TextInstance instance = TextInstance.create("yuh")
                        .at(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height))
                        .ofSize(1.5f)
                        .aged(-1)
                        .withColor(new Color(255, 255, 0, 255))
                        .withShaking(3)
                        .shiftPosition(pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height), true)
                        .shiftColor(Color.GREEN, 40);
                instance.addKeyframe(new PositionalKeyframe(instance, 50,
                        pLevel.random.nextInt(TextOverlay.width), pLevel.random.nextInt(TextOverlay.height)){
                        @Override
                        public void PostFire() {
                            this.x = pLevel.random.nextInt(TextOverlay.width);
                            this.y = pLevel.random.nextInt(TextOverlay.height);
                        }}
                        .Infinite());
                TextOverlay.instance.textInstances.add(instance);
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
