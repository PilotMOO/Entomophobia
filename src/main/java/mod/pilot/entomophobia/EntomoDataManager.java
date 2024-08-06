package mod.pilot.entomophobia;

import com.mojang.datafixers.util.Pair;
import mod.pilot.entomophobia.entity.EntomoEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntomoDataManager {
    public static EntityType<?> GetConvertedFor(String ID){
        for (String configged : Config.SERVER.myiatic_conversion_list.get()){
            String[] split = configged.split(">");
            if (split[0].equals(ID)){
                return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(split[1]));
            }
        }
        return null;
    }
    public static EntityType<?> GetConvertedFor(Entity target){
        return GetConvertedFor(target.getEncodeId());
    }

    private static final HashMap<String, Integer> MyiaticStorage = new HashMap<>();
    private static HashMap<String, Integer> Duplicate(){
        return new HashMap<>(Map.copyOf(MyiaticStorage));
    }
    public static void AddThisToStorage(String ID){
        if (Duplicate().containsKey(ID)){
            MyiaticStorage.replace(ID, MyiaticStorage.get(ID) + 1);
        }
        else{
            MyiaticStorage.put(ID, 1);
        }
    }
    public static boolean RemoveAmountOfThisStorage(int count, String ID){
        HashMap<String, Integer> duplicate = Duplicate();
        if (duplicate.containsKey(ID) && duplicate.get(ID) >= count){
            MyiaticStorage.replace(ID, MyiaticStorage.get(ID) - count);
            return true;
        }
        else{
            return false;
        }
    }
}
