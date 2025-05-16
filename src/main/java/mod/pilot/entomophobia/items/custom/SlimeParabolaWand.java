package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.ParabolaCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SlimeParabolaWand extends Item {
    public SlimeParabolaWand(Properties pProperties) {
        super(pProperties);
    }

    public static ParabolaCalculator parabola = new ParabolaCalculator(-0.01);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()){
            Vec3 pos = player.position();
            parabola.parabolaCenter = new Vector3d(pos.x, pos.y, pos.z);
            player.displayClientMessage(Component.literal("Set parabola center to " + pos), true);
            player.getCooldowns().addCooldown(this, 5);
        } else {
            Slime slime = EntityType.SLIME.create(level);
            assert slime != null;
            slime.setNoAi(true);
            slime.setNoGravity(true);
            slime.setPos(parabola.calculateParabolaWorldPositionFromVector(player.position()));
            level.addFreshEntity(slime);
            player.displayClientMessage(Component.literal("Created slime at Y level " + slime.position().y), true);
            player.getCooldowns().addCooldown(this, 2);
        }
        return super.use(level, player, hand);
    }
}
