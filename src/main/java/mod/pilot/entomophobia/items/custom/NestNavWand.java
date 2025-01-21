package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pathfinding.GroundedNestNavigation;
import mod.pilot.entomophobia.entity.pathfinding.INestPathfinding;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NestNavWand extends Item {
    public NestNavWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player,
                                                           @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof MyiaticBase m && m.getNavigation() instanceof GroundedNestNavigation nNav){
            if (nNav.amIHomeless()){
                nNav.setNestMap(INestPathfinding.NestMap.MapOf(INestPathfinding.getClosestNest(m, nNav)));
                player.displayClientMessage(Component.literal("Assigned the NestMap of this Myiatic to the Closest Entrance"), true);
            }
            else {
                nNav.setMoveDirections(new INestPathfinding.MoveDirections(nNav.getNestMap().currentOffshoot(), false));
                player.displayClientMessage(Component.literal("he's heading to the fucking entrance"), true);
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
