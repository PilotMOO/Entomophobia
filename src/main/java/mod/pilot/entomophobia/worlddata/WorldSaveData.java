package mod.pilot.entomophobia.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
}
