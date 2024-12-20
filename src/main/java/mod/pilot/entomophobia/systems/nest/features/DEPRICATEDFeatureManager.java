package mod.pilot.entomophobia.systems.nest.features;

import mod.pilot.entomophobia.data.WeightedRandomizer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the first attempt at the Feature Manager I made, I realized it was very poorly built and needlessly convoluted,
 * but I didn't feel like deleting it, so I kept it. Check out FeatureManager in the same package for the real one.
 * <p>
 * DO NOT ATTEMPT TO USE THIS
 */
@Deprecated
public class DEPRICATEDFeatureManager {

    public static class Variants {
        protected static ArrayList<VariantHolder<?>> holders = new ArrayList<>();
        public static ArrayList<VariantHolder<?>> getVariantHolders(){
            return new ArrayList<>(holders);
        }
        public @Nullable VariantHolder<?> filterForHolderWithTypeOf(Feature.Types type){
            for (VariantHolder<?> vH : getVariantHolders()){
                if (vH.testForVariantType(type)) return vH;
            }
            return null;
        }
        public @Nullable VariantHolder<?> filterWithArguments(VariantArguments vArgs){
            for (VariantHolder<?> vH : getVariantHolders()){
                if (vH.testHolderFor(vArgs)) return vH;
            }
            return null;
        }

        public static class VariantArguments{
            public VariantArguments(){}

            private Feature.PlacementPositions placement;
            public VariantArguments setPlacement(Feature.PlacementPositions pos){
                placement = pos;
                return this;
            }
            public @Nullable Feature.PlacementPositions getPlacement(){ return placement; }

            private Feature.OffshootTypes offshootType;
            public VariantArguments setOffshootType(Feature.OffshootTypes type){
                offshootType = type;
                return this;
            }
            public @Nullable Feature.OffshootTypes getOffshootType(){ return offshootType; }

            private Feature.Types featureType;
            public VariantArguments setFeatureType(Feature.Types types){
                featureType = types;
                return this;
            }
            //Turned to string to satisfy the compiler,
            // needs to be Feature.Types to be used properly but this class is Deprecated and unused so IDGAF
            public @Nullable /*Feature.Types*/ String getFeatureType(){ return "featureType"; }

            public boolean filter(Feature feature){
                if (getPlacement() == null || feature.PlacementPos == getPlacement().asByte()){
                    if (getOffshootType() == null || feature.OffshootType == getOffshootType().asByte()){
                        return getFeatureType() == null || feature.Type == getFeatureType()/*.asByte()*/;
                    }
                    else return false;
                }
                else return false;
            }
        }
        public record VariantTag(ArrayList<Feature.Types> types,
                                 ArrayList<Feature.OffshootTypes> offshootTypes,
                                 ArrayList<Feature.PlacementPositions> placementPositions){
            public VariantTag(@Nullable ArrayList<Feature.Types> types,
                              @Nullable ArrayList<Feature.OffshootTypes> offshootTypes,
                              @Nullable ArrayList<Feature.PlacementPositions> placementPositions){
                this.types = types != null ? types : new ArrayList<>();
                this.offshootTypes = offshootTypes != null ? offshootTypes : new ArrayList<>();
                this.placementPositions = placementPositions != null ? placementPositions : new ArrayList<>();
            }
            public VariantTag(@Nullable Feature.Types type,
                              @Nullable Feature.OffshootTypes offshootType,
                              @Nullable Feature.PlacementPositions placementPos){
                this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                if (type != null) this.add(type);
                if (offshootType != null) this.add(offshootType);
                if (placementPos != null) this.add(placementPos);
            }
            public boolean has(Feature.Types types){
                return this.types.contains(types);
            }
            public boolean has(Feature.OffshootTypes type){
                return offshootTypes.contains(type);
            }
            public boolean has(Feature.PlacementPositions pos){
                return placementPositions.contains(pos);
            }

            public boolean add(Feature.Types types){
                return !this.types.contains(types) && this.types.add(types);
            }
            public boolean add(Feature.OffshootTypes type){
                return !offshootTypes.contains(type) && offshootTypes.add(type);
            }
            public boolean add(Feature.PlacementPositions pos){
                return !placementPositions.contains(pos) && placementPositions.add(pos);
            }

            public void update(Feature update){
                //Commented out due to changes to Feature class
                //add(Feature.Types.fromByte(update.Type));
                add(Feature.OffshootTypes.fromByte(update.OffshootType));
                add(Feature.PlacementPositions.fromByte(update.PlacementPos));
            }
        }

        public static abstract class VariantHolder<T extends Feature>{
            private final HashMap<T, Integer> weightHashmap = new HashMap<>();
            private final ArrayList<T> features = new ArrayList<>();
            public WeightedRandomizer<T> defaultRandomizer;


            private final int baseWeight;
            public final VariantTag tag;
            protected VariantHolder(int baseWeight){
                this.baseWeight = baseWeight;
                tag = new VariantTag((ArrayList<Feature.Types>)null, null, null);
            }

            public abstract Feature.Types getVariantCollectiveType();
            public boolean testForVariantType(Feature.Types types){
                return getVariantCollectiveType() == types;
            }
            public boolean testHolderFor(VariantArguments vArgs){
                //Commented out due to changes in feature class
                //if (vArgs.getFeatureType() != null && !tag.has(vArgs.getFeatureType())) return false;
                if (vArgs.getOffshootType() != null && !tag.has(vArgs.getOffshootType())) return false;
                if (vArgs.getPlacement() != null && !tag.has(vArgs.getPlacement())) return false;

                return true;
            }
            public void Add(T t){
                Add(t, baseWeight);
            }
            public void Add(T t, int weight){
                weightHashmap.put(t, weight);
                features.add(t);
                defaultRandomizer = createRandomizer();
                tag.update(t);
            }

            public WeightedRandomizer<T> createRandomizer(){
                return createRandomizer(null);
            }
            public WeightedRandomizer<T> createRandomizer(@Nullable VariantArguments vArgs){
                ArrayList<T> validFeatures;
                if (vArgs != null){
                    validFeatures = new ArrayList<>();
                    for (T t : features){
                        if (vArgs.filter(t)) validFeatures.add(t);
                    }
                }
                else{
                    validFeatures = features;
                }

                HashMap<T, Integer> newWeights = new HashMap<>();
                for (T valid : validFeatures){
                    newWeights.put(valid, weightHashmap.getOrDefault(valid, baseWeight));
                }

                return new WeightedRandomizer<>(newWeights, baseWeight);
            }

            public @Nullable T getRandomFeature(){
                return getRandomFeature(null);
            }
            public @Nullable T getRandomFeature(@Nullable VariantArguments vArgs){
                if (vArgs == null){
                    return defaultRandomizer.getRandomWeightedObject();
                }
                else{
                    return createRandomizer(vArgs).getRandomWeightedObject();
                }
            }
        }
    }
}
