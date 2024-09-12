package mod.pilot.entomophobia.data;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.PolyForged.Shapes.TunnelGenerator;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_world_data";

    public WorldSaveData(){
        super();
        MyiaticStorage = "dummy/";
    }
    public static void SetActiveData(ServerLevel server){
        Entomophobia.activeData = server.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull WorldSaveData activeData(){
        return Entomophobia.activeData;
    }
    public static WorldSaveData load(CompoundTag tag){
        WorldSaveData data = new WorldSaveData();
        if (tag.contains("myiatic_mobcap",99)){
            data.MyiaticCount = tag.getInt("myiatic_mobcap");
        }
        if (tag.contains("has_started")){
            data.HasStarted = tag.getBoolean("has_started");
        }
        if (tag.contains("world_age", 99)){
            data.WorldAge = tag.getInt("world_age");
        }
        if (tag.contains("myiatic_storage")){
            data.MyiaticStorage = tag.getString("myiatic_storage");
        }

        NestPackager packager = new NestPackager(tag, data);
        packager.UnpackNests();

        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putInt("myiatic_mobcap", MyiaticCount);
        tag.putBoolean("has_started", HasStarted);
        tag.putInt("world_age", WorldAge);
        tag.putString("myiatic_storage", MyiaticStorage);

        NestPackager packager = new NestPackager(tag, this);
        packager.PackAllNests();

        return tag;
    }

    private int MyiaticCount = 0;

    public void AddToMyiaticCount(int count){
        MyiaticCount += count;
        setDirty();
    }
    public void AddToMyiaticCount(){
        AddToMyiaticCount(1);
    }
    public void RemoveFromMyiaticCount(int count){
        MyiaticCount -= count;
        setDirty();
    }
    public void RemoveFromMyiaticCount(){
        RemoveFromMyiaticCount(1);
    }
    public int GetMyiaticCount(){
        return activeData().MyiaticCount;
    }

    private boolean HasStarted = false;

    public boolean getHasStarted(){
        return HasStarted;
    }
    public void setHasStarted(boolean flag){
        HasStarted = flag;
    }

    private int WorldAge = 0;
    public int getWorldAge(){
        return WorldAge;
    }
    public void setWorldAge(int age){
        WorldAge = age;
    }
    public void ageWorldBy(int age){
        setWorldAge(getWorldAge() + age);
    }
    public void ageWorld(){
        ageWorldBy(1);
    }

    private String MyiaticStorage;
    public void AddToStorage(String ID){
        MyiaticStorage += ID + "/";
        setDirty();
    }
    public void RemoveFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                splicedList.remove(S);
                break;
            }
        }
        MyiaticStorage = RecompressStorage(splicedList);
    }
    public EntityType<?> GetFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                RemoveFromStorage(S);
                return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(S));
            }
        }
        return null;
    }
    public String GetStringFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                RemoveFromStorage(S);
                return S;
            }
        }
        return null;
    }
    public String GetPhantomStringFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                return S;
            }
        }
        return null;
    }
    public int GetQuantityOf(String ID){
        int amount = 0;
        for (String S : GetSpliced()){
            if (Objects.equals(S, ID)){
                amount++;
            }
        }
        return amount;
    }
    public int GetTotalInStorage(){
        return GetSpliced().length - 1;
    }
    public String[] GetStringFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(GetQuantityOf(ID), amount);
        String[] toReturn = new String[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = GetStringFromStorage(ID);
        }
        return toReturn;
    }
    public EntityType<?>[] GetFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(GetQuantityOf(ID), amount);
        EntityType<?>[] toReturn = new EntityType[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = GetFromStorage(ID);
        }
        return toReturn;
    }
    public String[] GetAnyStringFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(GetTotalInStorage(), amount);
        String[] toReturn = new String[amountToReturn];
        String[] spliced = GetSpliced();
        //Setting i to 1 instead of 0 allows us to avoid the dummy string (in theory lmao)
        for (int i = 1; i < amountToReturn - 1; i++){
            toReturn[i] = spliced[i];
        }
        return toReturn;
    }
    public EntityType<?>[] GetAnyFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(GetTotalInStorage(), amount);
        EntityType<?>[] toReturn = new EntityType<?>[amountToReturn];
        String[] strings = GetAnyStringFromStorageBetweenMax(amountToReturn);
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(strings[i]));
        }
        return toReturn;
    }
    public String GetFirstStringFromStorage(){
        if (GetTotalInStorage() > 1){
            String[] spliced = GetSpliced();
            String toReturn = spliced[1];
            RemoveFromStorage(toReturn);
            return toReturn;
        }
        return null;
    }
    public EntityType<?> GetFirstFromStorage(){
        if (GetTotalInStorage() > 1){
            String[] spliced = GetSpliced();
            EntityType<?> toReturn = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(spliced[1]));;
            RemoveFromStorage(spliced[1]);
            return toReturn;
        }
        return null;
    }
    public String GetFirstPhantomFromStorage(){
        if (GetTotalInStorage() > 1){
            return GetSpliced()[1];
        }
        return null;
    }
    public void RemoveFirstFromStorage(){
        if (GetTotalInStorage() > 1){
            RemoveFromStorage(GetSpliced()[1]);
        }
    }

    private String[] GetSpliced(){
        return MyiaticStorage.split("/");
    }
    private String RecompressStorage(ArrayList<String> splicedList) {
        StringBuilder newString = new StringBuilder();
        for (String S : splicedList){
            newString.append(S).append("/");
        }
        return newString.toString();
    }


    public ServerLevel server;
    public static class NestPackager {
        protected NestPackager(CompoundTag tag, WorldSaveData data){
            this.tag = tag;
            nests = NestManager.ActiveNests;
            entomoWorld = data;
        }
        private final CompoundTag tag;
        private final ArrayList<Nest> nests;
        private final WorldSaveData entomoWorld;
        private final StringBuilder builder = new StringBuilder();

        public void PackAllNests(){
            int temp = 0;
            for (int n = 0; n < nests.size(); n++){
                temp++;

                Nest current = nests.get(n);
                if (current == null || current.Dead()) continue;

                PackageThisNest(current, n);

                HashMap<Nest.Offshoot, String> childIDs = new HashMap<>();
                childIDs.put(current.MainChamber, PackageThisOffshoot(current.MainChamber, "Nest" + n, n));

                if (current.MainChamber.children == null) continue;
                ArrayList<Nest.Offshoot> currentLayer = current.MainChamber.children;

                while (currentLayer.size() > 0){
                    for (int i = 0; i < currentLayer.size(); i++){
                        Nest.Offshoot child = currentLayer.get(i);
                        childIDs.put(child, PackageThisOffshoot(child, childIDs.get(child.parent), i));
                    }

                    currentLayer.clear();
                    currentLayer.addAll(CollectChildrenFrom(currentLayer));
                }}
            System.out.println("Packed up all of the nests!");
            System.out.println("Packed up: " + temp + " Nests!");
        }
        private ArrayList<Nest.Offshoot> CollectChildrenFrom(ArrayList<Nest.Offshoot> parents){
            ArrayList<Nest.Offshoot> toReturn = new ArrayList<>();
            for (Nest.Offshoot offshoot : parents){
                toReturn.addAll(offshoot.children);
            }
            return toReturn;
        }

        private void PackageThisNest(Nest nest, int index){
            String nestID = builder.append("Nest").append(index).toString();
            tag.putDouble(builder.append(nestID).append("x").toString(), nest.origin.x); CleanBuilder();
            tag.putDouble(builder.append(nestID).append("y").toString(), nest.origin.y); CleanBuilder();
            tag.putDouble(builder.append(nestID).append("z").toString(), nest.origin.z); CleanBuilder();

            tag.putByte(builder.append(nestID).append("state").toString(), nest.getNestState()); CleanBuilder();
        }
        private String PackageThisOffshoot(Nest.Offshoot offshoot, String parentID, int childIndex){
            String ID = builder.append(parentID).append("offshoot").append(childIndex).toString(); CleanBuilder();
            Nest.Corridor corridor = offshoot instanceof Nest.Corridor ? (Nest.Corridor)offshoot : null;
            Nest.Chamber chamber = offshoot instanceof Nest.Chamber ? (Nest.Chamber)offshoot : null;
            boolean isCorridor = corridor != null;
            Vec3 pos = isCorridor ? corridor.getStartDirect() : offshoot.getPosition();

            tag.putDouble(builder.append(ID).append("x").toString(), pos.x); CleanBuilder();
            tag.putDouble(builder.append(ID).append("y").toString(), pos.y); CleanBuilder();
            tag.putDouble(builder.append(ID).append("z").toString(), pos.z); CleanBuilder();

            tag.putBoolean(builder.append(ID).append("deadend").toString(), offshoot.DeadEnd); CleanBuilder();

            tag.putByte(builder.append(ID).append("type").toString(), offshoot.getOffshootType()); CleanBuilder();
            tag.putByte(builder.append(ID).append("state").toString(), offshoot.getOffshootState()); CleanBuilder();

            int size = corridor != null ? corridor.weight : chamber != null ? chamber.radius : -1;
            tag.putInt(builder.append(ID).append("size").toString(), size);
            int thickness = corridor != null ? corridor.thickness : chamber != null ? chamber.thickness : -1;
            tag.putInt(builder.append(ID).append("thickness").toString(), thickness);

            if (isCorridor){
                Vec3 end = corridor.end;
                tag.putDouble(builder.append(ID).append("x2").toString(), end.x); CleanBuilder();
                tag.putDouble(builder.append(ID).append("y2").toString(), end.y); CleanBuilder();
                tag.putDouble(builder.append(ID).append("z2").toString(), end.z); CleanBuilder();
            }

            return ID;
        }
        public void UnpackNests(){
            int nestIndexTracker = 0;
            String NestID = builder.append("Nest").append(nestIndexTracker).toString(); CleanBuilder();
            while (tag.contains(NestID)){
                builder.append(NestID);
                double X = tag.getDouble(builder.append("x").toString()); builder.setLength(NestID.length());
                double Y = tag.getDouble(builder.append("y").toString()); builder.setLength(NestID.length());
                double Z = tag.getDouble(builder.append("z").toString()); builder.setLength(NestID.length());
                Vec3 nestPos = new Vec3(X, Y, Z);

                Nest.Chamber mainChamber = (Nest.Chamber)ConstructPackagedFromID(builder.append("offshoot0").toString()).Unpack(null);CleanBuilder();
                Nest.ConstructFromBlueprint(entomoWorld.server, nestPos, NestManager.getTickFrequency(), mainChamber);

                ArrayList<String> OffshootIDs = new ArrayList<>();
                ArrayList<Nest.Offshoot> parents = new ArrayList<>();
                parents.add(mainChamber);

                while (DoAnyOfTheseOffshootsHaveChildren(OffshootIDs)){
                    for (String id : OffshootIDs){
                        PackagedOffshoot packaged = ConstructPackagedFromID(id);
                    }
                }

                nestIndexTracker++;
                NestID = builder.append("Nest").append(nestIndexTracker).toString(); CleanBuilder();
            }
        }


        private PackagedOffshoot ConstructPackagedFromID(String ID) {
            byte type = tag.getByte(builder.append(ID).append("type").toString()); CleanBuilder();
            PackagedOffshoot toReturn = null;

            builder.append(ID);
            double x = tag.getDouble(builder.append("x").toString()); builder.setLength(ID.length());
            double y = tag.getDouble(builder.append("y").toString()); builder.setLength(ID.length());
            double z = tag.getDouble(builder.append("z").toString()); builder.setLength(ID.length());
            boolean deadEnd = tag.getBoolean(builder.append("deadend").toString()); builder.setLength(ID.length());
            byte state = tag.getByte(builder.append("state").toString()); builder.setLength(ID.length());
            int size = tag.getByte(builder.append("size").toString()); builder.setLength(ID.length());
            int thickness = tag.getByte(builder.append("thickness").toString()); builder.setLength(ID.length());

            if (type == 1){
                toReturn = new PackagedChamber(this, x, y, z, deadEnd, state, size, thickness);
            }
            if (type == 2){
                double x2 = tag.getDouble(builder.append("x2").toString()); builder.setLength(ID.length());
                double y2 = tag.getDouble(builder.append("y2").toString()); builder.setLength(ID.length());
                double z2 = tag.getDouble(builder.append("z2").toString()); builder.setLength(ID.length());

                toReturn = new PackagedCorridor(this, x, y, z, deadEnd, state, x2, y2, z2);
            }

            return toReturn;
        }

        private boolean DoAnyOfTheseOffshootsHaveChildren(ArrayList<String> offshootIDs) {
            CleanBuilder();

            boolean flag = false;
            for (String id : offshootIDs){
                flag = tag.contains(builder.append(id).append("offshoot0").toString());
                if (flag) break;
            }
            return flag;
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

            public NestPackager packager;

            public double X;
            public double Y;
            public double Z;
            public boolean DeadEnd;
            public byte state;
            public byte type = -1;

            public abstract Nest.Offshoot Unpack(@Nullable Nest.Offshoot parent);
            public ServerLevel getServer(){
                return packager.entomoWorld.server;
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

            public int radius;
            public int thickness;

            @Override
            public Nest.Offshoot Unpack(@Nullable Nest.Offshoot parent) {
                return Nest.Chamber.ConstructFromPackage(this, parent);
            }
        }
        public static class PackagedCorridor extends PackagedOffshoot{
            private PackagedCorridor(NestPackager packager, double x, double y, double z, boolean deadEnd, byte state, double x2, double y2, double z2) {
                super(packager, x, y, z, deadEnd, state);
                super.type = 2;
                X2 = x2; Y2 = y2; Z2 = z2;
            }
            public static PackagedCorridor PackageOffshoot(Nest.Corridor toPackage, NestPackager packager) {
                TunnelGenerator tunnel = (TunnelGenerator)toPackage.getGenerator();
                Vec3 start = tunnel.getStart();
                Vec3 end = tunnel.getEnd();

                return new PackagedCorridor(packager, start.x, start.y, start.z, toPackage.DeadEnd, toPackage.getOffshootState(), end.x, end.y, end.z);
            }

            public double X2;
            public double Y2;
            public double Z2;
            public int weight;
            public int thickness;
            @Override
            public Nest.Offshoot Unpack(Nest.Offshoot parent) {
                return Nest.Corridor.ConstructFromPackage(this, parent);
            }
        }
    }
}
