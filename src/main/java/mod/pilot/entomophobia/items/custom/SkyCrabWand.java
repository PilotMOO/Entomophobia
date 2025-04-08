package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.GenericModelRegistry.GenericModelHub;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import mod.pilot.entomophobia.systems.GenericModelRegistry.models.HorseshoeCrabModel;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.SkyboxModelManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkyCrabWand extends Item {
    public SkyCrabWand(Properties pProperties) {
        super(pProperties);
    }

    public static IGenericModel skycrab;
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide()) return super.use(pLevel, pPlayer, pUsedHand);
        if (skycrab == null) skycrab = new HorseshoeCrabModel(GenericModelHub.ModelSet.bakeLayer(HorseshoeCrabModel.LAYER_LOCATION));
        new SkyboxModelManager.RenderPackage(skycrab).offset(0, 5, 4).rotate(-45, 0, 0).que();

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
