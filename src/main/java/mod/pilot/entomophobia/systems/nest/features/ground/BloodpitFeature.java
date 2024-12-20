package mod.pilot.entomophobia.systems.nest.features.ground;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BloodpitFeature extends Feature {
    private static final String type = "blood_pit";
    private static final ResourceLocation structureLocation = new ResourceLocation(Entomophobia.MOD_ID, type);
    public BloodpitFeature() {
        super(type, 1, 1, structureLocation);
    }

    @Override
    protected Vec3 getPlaceOffset(StructureTemplate template, @Nullable Direction facing) {
        return super.getPlaceOffset(template, facing).add(0, 1, 0);
    }
}
