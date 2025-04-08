package mod.pilot.entomophobia.systems.nest.features;

import mod.pilot.entomophobia.data.WeightedRandomizer;
import mod.pilot.entomophobia.systems.nest.features.ceiling.FleshClumpCeilingFeaturePackage;
import mod.pilot.entomophobia.systems.nest.features.ground.BloodpitFeature;
import mod.pilot.entomophobia.systems.nest.features.ground.CorpsedewCombFeaturePackage;
import mod.pilot.entomophobia.systems.nest.features.ground.FleshClumpFeaturePackage;
import mod.pilot.entomophobia.systems.nest.features.ground.WaxComblessFeaturePackage;
import mod.pilot.entomophobia.systems.nest.features.wall.FleshClumpWallFeature;

import java.util.HashMap;

import static mod.pilot.entomophobia.systems.nest.features.FeatureManager.FeatureTypeHolder.RegisterFeature;

public class FeatureManager {
    //Private constructor because we don't need to ever create an instance of this class-- it's effectively static by C#'s rules.
    //Extends to nested classes
    private FeatureManager(){}

    public static void registerAllFeatures() {
        /*Testing Features*/
        //RegisterFeature(new YesFeature());
        //RegisterFeature(new WallTestFeature());
        //RegisterFeature(new ThickWallTestFeature());

        /*Variant Packages*/
        /*Ground*/
        RegisterFeature(new FleshClumpFeaturePackage());
        RegisterFeature(new WaxComblessFeaturePackage(), 30);
        RegisterFeature(new CorpsedewCombFeaturePackage(), 10);
        /*Wall*/
        //Empty :[
        /*Ceiling*/
        RegisterFeature(new FleshClumpCeilingFeaturePackage());

        /*Solo Features*/
        /*Ground*/
        RegisterFeature(new BloodpitFeature(), 5);
        /*Wall*/
        RegisterFeature(new FleshClumpWallFeature());
        /*Ceiling*/
    }

    public static class FeatureTypeHolder {
        private static final int defaultWeight = 50;
        private static final WeightedRandomizer<Feature> randomizer = new WeightedRandomizer<>(defaultWeight);
        public static final OffshootSpecificHolder Any;
        public static final OffshootSpecificHolder ChamberOnly;
        public static final OffshootSpecificHolder CorridorOnly;

        private FeatureTypeHolder(){}

        static{
            Any = new OffshootSpecificHolder((byte)0);
            ChamberOnly = new OffshootSpecificHolder((byte)1);
            CorridorOnly = new OffshootSpecificHolder((byte)2);
        }
        public static Feature getRandomFeature(byte oTypeB, byte pPosB){
            Feature.OffshootTypes oType = Feature.OffshootTypes.fromByte(oTypeB);
            Feature.PlacementPositions pPos = Feature.PlacementPositions.fromByte(pPosB);
            if (oType == null){
                System.err.println("[FEATURE MANAGER] Warning! Argument byte oTypeB (" + oTypeB + ") holds an invalid value! Defaulting to \"Any\"...");
                oType = Feature.OffshootTypes.Any;
            }
            if (pPos == null){
                System.err.println("[FEATURE MANAGER] Warning! Argument byte pPosB (" + pPosB + ") holds an invalid value! Defaulting to \"Any\"...");
                pPos = Feature.PlacementPositions.Any;
            }
            return getRandomFeature(oType, pPos);
        }
        public static Feature getRandomFeature(Feature.OffshootTypes oType, Feature.PlacementPositions pPos){
            return switch (oType){
                case Any -> HandleGatherOTypeAny(pPos);
                case Chamber -> HandleGatherOTypeChamber(pPos);
                case Corridor -> HandleGatherOTypeCorridor(pPos);
            };
        }

        private static Feature HandleGatherOTypeAny(Feature.PlacementPositions pPos) {
            HashMap<Feature, Integer> gathered = null;
            switch (pPos){
                case Any -> {
                    gathered = ChamberOnly.getAny();
                    gathered.putAll(CorridorOnly.getAny());
                }
                case Ground -> {
                    gathered = ChamberOnly.getGround();
                    gathered.putAll(CorridorOnly.getGround());
                }
                case Wall -> {
                    gathered = ChamberOnly.getWall();
                    gathered.putAll(CorridorOnly.getWall());
                }
                case Ceiling -> {
                    gathered = ChamberOnly.getCeiling();
                    gathered.putAll(CorridorOnly.getCeiling());
                }
            }
            randomizer.replaceEntriesWith(gathered);
            return randomizer.getRandomWeightedObject();
        }

        private static Feature HandleGatherOTypeChamber(Feature.PlacementPositions pPos) {
            HashMap<Feature, Integer> gathered = null;
            switch (pPos){
                case Any -> gathered = ChamberOnly.getAny();
                case Ground -> gathered = ChamberOnly.getGround();
                case Wall -> gathered = ChamberOnly.getWall();
                case Ceiling -> gathered = ChamberOnly.getCeiling();
            }
            randomizer.replaceEntriesWith(gathered);
            return randomizer.getRandomWeightedObject();
        }

        private static Feature HandleGatherOTypeCorridor(Feature.PlacementPositions pPos) {
            HashMap<Feature, Integer> gathered = null;
            switch (pPos){
                case Any -> gathered = CorridorOnly.getAny();
                case Ground -> gathered = CorridorOnly.getGround();
                case Wall -> gathered = CorridorOnly.getWall();
                case Ceiling -> gathered = CorridorOnly.getCeiling();
            }
            randomizer.replaceEntriesWith(gathered);
            return randomizer.getRandomWeightedObject();
        }
        public static void RegisterFeature(Feature feature){
            RegisterFeature(feature, defaultWeight);
        }
        public static void RegisterFeature(Feature feature, int weight){
            Feature.OffshootTypes oType =  Feature.OffshootTypes.fromByte(feature.OffshootType);
            Feature.PlacementPositions pPos =  Feature.PlacementPositions.fromByte(feature.PlacementPos);
            if (oType == null || pPos == null){
                System.err.println("[FEATURE MANAGER] WARNING! Argument Feature " + feature + " is invalid!" +
                        "because either OffshootType ( " + oType + ") OR PlacementPos (" + pPos + ") contained an invalid value.");
                System.err.println("[FEATURE MANAGER] Terminating addition...");
                return;
            }

            switch (oType){
                case Any -> {
                    HandleChamberOTypeAddition(feature, pPos, weight);
                    HandleCorridorOTypeAddition(feature, pPos, weight);
                }
                case Chamber -> HandleChamberOTypeAddition(feature, pPos, weight);
                case Corridor -> HandleCorridorOTypeAddition(feature, pPos, weight);
            }

            if (feature instanceof FeatureVariantPackage fvp){
                fvp.GenerateInstances();
            }
        }
        private static void HandleChamberOTypeAddition(Feature feature, Feature.PlacementPositions pPos, int weight) {
            switch (pPos){
                case Any -> {
                    ChamberOnly.addToGround(feature, weight);
                    ChamberOnly.addToCeiling(feature, weight);
                    ChamberOnly.addToWall(feature, weight);
                }
                case Ground -> ChamberOnly.addToGround(feature, weight);
                case Ceiling -> ChamberOnly.addToCeiling(feature, weight);
                case Wall -> ChamberOnly.addToWall(feature, weight);
            }
        }
        private static void HandleCorridorOTypeAddition(Feature feature, Feature.PlacementPositions pPos, int weight) {
            switch (pPos){
                case Any -> {
                    CorridorOnly.addToGround(feature, weight);
                    CorridorOnly.addToCeiling(feature, weight);
                    CorridorOnly.addToWall(feature, weight);
                }
                case Ground -> CorridorOnly.addToGround(feature, weight);
                case Ceiling -> CorridorOnly.addToCeiling(feature, weight);
                case Wall -> CorridorOnly.addToWall(feature, weight);
            }
        }

        private static class OffshootSpecificHolder{
            private OffshootSpecificHolder(byte type){PlacementPosType = type;}
            public byte PlacementPosType;

            public HashMap<Feature, Integer> getAny() {
                HashMap<Feature, Integer> toReturn = new HashMap<>(getGround());
                toReturn.putAll(getCeiling());
                toReturn.putAll(getWall());
                return toReturn;
            }

            private final HashMap<Feature, Integer> ground = new HashMap<>();
            public HashMap<Feature, Integer> getGround() {return new HashMap<>(ground);}
            public void addToGround(Feature toAdd, int weight) {ground.put(toAdd, weight);}

            private final HashMap<Feature, Integer> wall = new HashMap<>();
            public HashMap<Feature, Integer> getWall() {return new HashMap<>(wall);}
            public void addToWall(Feature toAdd, int weight) {wall.put(toAdd, weight);}

            private final HashMap<Feature, Integer> ceiling = new HashMap<>();
            public HashMap<Feature, Integer> getCeiling() {return new HashMap<>(ceiling);}
            public void addToCeiling(Feature toAdd, int weight) {ceiling.put(toAdd, weight);}
        }
    }
}
