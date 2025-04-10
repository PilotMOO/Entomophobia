package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.GenericModelRegistry.GenericModelHub;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import mod.pilot.entomophobia.systems.GenericModelRegistry.models.HorseshoeCrabModel;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.SkyboxModelManager;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.OffsetKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.OrbitKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.RotationKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.ScaleKeyframe;
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
    public static RenderPackage rPackage;
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide()) return super.use(pLevel, pPlayer, pUsedHand);
        if (skycrab == null) skycrab = new HorseshoeCrabModel(GenericModelHub.ModelSet.bakeLayer(HorseshoeCrabModel.LAYER_LOCATION));
        if (rPackage == null) {
            rPackage = new RenderPackage(skycrab)
                    /*.orbit(-70, 45)*/
                    .offset(0, 1, 0)
                    .rotate(0, 0, 0).que();
        } else {
            rPackage.addKeyframe(new OffsetKeyframe(1, 1, 1, 2))
                    .addKeyframe(new RotationKeyframe(0, 360, 0, 100f))
                    .addKeyframe(new ScaleKeyframe(2, 2, 2, 30f));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
