package mod.pilot.entomophobia.entity.client.testing;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.testing.SkeletonPuppetTestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkeletonPuppetTestModel extends DefaultedEntityGeoModel<SkeletonPuppetTestEntity> {
    public SkeletonPuppetTestModel() {
        super(new ResourceLocation(Entomophobia.MOD_ID, "test_puppet"));
    }
}
