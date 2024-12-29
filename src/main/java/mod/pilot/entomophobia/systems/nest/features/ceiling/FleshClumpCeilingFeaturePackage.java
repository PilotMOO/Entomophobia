package mod.pilot.entomophobia.systems.nest.features.ceiling;

import mod.pilot.entomophobia.systems.nest.features.FeatureVariantPackage;

public class FleshClumpCeilingFeaturePackage extends FeatureVariantPackage {
    public FleshClumpCeilingFeaturePackage() {
        super(1, 3, "flesh_clump_ceiling", 10);
    }

    @Override
    public void GenerateInstances() {
        instances.add(new Instance("1"), 5);
        instances.add(new Instance("2"), 10);
        instances.add(new Instance("3"), 5);
    }
}
