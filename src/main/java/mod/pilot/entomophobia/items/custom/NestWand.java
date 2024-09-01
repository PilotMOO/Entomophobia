package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class NestWand extends Item {
    public NestWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel server){
            NestManager.ConstructNewNest(server, context.getClickLocation());
        }
        context.getPlayer().displayClientMessage(Component.literal("Makin' a new nest!"), true);
        return InteractionResult.SUCCESS;
    }
}
