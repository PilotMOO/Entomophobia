package mod.pilot.entomophobia.worlddata;

import mod.pilot.entomophobia.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

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
}
