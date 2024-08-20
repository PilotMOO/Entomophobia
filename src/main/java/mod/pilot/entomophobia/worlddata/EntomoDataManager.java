package mod.pilot.entomophobia.worlddata;

import mod.pilot.entomophobia.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
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

    public static Vec3 GetDirectionFromAToB(Entity target, Entity parent) {
        return parent.position().subtract(target.position()).normalize();
    }
    public static Vec3 GetDirectionFromAToB(Vec3 target, Entity parent) {
        return parent.position().subtract(target).normalize();
    }
    public static Vec3 GetDirectionFromAToB(Entity target, Vec3 parent) {
        return parent.subtract(target.position()).normalize();
    }
    public static Vec3 GetDirectionFromAToB(Vec3 target, Vec3 parent) {
        return parent.subtract(target).normalize();
    }
    public static Vec3 GetDirectionToAFromB(Entity target, Entity parent) {
        return target.position().subtract(parent.position()).normalize();
    }
    public static Vec3 GetDirectionToAFromB(Vec3 target, Entity parent) {
        return target.subtract(parent.position()).normalize();
    }
    public static Vec3 GetDirectionToAFromB(Entity target, Vec3 parent) {
        return target.position().subtract(parent).normalize();
    }
    public static Vec3 GetDirectionToAFromB(Vec3 target, Vec3 parent) {
        return target.subtract(parent).normalize();
    }
}
