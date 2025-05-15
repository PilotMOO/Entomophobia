package mod.pilot.entomophobia.systems.nest.features;

import mod.pilot.entomophobia.data.EntomoDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public abstract class Feature {
    @Deprecated
    public enum Types {
        Empty,
        Yes,
        WallTest;
        public byte asByte(){
            return (byte)this.ordinal();
        }
        public static @Nullable Feature.Types fromByte(byte b){
            if (b == 0) return Empty;
            if (b == 1) return Yes;
            if (b == 2) return WallTest;

            return null;
        }
    }
    public enum OffshootTypes {
        Any,
        Chamber,
        Corridor;

        public byte asByte(){
            return (byte)this.ordinal();
        }
        public static @Nullable Feature.OffshootTypes fromByte(byte b){
            if (b == 0) return Any;
            if (b == 1) return Chamber;
            if (b == 2) return Corridor;

            return null;
        }
    }
    public enum PlacementPositions {
        Any,
        Ground,
        Wall,
        Ceiling;

        public byte asByte(){
            return (byte)this.ordinal();
        }
        public static @Nullable Feature.PlacementPositions fromByte(byte b){
            if (b == 0) return Any;
            if (b == 1) return Ground;
            if (b == 2) return Wall;
            if (b == 3) return Ceiling;

            return null;
        }
    }
    protected Feature(String type, int offshootType, int placementPos, ResourceLocation structureLocation){
        this.Type = type;
        this.OffshootType = (byte)offshootType;
        this.PlacementPos = (byte)placementPos;
        this.structureLocation = structureLocation;
    }
    protected void registerAsWallFeature(@Nullable Collection<? extends ResourceLocation> northStructures,
                                         @Nullable Collection<? extends ResourceLocation> eastStructures,
                                         @Nullable Collection<? extends ResourceLocation> southStructures,
                                         @Nullable Collection<? extends ResourceLocation> westStructures){
        if (PlacementPos != PlacementPositions.Wall.asByte() && PlacementPos != PlacementPositions.Any.asByte()){
            System.err.println();
            System.err.println("---------------");
            System.err.println("Warning! An attempt to assign a Non-Wall or Non-Any feature as a Wall Feature!");
            System.err.println("Attempted to register " + this + " as a Wall Feature which is not denoted as Wall or Any, denote this Feature as "
                    + PlacementPositions.Wall + " or " + PlacementPositions.Any + " if you wish to register this Feature as a Wall Feature");
            System.err.println("Canceling registration...");
            System.err.println("---------------");
            System.err.println();
            return;
        }
        if (northStructures == null && eastStructures == null && southStructures == null && westStructures == null){
            System.err.println();
            System.err.println("---------------");
            System.err.println("Warning! An attempt to assign a feature as a Wall Feature without any assigned structures!");
            System.err.println("Attempted to register " + this + " as a Wall Feature without any registered structures for the walls! "
                    + "ensure that AT LEAST one argument in the wall registration is NOT null");
            System.err.println("Canceling registration...");
            System.err.println("---------------");
            System.err.println();
            return;
        }

        wallResourceLocationHashmap = new HashMap<>();
        wallStructureHashmap = new HashMap<>();

        if (northStructures != null){
            for (ResourceLocation r : northStructures){
                wallResourceLocationHashmap.put(Direction.NORTH, r);
            }
        }
        if (eastStructures != null){
            for (ResourceLocation r : eastStructures){
                wallResourceLocationHashmap.put(Direction.EAST, r);
            }
        }
        if (southStructures != null){
            for (ResourceLocation r : southStructures){
                wallResourceLocationHashmap.put(Direction.SOUTH, r);
            }
        }
        if (westStructures != null){
            for (ResourceLocation r : westStructures){
                wallResourceLocationHashmap.put(Direction.WEST, r);
            }
        }
        wallFeature = true;
    }
    protected void registerAsWallFeature(@Nullable ResourceLocation northStructure,
                                         @Nullable ResourceLocation eastStructure,
                                         @Nullable ResourceLocation southStructure,
                                         @Nullable ResourceLocation westStructure){
        if (PlacementPos != PlacementPositions.Wall.asByte() && PlacementPos != PlacementPositions.Any.asByte()){
            System.err.println();
            System.err.println("---------------");
            System.err.println("Warning! An attempt to assign a Non-Wall or Non-Any feature as a Wall Feature!");
            System.err.println("Attempted to register " + this + " as a Wall Feature which is not denoted as Wall or Any, denote this Feature as "
                    + PlacementPositions.Wall + " or " + PlacementPositions.Any + " if you wish to register this Feature as a Wall Feature");
            System.err.println("Canceling registration...");
            System.err.println("---------------");
            System.err.println();
            return;
        }
        if (northStructure == null && eastStructure == null && southStructure == null && westStructure == null){
            System.err.println();
            System.err.println("---------------");
            System.err.println("Warning! An attempt to assign a feature as a Wall Feature without any assigned structures!");
            System.err.println("Attempted to register " + this + " as a Wall Feature without any registered structures for the walls! "
                    + "ensure that AT LEAST one argument in the wall registration is NOT null");
            System.err.println("Canceling registration...");
            System.err.println("---------------");
            System.err.println();
            return;
        }

        wallResourceLocationHashmap = new HashMap<>();
        wallStructureHashmap = new HashMap<>();

        if (northStructure != null){
            wallResourceLocationHashmap.put(Direction.NORTH, northStructure);
        }
        if (eastStructure != null){
            wallResourceLocationHashmap.put(Direction.EAST, eastStructure);
        }
        if (southStructure != null){
            wallResourceLocationHashmap.put(Direction.SOUTH, southStructure);
        }
        if (westStructure != null){
            wallResourceLocationHashmap.put(Direction.WEST, westStructure);
        }
        wallFeature = true;
    }
    public final String Type;
    public final byte OffshootType;
    public final byte PlacementPos;
    public final ResourceLocation structureLocation;
    public StructureTemplate template;
    protected @Nullable HashMap<Direction, ResourceLocation> wallResourceLocationHashmap = null;
    protected @Nullable HashMap<Direction, StructureTemplate> wallStructureHashmap = null;
    private boolean wallFeature = false;
    public final boolean isWallFeature(){
        return wallFeature;
    }
    public final boolean isVariantPackage(){
        return this instanceof FeatureVariantPackage;
    }
    public Vec3i size(){
        return template.getSize();
    }
    public boolean Place(Vec3 position, ServerLevel server, @Nullable Rotation rotation, @Nullable Direction facing){
        StructureTemplate template = getTemplate(server, facing);

        Vec3 offset = getPlaceOffset(template, facing);
        BlockPos placePos = BlockPos.containing(position.subtract(offset));

        StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(true);
        if (!isWallFeature()) settings = settings.setRotationPivot(BlockPos.containing(offset))
                .setRotation(rotation == null ? Rotation.getRandom(server.random) : rotation);

        return template.placeInWorld(server, placePos, BlockPos.containing(position), settings, server.random, 3);
    }
    public StructureTemplate getTemplate(ServerLevel server, @Nullable Direction facing){
        if (isWallFeature() && facing != null && wallStructureHashmap != null && wallResourceLocationHashmap != null){
            if (!wallStructureHashmap.containsKey(facing)){
                wallResourceLocationHashmap.forEach((direction, resource) ->{
                    if (direction == facing){
                        wallStructureHashmap.put(direction, server.getStructureManager().getOrCreate(resource));
                    }
                });
            }

            ArrayList<StructureTemplate> templateList = new ArrayList<>();
            wallStructureHashmap.forEach((direction, template1) ->{
                if (direction == facing){
                    templateList.add(template1);
                }
            });
            return template = templateList.size() == 0 ?
                    this.template : templateList.get(server.random.nextInt(templateList.size()));
        }
        else if (structureLocation == null){
            printIllegalAction(illegalActionResourceLocationEmpty());
            return null;
        }
        else return Objects.requireNonNullElseGet(this.template,
                () -> this.template = server.getStructureManager().getOrCreate(structureLocation));
    }
    protected Vec3 getPlaceOffset(StructureTemplate template, @Nullable Direction facing){
        if (facing != null) {
            return EntomoDataManager.vec3iToVec3(template.getSize()).multiply(
                                            facing.getStepX() == 0 ? 0.5 : facing.getStepX() == -1 ? 1 : 0,
                                            facing.getStepY() == 1 ? 0 : facing.getStepY() == -1 ? 1 : 0.5,
                                            facing.getStepZ() == 0 ? 0.5 : facing.getStepZ() == -1 ? 1 : 0)
                    .subtract(facing.getStepX() == -1 ? 1 : 0,
                            facing.getStepY() == -1 ? 1 : 0,
                            facing.getStepZ() == -1 ? 1 : 0);
        }
        Vec3 toReturn = EntomoDataManager.vec3iToVec3(template.getSize()).multiply(0.5,0,0.5);
        if (PlacementPos == 3) toReturn.add(0, size().getY(), 0);
        return toReturn;
    }
    @Override
    public String toString() {
        return "Feature [Type: " + Type +
                ", OffshootType: " + OffshootTypes.fromByte(OffshootType) +
                ", PlacementPos: " + PlacementPositions.fromByte(PlacementPos) +
                ", ResourceLocation: " + structureLocation + "]";
    }

    protected void printIllegalAction(String action){
        System.err.println("WARNING! ILLEGAL ACTION: " + action);
    }
    protected String illegalActionPackageEmpty(){
        return "Attempted to place " + this + " but getRandomInstance() returned null!";
    }
    protected String illegalActionResourceLocationEmpty(){
        if (isVariantPackage()){
            return "Attempted to place " + this + " but it is a FeatureVariantPackage! Use getRandomInstance() instead!";
        }
        return "Attempted to place " + this + " but structureLocation was null!";
    }
}