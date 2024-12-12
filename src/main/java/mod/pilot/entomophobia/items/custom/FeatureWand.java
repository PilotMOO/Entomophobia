package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.nest.features.Feature;
import mod.pilot.entomophobia.systems.nest.features.FeatureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class FeatureWand extends Item {
    public FeatureWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel server){
            Feature f = FeatureManager.FeatureTypeHolder.getRandomFeature((byte) 0, (byte) 0);
            f.Place(pContext.getClickedPos().relative(pContext.getClickedFace()).getCenter(), server, null, pContext.getClickedFace());
            pContext.getPlayer().displayClientMessage(Component.literal("Placed feature!"), true);
        }
        return InteractionResult.SUCCESS;
    }
}
