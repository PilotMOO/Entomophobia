package mod.pilot.entomophobia.systems.nest.features.ground;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.FeatureVariantPackage;

public class CorpsedewCombFeaturePackage extends FeatureVariantPackage {

    public CorpsedewCombFeaturePackage() {
        super(1, 1, "corpsedew_comb", 10, Entomophobia.MOD_ID);
    }
    @Override
    public void GenerateInstances() {
        instances.add(new Instance("1"));
        instances.add(new Instance("2"));
        instances.add(new Instance("3"));
    }
}
