package mod.pilot.entomophobia.systems.nest.features;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.data.WeightedRandomizer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**A class that extends Feature, for use in variant management, allowing for weighted variants.*/
public abstract class FeatureVariantPackage extends Feature{
    /**The "Common" String between all the variants.
     This plus the Identifier (see Instance nested class) should refer to a structure NBT file*/
    public final String Common;
    public final String ModRoot;
    /**Private reference to itself so nested classes can access the package's super values rather than their own
     * (since they both extend Feature)*/
    private final FeatureVariantPackage parentPackage = this;
    /**Default weight for the Randomizer*/
    public final int defaultWeight;
    /**A WeightedRandomizer with all the Instances, for use in retrieving a random variant*/
    public final WeightedRandomizer<Instance> instances;
    public FeatureVariantPackage(int offshootType, int placementPos, String common, int weight) {
        this(offshootType, placementPos, common, weight, Entomophobia.MOD_ID);
    }
    public FeatureVariantPackage(int offshootType, int placementPos, String common, int weight, String modRoot) {
        super(common, offshootType, placementPos, null);
        this.Common = common;
        this.defaultWeight = weight;
        instances = new WeightedRandomizer<>(defaultWeight);
        this.ModRoot = modRoot;
    }
    //Delicious Shorthand
    public @Nullable Instance getRandomInstance(){
        return instances.getRandomWeightedObject();
    }

    /* Useless cuz Place() now manages this using getTemplate()
    public boolean PlaceRandomInstance(Vec3 position, ServerLevel server, @Nullable Rotation rotation, @Nullable Direction facing){
        Instance feature = getRandomInstance();
        if (feature == null) return false;
        feature.Place(position, server, rotation, facing);
        return true;
    }*/

    @Override
    public boolean Place(Vec3 position, ServerLevel server, @Nullable Rotation rotation, @Nullable Direction facing) {
        Instance i = getRandomInstance();
        if (i == null){
            printIllegalAction(illegalActionPackageEmpty());
            return false;
        }
        return i.Place(position, server, rotation, facing);
    }

    @Override
    public @Nullable StructureTemplate getTemplate(ServerLevel server, @Nullable Direction facing) {
        System.err.println("WARNING! AN ILLEGAL ATTEMPT TO CALL getTemplate(ServerLevel, Direction) IN A VARIANT PACKAGE WAS MADE!");
        System.err.println("Call getRandomInstance() to get access to an instance of the package, or use FeatureVariantPackage.Place(args)");
        return null;
    }

    /**Generates all the Instances and then adds them to the WeightedRandomizer.
     Required to be overridden when creating a new FeatureVariantPackage*/
    public abstract void GenerateInstances();

    //First use of non-static nested classes :GodHasLeftUs:
    public class Instance extends Feature{
        /**
         * The Identifier of this given variant Instance. Put simply, it's the trailing String that differentiates it from the rest of the variants
         *  (usually just a number or similar)
         * <p></p>
         * The Common plus this should reference a structure NBT if used for a ResourceLocation
         */
        public final String Identifier;
        public Instance(String identifier){
            this(identifier, defaultWeight);
        }
        public Instance(String identifier, int weight){
            super(Common + identifier, parentPackage.OffshootType, parentPackage.PlacementPos,
                    new ResourceLocation(ModRoot, Common + identifier));
            this.Identifier = identifier;
            instances.add(this, weight);
        }

        @Override
        public StructureTemplate getTemplate(ServerLevel server, @Nullable Direction facing) {
            return super.getTemplate(server, facing);
        }
    }
}
