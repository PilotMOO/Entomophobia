package mod.pilot.entomophobia.systems.nest.features.ground;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.nest.features.FeatureVariantPackage;

public class WaxComblessFeaturePackage extends FeatureVariantPackage {

    public WaxComblessFeaturePackage() {
        super(1, 1, "wax_combless", 10, Entomophobia.MOD_ID);
    }
    @Override
    public void GenerateInstances() {
        instances.add(new Instance("1"));
        instances.add(new Instance("2"));
        instances.add(new Instance("3"));
    }
}
