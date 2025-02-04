package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pathfinding.INestPathfinding;
import mod.pilot.entomophobia.systems.nest.Nest;
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
        if (target instanceof MyiaticBase m && m.getNavigation() instanceof INestPathfinding nNav){
            if (nNav.amIHomeless()){
                nNav.setNestMap(INestPathfinding.NestMap.MapOf(INestPathfinding.getClosestNest(m, nNav)));
                player.displayClientMessage(Component.literal("Assigned the NestMap of this Myiatic to the Closest Entrance"), true);
            }
            else {
                Nest.Offshoot o = nNav.getNestMap().currentOffshoot();
                if (o instanceof Nest.Chamber c){
                    nNav.HeadTo(c, 1);
                }
                else if (o instanceof Nest.Corridor c){
                    nNav.HeadTo(c, false, 1);
                }
                player.displayClientMessage(Component.literal("he's heading to the place defined by the map"), true);
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
