package mod.pilot.entomophobia.data;

import mod.pilot.entomophobia.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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

    public static Vec3 vec3iToVec3(Vec3i position){
        return new Vec3(position.getX(), position.getY(), position.getZ());
    }
    public static Vec3i vec3ToVec3i(Vec3 position){
        return new Vec3i((int)position.x, (int)position.y, (int)position.z);
    }

    public static boolean isThisGlass(BlockState state){
        Block block = state.getBlock();
        return block instanceof GlassBlock || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock || state.is(Blocks.GLASS_PANE);
    }

    private static final long dayLength = 24000L;
    private static Minecraft mc;
    public static int getDaysElapsed(){
        if (mc == null) mc = Minecraft.getInstance();
        if (mc.level == null) return -1;
        return (int)(mc.level.getDayTime() / dayLength);
    }
    public static float getDayPercentage(){
        if (mc == null) mc = Minecraft.getInstance();
        if (mc.level == null) return -1f;
        return (float)(mc.level.getDayTime() % dayLength) / dayLength;
    }
}
