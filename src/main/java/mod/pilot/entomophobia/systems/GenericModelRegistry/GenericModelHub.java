package mod.pilot.entomophobia.systems.GenericModelRegistry;

import mod.pilot.entomophobia.systems.GenericModelRegistry.models.HorseshoeCrabModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class GenericModelHub {
    public static void setup(){
        System.out.println("[GENERIC MODEL HUB] Setup started!");
        ModelSet = new GenericModelSet();
        _registerInternalGenericModels();
    }

    private static void _registerInternalGenericModels(){
        System.out.println("Registering internal models...");
        registerGenericModel(HorseshoeCrabModel.LAYER_LOCATION, HorseshoeCrabModel::createLayer);
        //RegisterGenericModel(EXAMPLE_MODEL.LAYER_LOCATION, EXAMPLE_MODEL::createLayer);
    }

    public static void registerGenericModel(ModelLayerLocation location, Supplier<LayerDefinition> supplier){
        ModelSet.addRoot(location, supplier.get());
    }
    public static GenericModelSet ModelSet;

    public static class GenericModelSet {
        private final Map<ModelLayerLocation, LayerDefinition> roots = new HashMap<>();
        public void addRoot(ModelLayerLocation key, LayerDefinition value){
            this.roots.put(key, value);
        }

        public ModelPart bakeLayer(ModelLayerLocation pModelLayerLocation) {
            LayerDefinition layerdefinition = this.roots.get(pModelLayerLocation);
            if (layerdefinition == null) {
                throw new IllegalArgumentException("No model for layer " + pModelLayerLocation);
            } else {
                return layerdefinition.bakeRoot();
            }
        }
    }
}
