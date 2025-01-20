package mod.pilot.entomophobia.entity.client.truepest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import mod.pilot.entomophobia.entity.truepest.SpiderPestEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SpiderPestRenderer extends GeoEntityRenderer<SpiderPestEntity> {
    public SpiderPestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiderPestModel());
    }
}
