package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import mod.pilot.entomophobia.systems.EventStart.EventStart;
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
    public static void dirty(){
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
        EventStart.unpackFromData(tag);

        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putInt("myiatic_mobcap", MyiaticCount);
        tag.putBoolean("has_started", HasStarted);
        tag.putInt("world_age", WorldAge);
        tag.putString("myiatic_storage", MyiaticStorage);
        EventStart.packToData(tag);

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
    public void removeFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(getSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                splicedList.remove(S);
                break;
            }
        }
        MyiaticStorage = recompressStorage(splicedList);
        setDirty();
    }
    public EntityType<?> getFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(getSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                removeFromStorage(S);
                setDirty();
                return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(S));
            }
        }
        return null;
    }
    public String getStringFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(getSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                removeFromStorage(S);
                setDirty();
                return S;
            }
        }
        return null;
    }
    public String getPhantomStringFromStorage(String ID){
        ArrayList<String> splicedList = new ArrayList<>(Arrays.asList(getSpliced()));
        for (String S : splicedList){
            if (Objects.equals(S, ID)){
                return S;
            }
        }
        return null;
    }
    public int getQuantityOf(String ID){
        int amount = 0;
        for (String S : getSpliced()){
            if (Objects.equals(S, ID)){
                amount++;
            }
        }
        return amount;
    }
    public int getTotalInStorage(){
        return getSpliced().length - 1;
    }
    public String[] getStringFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(getQuantityOf(ID), amount);
        String[] toReturn = new String[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = getStringFromStorage(ID);
        }
        setDirty();
        return toReturn;
    }
    public EntityType<?>[] getFromStorageBetweenMax(String ID, int amount){
        int amountToReturn = Math.min(getQuantityOf(ID), amount);
        EntityType<?>[] toReturn = new EntityType[amountToReturn];
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = getFromStorage(ID);
        }
        setDirty();
        return toReturn;
    }
    public String[] getAnyStringFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(getTotalInStorage(), amount);
        String[] toReturn = new String[amountToReturn];
        String[] spliced = getSpliced();
        if (amountToReturn - 2 >= 0) System.arraycopy(spliced, 1, toReturn, 1, amountToReturn - 1 - 1);
        setDirty();
        return toReturn;
    }
    public EntityType<?>[] getAnyFromStorageBetweenMax(int amount){
        int amountToReturn = Math.min(getTotalInStorage(), amount);
        EntityType<?>[] toReturn = new EntityType<?>[amountToReturn];
        String[] strings = getAnyStringFromStorageBetweenMax(amountToReturn);
        for (int i = 0; i < amountToReturn; i++){
            toReturn[i] = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(strings[i]));
        }
        setDirty();
        return toReturn;
    }
    public String getFirstStringFromStorage(){
        if (getTotalInStorage() > 1){
            String[] spliced = getSpliced();
            String toReturn = spliced[1];
            removeFromStorage(toReturn);
            setDirty();
            return toReturn;
        }
        return null;
    }
    public EntityType<?> getFirstFromStorage(){
        if (getTotalInStorage() > 1){
            String[] spliced = getSpliced();
            EntityType<?> toReturn = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(spliced[1]));
            removeFromStorage(spliced[1]);
            setDirty();
            return toReturn;
        }
        return null;
    }
    public String getFirstPhantomFromStorage(){
        if (getTotalInStorage() > 1){
            return getSpliced()[1];
        }
        return null;
    }
    public void removeFirstFromStorage(){
        if (getTotalInStorage() > 1){
            removeFromStorage(getSpliced()[1]);
            setDirty();
        }
    }

    private String[] getSpliced(){
        return MyiaticStorage.split("/");
    }
    private String recompressStorage(ArrayList<String> splicedList) {
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
