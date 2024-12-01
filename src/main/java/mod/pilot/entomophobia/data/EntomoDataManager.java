package mod.pilot.entomophobia.data;

import mod.pilot.entomophobia.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class EntomoDataManager {
    public static EntityType<?> getConvertedFor(String ID){
        for (String configged : Config.SERVER.myiatic_conversion_list.get()){
            String[] split = configged.split(">");
            if (split[0].equals(ID)){
                return getEntityFromString(split[1]);
            }
        }
        return null;
    }
    public static EntityType<?> getConvertedFor(Entity target){
        return getConvertedFor(target.getEncodeId());
    }

    public static EntityType<?> getEntityFromString(String ID){
        return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(ID));
    }

    public static Vec3 getDirectionFromAToB(Entity target, Entity parent) {
        return parent.position().subtract(target.position()).normalize();
    }
    public static Vec3 getDirectionFromAToB(Vec3 target, Entity parent) {
        return parent.position().subtract(target).normalize();
    }
    public static Vec3 getDirectionFromAToB(Entity target, Vec3 parent) {
        return parent.subtract(target.position()).normalize();
    }
    public static Vec3 getDirectionFromAToB(Vec3 target, Vec3 parent) {
        return parent.subtract(target).normalize();
    }
    public static Vec3 getDirectionToAFromB(Entity target, Entity parent) {
        return target.position().subtract(parent.position()).normalize();
    }
    public static Vec3 getDirectionToAFromB(Vec3 target, Entity parent) {
        return target.subtract(parent.position()).normalize();
    }
    public static Vec3 getDirectionToAFromB(Entity target, Vec3 parent) {
        return target.position().subtract(parent).normalize();
    }
    public static Vec3 getDirectionToAFromB(Vec3 target, Vec3 parent) {
        return target.subtract(parent).normalize();
    }

    public static boolean isThisGlass(BlockState state){
        Block block = state.getBlock();
        return block instanceof GlassBlock || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock || state.is(Blocks.GLASS_PANE);
    }
}
