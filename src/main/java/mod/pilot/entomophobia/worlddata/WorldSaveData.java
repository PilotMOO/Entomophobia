package mod.pilot.entomophobia.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_world_data";

    public WorldSaveData(){
        super();
    }
    public static void StartupData(ServerLevel level){
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        data.setDirty();
    }

    public static WorldSaveData getDataLocation(ServerLevel level){
        return level.getDataStorage().get(WorldSaveData::load, NAME);
    }
    public static WorldSaveData load(CompoundTag tag){
        WorldSaveData data = new WorldSaveData();
        if (tag.contains("myiatic_mobcap",99)){
            data.MyiaticCount = tag.getInt("myiatic_mobcap");
        }
        if (tag.contains("myiatic_storage")){
            data.MyiaticStorage = tag.getString("myiatic_storage");
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putInt("myiatic_mobcap",MyiaticCount);
        tag.putString("myiatic_storage",MyiaticStorage);
        return tag;
    }

    private int MyiaticCount = 0;

    public static void AddToMyiaticCount(ServerLevel level, int count){
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        data.MyiaticCount += count;
        data.setDirty();
    }
    public static void AddToMyiaticCount(ServerLevel level){
        AddToMyiaticCount(level, 1);
    }
    public static void RemoveFromMyiaticCount(ServerLevel level, int count){
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        data.MyiaticCount -= count;
        data.setDirty();
    }
    public static void RemoveFromMyiaticCount(ServerLevel level){
        RemoveFromMyiaticCount(level, 1);
    }
    public static int GetMyiaticCount(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME).MyiaticCount;
    }
    public int GetMyiaticCount(){
        return MyiaticCount;
    }

    private String MyiaticStorage;
    public static void AddToStorage(ServerLevel level, String ID){
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        data.MyiaticStorage += ID + ":";
        data.setDirty();
    }
    public static void RemoveFromStorage(ServerLevel level, String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced(level)));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                splicedList.remove(S);
                break;
            }
        }
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        data.MyiaticStorage = RecompressStorage(splicedList);
    }
    public static String GetFromStorage(ServerLevel level, String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced(level)));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                splicedList.remove(S);
                return S;
            }
        }
        return null;
    }
    public static String GetPhantomFromStorage(ServerLevel level, String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(GetSpliced(level)));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                return S;
            }
        }
        return null;
    }
    public static int GetQuantityOf(ServerLevel level, String ID){
        int amount = 0;
        for (String S : GetSpliced(level)){
            if (Objects.equals(S, ID)){
                amount++;
            }
        }
        return amount;
    }
    public static String[] GetFromStorageBetweenMax(ServerLevel level, String ID, int amount){
        int amountToReturn = Math.min(GetQuantityOf(level, ID), amount);
        String[] toReturn = new String[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = GetFromStorage(level, ID);
        }
        return toReturn;
    }

    private static String[] GetSpliced(ServerLevel level){
        WorldSaveData data = level.getDataStorage().computeIfAbsent(WorldSaveData::load, WorldSaveData::new, NAME);
        return data.MyiaticStorage.split(":");
    }
    private static String RecompressStorage(ArrayList<String> splicedList) {
        StringBuilder newString = new StringBuilder();
        for (String S : splicedList){
            newString.append(S).append(":");
        }
        return newString.toString();
    }
}
