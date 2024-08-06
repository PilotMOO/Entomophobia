package mod.pilot.entomophobia.damagetypes;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class EntomoDamageTypes {
    public static ResourceKey<DamageType> create(String id){
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Entomophobia.MOD_ID, id));
    }

    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey));
    }
    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey, @Nullable Entity entity2){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey), entity2);
    }

    public static final ResourceKey<DamageType> MYIATIC_BASIC1 = create("myiatic_basic1");
    public static final ResourceKey<DamageType> MYIATIC_BASIC2 = create("myiatic_basic2");
    public static final ResourceKey<DamageType> MYIATIC_BASIC3 = create("myiatic_basic3");


    public static final ResourceKey<DamageType> MYAISIS_1 = create("myiasis_1");
    public static final ResourceKey<DamageType> MYAISIS_2 = create("myiasis_2");
    public static final ResourceKey<DamageType> MYAISIS_3 = create("myiasis_3");


    public static final ResourceKey<DamageType> MYIATIC_EXPLODE = create("myiatic_explode");

    public static DamageSource myiatic_basic(LivingEntity entity){
        switch (entity.getRandom().nextIntBetweenInclusive(1, 3)){
            default -> {
                return damageSource(entity, MYIATIC_BASIC1, entity);
            }
            case 2 -> {
                return damageSource(entity, MYIATIC_BASIC2, entity);
            }
            case 3 -> {
                return damageSource(entity, MYIATIC_BASIC3, entity);
            }
        }
    }
    public static DamageSource myiasis(LivingEntity entity){
        switch (entity.getRandom().nextIntBetweenInclusive(1, 3)){
            default -> {
                return damageSource(entity, MYAISIS_1);
            }
            case 2 -> {
                return damageSource(entity, MYAISIS_2);
            }
            case 3 -> {
                return damageSource(entity, MYAISIS_3);
            }
        }
    }
    public static DamageSource myiatic_explode(LivingEntity entity){
        return damageSource(entity, MYIATIC_EXPLODE, entity);
    }
}
