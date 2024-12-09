package mod.pilot.entomophobia.systems.nest.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class Feature {
    public enum Types {
        Empty,
        Yes;
        public byte asByte(){
            return (byte)this.ordinal();
        }
        public static @Nullable Feature.Types fromByte(byte b){
            if (b == 0) return Empty;
            if (b == 1) return Yes;

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
        Ceiling,
        Wall;

        public byte asByte(){
            return (byte)this.ordinal();
        }
        public static @Nullable Feature.PlacementPositions fromByte(byte b){
            if (b == 0) return Any;
            if (b == 1) return Ground;
            if (b == 2) return Ceiling;
            if (b == 3) return Wall;

            return null;
        }
    }
    protected Feature(byte type, byte offshootType, byte placementPos, ResourceLocation structureLocation){
        this.Type = type;
        this.OffshootType = offshootType;
        this.PlacementPos = placementPos;
        this.structureLocation = structureLocation;
    }
    public final byte Type;
    public final byte OffshootType;
    public final byte PlacementPos;
    public final ResourceLocation structureLocation;
    public StructureTemplate template;
    public Vec3i size(){
        return template.getSize();
    }
    public void Place(Vec3 position, ServerLevel server){
        if (template == null) {
            template = server.getStructureManager().getOrCreate(structureLocation);
        }
        template.placeInWorld(server, VecToBPos(position), VecToBPos(position),
                new StructurePlaceSettings().setIgnoreEntities(true), server.random, 3);
    }

    protected BlockPos VecToBPos(Vec3 position){
        return new BlockPos((int)position.x, (int)position.y, (int)position.z);
    }

    @Override
    public String toString() {
        return "Feature [Type: " + Types.fromByte(Type) +
                ", OffshootType: " + OffshootTypes.fromByte(OffshootType) +
                ", PlacementPos: " + PlacementPositions.fromByte(PlacementPos) +
                ", Template: " + template +
                ", ResourceLocation: " + structureLocation + "]";
    }
}