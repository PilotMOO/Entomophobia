package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.ParabolaCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

public class WorldXPositionParabolaWand extends Item {
    public WorldXPositionParabolaWand(Properties pProperties) {
        super(pProperties);
    }

    public static ParabolaCalculator calculator = new ParabolaCalculator(0.1f);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 pos = player.position();
        if (player.isSecondaryUseActive()){
            calculator.setParabolaCenter(pos);
            player.displayClientMessage(Component.literal("Set parabola center to " + pos), true);
            player.getCooldowns().addCooldown(this, 5);
        } else {
            Pair<Vec3, Vec3> solutions = calculator.calculateParabolaXValuesFromWorldPosition(pos);
            if (solutions == null){
                player.displayClientMessage(Component.literal("Position Y value of [" + pos.y + "] is an INVALID VALUE for the given parabola"), true);
                player.getCooldowns().addCooldown(this, 10);
            } else {
                level.setBlock(BlockPos.containing(solutions.getA()), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                level.setBlock(BlockPos.containing(solutions.getB()), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                player.displayClientMessage(Component.literal("World positions are " + solutions.getA() + " and " + solutions.getB()), false);
            }
        }
        return super.use(level, player, hand);
    }
}
