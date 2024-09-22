package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.event.EntomoHandlerEvents;
import mod.pilot.entomophobia.systems.PolyForged.Shapes.TunnelGenerator;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NestSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_nest_world_data";

    public NestSaveData(){
        super();
        server = EntomoHandlerEvents.getServer();
    }
    public static void SetActiveNestData(ServerLevel server){
        Entomophobia.activeNestData = server.getDataStorage().computeIfAbsent(NestSaveData::load, NestSaveData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull NestSaveData activeData(){
        return Entomophobia.activeNestData;
    }
    public static void Dirty(){
        if (Entomophobia.activeNestData == null) return;
        Entomophobia.activeNestData.setDirty();
    }
    public static NestSaveData load(CompoundTag tag){
        NestSaveData data = new NestSaveData();

        NestPackager packager = new NestPackager(tag, data);
        packager.UnpackNests();
        System.out.println("Amount of active nests: " + NestManager.AmountOfActiveNests());

        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        NestPackager packager = new NestPackager(tag, this);
        packager.PackAllNests();

        return tag;
    }
    private ServerLevel server;
    public ServerLevel getServer(){
        return server;
    }
    public void setServer(ServerLevel server){
        this.server = server;
    }

    public static class NestPackager {
        protected NestPackager(CompoundTag tag, NestSaveData data){
            this.tag = tag;
            nestData = data;
        }
        private final CompoundTag tag;
        private static ArrayList<Nest> getNests(){
            return NestManager.getActiveNests();
        }
        private final NestSaveData nestData;
        private final StringBuilder builder = new StringBuilder();

        public void PackAllNests(){
            int nestTracker = 0;
            ArrayList<Nest> nests = getNests();

            for (int n = 0; n < nests.size(); n++){
                Nest current = nests.get(n);
                if (current == null || current.Dead()) continue;

                PackageThisNest(current, n);

                HashMap<Nest.Offshoot, String> childIDs = new HashMap<>();
                childIDs.put(current.MainChamber, PackageThisOffshoot(current.MainChamber, "Nest" + n, n));

                nestTracker++;

                if (current.MainChamber.children == null) continue;
                ArrayList<Nest.Offshoot> currentLayer = current.MainChamber.children;

                while (currentLayer.size() > 0){
                    for (int i = 0; i < currentLayer.size(); i++){
                        Nest.Offshoot child = currentLayer.get(i);
                        childIDs.put(child, PackageThisOffshoot(child, childIDs.get(child.parent), i));
                    }

                    ArrayList<Nest.Offshoot> snapshot = new ArrayList<>(currentLayer);
                    currentLayer.clear();
                    currentLayer = CollectChildrenFrom(snapshot);
                }
            }
            if (nestTracker > 0){
                System.out.println("Packed up all of the nests!");
                System.out.println("Packed up: " + nestTracker + " Nest(s)!");
            }
            else{
                System.out.println("There wasn't any nests in need of packing");
            }
        }
        private ArrayList<Nest.Offshoot> CollectChildrenFrom(ArrayList<Nest.Offshoot> parents){
            ArrayList<Nest.Offshoot> toReturn = new ArrayList<>();
            for (Nest.Offshoot offshoot : parents){
                if (offshoot.children == null) continue;
                toReturn.addAll(offshoot.children);
            }
            return toReturn;
        }

        private void PackageThisNest(Nest nest, int index){
            String nestID = builder.append("Nest").append(index).toString(); CleanBuilder();
            tag.putDouble(builder.append(nestID).append("x").toString(), nest.origin.x); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
            tag.putDouble(builder.append(nestID).append("y").toString(), nest.origin.y); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
            tag.putDouble(builder.append(nestID).append("z").toString(), nest.origin.z); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();

            tag.putByte(builder.append(nestID).append("state").toString(), nest.getNestState()); System.out.println("Packaged up a byte with the ID: " + builder); CleanBuilder();

            System.out.println("Packing up a nest with the ID: " + nestID);
        }
        private String PackageThisOffshoot(Nest.Offshoot offshoot, String parentID, int childIndex){
            String ID = builder.append(parentID).append("offshoot").append(childIndex).toString(); CleanBuilder();
            Nest.Corridor corridor = offshoot instanceof Nest.Corridor ? (Nest.Corridor)offshoot : null;
            Nest.Chamber chamber = offshoot instanceof Nest.Chamber ? (Nest.Chamber)offshoot : null;
            boolean isCorridor = corridor != null;
            Vec3 pos = isCorridor ? corridor.getStartDirect() : offshoot.getPosition();

            tag.putDouble(builder.append(ID).append("x").toString(), pos.x); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
            tag.putDouble(builder.append(ID).append("y").toString(), pos.y); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
            tag.putDouble(builder.append(ID).append("z").toString(), pos.z); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();

            tag.putBoolean(builder.append(ID).append("deadend").toString(), offshoot.DeadEnd); System.out.println("Packaged up a boolean with the ID: " + builder); CleanBuilder();

            tag.putByte(builder.append(ID).append("type").toString(), offshoot.getOffshootType()); System.out.println("Packaged up a byte with the ID: " + builder); CleanBuilder();
            tag.putByte(builder.append(ID).append("state").toString(), offshoot.getOffshootState()); System.out.println("Packaged up a byte with the ID: " + builder); CleanBuilder();

            int size = corridor != null ? corridor.weight : chamber != null ? chamber.radius : -1;
            tag.putInt(builder.append(ID).append("size").toString(), size); System.out.println("Packaged up an int with the ID: " + builder); CleanBuilder();
            int thickness = corridor != null ? corridor.thickness : chamber != null ? chamber.thickness : -1;
            tag.putInt(builder.append(ID).append("thickness").toString(), thickness); System.out.println("Packaged up an int with the ID: " + builder); CleanBuilder();

            if (isCorridor){
                Vec3 end = corridor.end;
                tag.putDouble(builder.append(ID).append("x2").toString(), end.x); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
                tag.putDouble(builder.append(ID).append("y2").toString(), end.y); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
                tag.putDouble(builder.append(ID).append("z2").toString(), end.z); System.out.println("Packaged up a double with the ID: " + builder); CleanBuilder();
            }

            return ID;
        }

        public void UnpackNests(){
            System.out.println("Unpacking nests...");

            int nestIndexTracker = 0;
            String NestID = builder.append("Nest").append(nestIndexTracker).toString(); CleanBuilder();

            boolean flag = tag.contains(builder.append(NestID).append("x").toString(), 99);
            CleanBuilder();
            if (!flag){
                System.out.println("Tag didn't contain " + NestID);
            }
            else{
                while (flag){
                    HashMap<Nest.Offshoot, String> OffshootIDMap = new HashMap<>();

                    builder.append(NestID);
                    double X = tag.getDouble(builder.append("x").toString()); builder.setLength(NestID.length());
                    double Y = tag.getDouble(builder.append("y").toString()); builder.setLength(NestID.length());
                    double Z = tag.getDouble(builder.append("z").toString()); builder.setLength(NestID.length());
                    Vec3 nestPos = new Vec3(X, Y, Z);

                    String mainChamberID = builder.append("offshoot0").toString(); CleanBuilder();
                    Nest.Chamber mainChamber = (Nest.Chamber)ConstructPackagedFromID(mainChamberID).Unpack(null); CleanBuilder();
                    OffshootIDMap.put(mainChamber, mainChamberID);

                    ArrayList<String> OffshootIDs = new ArrayList<>(); OffshootIDs.add(mainChamberID);
                    ArrayList<Nest.Offshoot> parents = new ArrayList<>(); parents.add(mainChamber);

                    boolean childFlag = DoAnyOfTheseOffshootsHaveChildren(OffshootIDs);
                    if (!childFlag){
                        System.out.println("None of the given offshoots had children");
                    }
                    else{
                        while (childFlag){
                            ArrayList<String> newIDs = new ArrayList<>();
                            ArrayList<Nest.Offshoot> newParents = new ArrayList<>();
                            for (String id : OffshootIDs){
                                CleanBuilder();
                                String baseChildID = builder.append(id).append("offshoot").toString();
                                for (int i = 0; tag.contains(builder.append(i).append("x").toString()); i++){
                                    builder.setLength(builder.length() - 1);
                                    newIDs.add(builder.toString());
                                    builder.setLength(baseChildID.length());
                                }
                            }
                            for (String childID : newIDs){
                                for (Nest.Offshoot possibleParent : parents){
                                    if (childID.contains(OffshootIDMap.get(possibleParent))){
                                        Nest.Offshoot child = ConstructPackagedFromID(childID).Unpack(possibleParent);
                                        newParents.add(child); OffshootIDMap.put(child, childID);
                                    }
                                }
                            }

                            parents = new ArrayList<>(newParents);
                            OffshootIDs = new ArrayList<>(newIDs);
                            childFlag = DoAnyOfTheseOffshootsHaveChildren(OffshootIDs);
                        }
                    }
                    CleanBuilder();

                    System.out.println("Finished getting all of the offshoots for " + NestID);

                    byte nestState = tag.getByte(builder.append(NestID).append("state").toString()); CleanBuilder();
                    Nest newNest = Nest.ConstructFromBlueprint(nestData.getServer(), nestPos, nestState, NestManager.getTickFrequency(), mainChamber);
                    NestManager.addToActiveNests(newNest);
                    System.out.println("Fully unpacked " + NestID + " and added it to ActiveNests!");

                    nestIndexTracker++;
                    CleanBuilder();
                    NestID = builder.append("Nest").append(nestIndexTracker).toString(); CleanBuilder();
                    flag = tag.contains(builder.append(NestID).append("x").toString(), 99); CleanBuilder();
                }
            }

            System.out.println("We are finished unpacking the nests, boss!");
            System.out.println("Unpacked: " + nestIndexTracker + " Nest(s)!");
        }

        private PackagedOffshoot ConstructPackagedFromID(String ID) {
            CleanBuilder();
            byte type = tag.getByte(builder.append(ID).append("type").toString()); System.out.println("Unpacking a byte with the ID: " + builder); CleanBuilder();
            PackagedOffshoot toReturn = null;

            builder.append(ID);
            double x = tag.getDouble(builder.append("x").toString()); builder.setLength(ID.length());
            double y = tag.getDouble(builder.append("y").toString()); builder.setLength(ID.length());
            double z = tag.getDouble(builder.append("z").toString()); builder.setLength(ID.length());
            boolean deadEnd = tag.getBoolean(builder.append("deadend").toString()); builder.setLength(ID.length());
            byte state = tag.getByte(builder.append("state").toString()); builder.setLength(ID.length());
            int size = tag.getInt(builder.append("size").toString()); builder.setLength(ID.length());
            int thickness = tag.getInt(builder.append("thickness").toString()); builder.setLength(ID.length());

            if (type == 1){
                System.out.println("Constructed a PackagedChamber with the I.D. " + ID);
                toReturn = new PackagedChamber(this, x, y, z, deadEnd, state, size, thickness);
            }
            if (type == 2){
                double x2 = tag.getDouble(builder.append("x2").toString()); builder.setLength(ID.length());
                double y2 = tag.getDouble(builder.append("y2").toString()); builder.setLength(ID.length());
                double z2 = tag.getDouble(builder.append("z2").toString()); builder.setLength(ID.length());
                CleanBuilder();

                System.out.println("Constructed a PackagedCorridor with the I.D. " + ID);
                toReturn = new PackagedCorridor(this, x, y, z, deadEnd, state, size, thickness, x2, y2, z2);
            }

            return toReturn;
        }

        private boolean DoAnyOfTheseOffshootsHaveChildren(ArrayList<String> offshootIDs) {
            for (String id : offshootIDs){
                CleanBuilder();
                if (tag.contains(builder.append(id).append("offshoot0x").toString())){
                    CleanBuilder();
                    return true;
                }
            }
            return false;
        }

        private void CleanBuilder(){
            builder.setLength(0);
        }

        private static abstract class PackagedOffshoot{
            protected PackagedOffshoot(NestPackager packager, double x, double y, double z, boolean deadEnd, byte state){
                this.packager = packager;
                X = x; Y = y; Z = z;
                DeadEnd = deadEnd;
                this.state = state;
            }

            public final NestPackager packager;

            public final double X;
            public final double Y;
            public final double Z;
            public final boolean DeadEnd;
            public final byte state;
            public byte type = -1;

            public abstract Nest.Offshoot Unpack(@Nullable Nest.Offshoot parent);
            public ServerLevel getServer(){
                return packager.nestData.getServer();
            }
        }
        public static class PackagedChamber extends PackagedOffshoot{
            private PackagedChamber(NestPackager packager, double x, double y, double z, boolean deadEnd, byte state, int radius, int thickness) {
                super(packager, x, y, z, deadEnd, state);
                super.type = 1;
                this.radius = radius;
                this.thickness = thickness;
            }
            public static PackagedChamber PackageOffshoot(Nest.Chamber toPackage, NestPackager packager) {
                Vec3 pos = toPackage.getPosition();
                return new PackagedChamber(packager, pos.x, pos.y, pos.z, toPackage.DeadEnd, toPackage.getOffshootState(), toPackage.radius, toPackage.thickness);
            }

            public final int radius;
            public final int thickness;

            @Override
            public Nest.Offshoot Unpack(@Nullable Nest.Offshoot parent) {
                return Nest.Chamber.ConstructFromPackage(this, parent);
            }
        }
        public static class PackagedCorridor extends PackagedOffshoot{
            private PackagedCorridor(NestPackager packager, double x, double y, double z, boolean deadEnd, byte state, int weight, int thickness, double x2, double y2, double z2) {
                super(packager, x, y, z, deadEnd, state);
                super.type = 2;
                this.weight = weight;
                this.thickness = thickness;
                X2 = x2; Y2 = y2; Z2 = z2;
            }
            public static PackagedCorridor PackageOffshoot(Nest.Corridor toPackage, NestPackager packager) {
                TunnelGenerator tunnel = (TunnelGenerator)toPackage.getGenerator();
                Vec3 start = tunnel.getStart();
                Vec3 end = tunnel.getEnd();

                return new PackagedCorridor(packager, start.x, start.y, start.z, toPackage.DeadEnd, toPackage.getOffshootState(), toPackage.weight, toPackage.thickness, end.x, end.y, end.z);
            }

            public final double X2;
            public final double Y2;
            public final double Z2;
            public final int weight;
            public final int thickness;
            @Override
            public Nest.Offshoot Unpack(Nest.Offshoot parent) {
                return Nest.Corridor.ConstructFromPackage(this, parent);
            }
        }
    }
}
