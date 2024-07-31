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

    public static final ResourceKey<DamageType> MYIATIC_BASIC = create("myiatic_basic");

    public static DamageSource myiatic_basic(LivingEntity entity){
        return damageSource(entity, MYIATIC_BASIC, entity);
    }
}
