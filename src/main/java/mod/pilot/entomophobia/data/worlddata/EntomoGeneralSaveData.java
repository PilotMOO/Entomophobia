package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntomoGeneralSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_misc_world_data";

    public EntomoGeneralSaveData(){
        super();
        MyiaticStorage = "dummy/";
        server = EntomoForgeEvents.getServer();
    }
    public static void setActiveData(ServerLevel server){
        Entomophobia.activeData = server.getDataStorage().computeIfAbsent(EntomoGeneralSaveData::load, EntomoGeneralSaveData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull EntomoGeneralSaveData activeData(){
        return Entomophobia.activeData;
    }
    public static void Dirty(){
        Entomophobia.activeData.setDirty();
    }
    public static EntomoGeneralSaveData load(CompoundTag tag){
        EntomoGeneralSaveData data = new EntomoGeneralSaveData();
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

        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putInt("myiatic_mobcap", MyiaticCount);
        tag.putBoolean("has_started", HasStarted);
        tag.putInt("world_age", WorldAge);
        tag.putString("myiatic_storage", MyiaticStorage);

        return tag;
    }

    private int MyiaticCount = 0;

    public void addToMyiaticCount(int count){
        MyiaticCount += count;
        setDirty();
    }
    public void addToMyiaticCount(){
        addToMyiaticCount(1);
    }
    public void removeFromMyiaticCount(int count){
        MyiaticCount -= count;
        setDirty();
    }
    public void removeFromMyiaticCount(){
        removeFromMyiaticCount(1);
    }
    public static int getMyiaticCount(){
        return activeData().MyiaticCount;
    }

    private boolean HasStarted = false;

    public static boolean hasStarted(){
        return activeData().HasStarted;
    }
    public void setHasStarted(boolean flag){
        HasStarted = flag;
    }

    private int WorldAge = 0;
    public static int getWorldAge(){
        return activeData().WorldAge;
    }
    public void setWorldAge(int age){
        WorldAge = age;
        setDirty();
    }
    public void ageWorldBy(int age){
        setWorldAge(getWorldAge() + age);
    }
    public void ageWorld(){
        ageWorldBy(1);
    }

    private String MyiaticStorage;
    public void addToStorage(String ID){
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
        setDirty();
    }
    public EntityType<?> GetFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                RemoveFromStorage(S);
                setDirty();
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
                setDirty();
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
    public int getTotalInStorage(){
        return GetSpliced().length - 1;
    }
    public String[] GetStringFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(GetQuantityOf(ID), amount);
        String[] toReturn = new String[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = GetStringFromStorage(ID);
        }
        setDirty();
        return toReturn;
    }
    public EntityType<?>[] GetFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(GetQuantityOf(ID), amount);
        EntityType<?>[] toReturn = new EntityType[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = GetFromStorage(ID);
        }
        setDirty();
        return toReturn;
    }
    public String[] GetAnyStringFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(getTotalInStorage(), amount);
        String[] toReturn = new String[amountToReturn];
        String[] spliced = GetSpliced();
        if (amountToReturn - 2 >= 0) System.arraycopy(spliced, 1, toReturn, 1, amountToReturn - 1 - 1);
        setDirty();
        return toReturn;
    }
    public EntityType<?>[] GetAnyFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(getTotalInStorage(), amount);
        EntityType<?>[] toReturn = new EntityType<?>[amountToReturn];
        String[] strings = GetAnyStringFromStorageBetweenMax(amountToReturn);
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(strings[i]));
        }
        setDirty();
        return toReturn;
    }
    public String GetFirstStringFromStorage(){
        if (getTotalInStorage() > 1){
            String[] spliced = GetSpliced();
            String toReturn = spliced[1];
            RemoveFromStorage(toReturn);
            setDirty();
            return toReturn;
        }
        return null;
    }
    public EntityType<?> GetFirstFromStorage(){
        if (getTotalInStorage() > 1){
            String[] spliced = GetSpliced();
            EntityType<?> toReturn = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(spliced[1]));
            RemoveFromStorage(spliced[1]);
            setDirty();
            return toReturn;
        }
        return null;
    }
    public String GetFirstPhantomFromStorage(){
        if (getTotalInStorage() > 1){
            return GetSpliced()[1];
        }
        return null;
    }
    public void RemoveFirstFromStorage(){
        if (getTotalInStorage() > 1){
            RemoveFromStorage(GetSpliced()[1]);
            setDirty();
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

    private ServerLevel server;
    public ServerLevel getServer(){
        return server;
    }
    public void setServer(ServerLevel server){
        this.server = server;
    }
}
