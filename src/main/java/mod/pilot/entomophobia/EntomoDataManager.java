package mod.pilot.entomophobia;

import mod.pilot.entomophobia.entity.EntomoEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

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
