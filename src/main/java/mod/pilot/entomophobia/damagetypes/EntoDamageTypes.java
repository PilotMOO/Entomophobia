package mod.pilot.entomophobia.damagetypes;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EntoDamageTypes {
    public static ResourceKey<DamageType> create(String id){
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Entomophobia.MOD_ID, id));
    }

    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey));
    }
    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey, @Nullable Entity entity2){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey), entity2);
    }

    public static void buildMultitranslatables(Level level){
        MYIATIC_MELEE_SOURCE = new MultitranslatableDamageSource(level, MYIATIC_MELEE,
                "death.attack.myiatic_basic_damage1",
                "death.attack.myiatic_basic_damage2",
                "death.attack.myiatic_basic_damage3");
        LATCH_SOURCE = new MultitranslatableDamageSource(level, LATCH,
                "death.attack.latch_damage1",
                "death.attack.latch_damage2",
                "death.attack.latch_damage3");
        MYIASIS_SOURCE = new MultitranslatableDamageSource(level, MYIASIS,
                "death.attack.myiasis_damage1",
                "death.attack.myiasis_damage2",
                "death.attack.myiasis_damage3");
        NEURO_SOURCE = new MultitranslatableDamageSource(level, NEURO,
                "death.attack.neuro_damage1",
                "death.attack.neuro_damage2",
                "death.attack.neuro_damage3");
        OVERSTIM_SOURCE = new MultitranslatableDamageSource(level, OVERSTIM,
                "death.attack.overstimulation1",
                "death.attack.overstimulation2",
                "death.attack.overstimulation3");

    }

    public static final ResourceKey<DamageType> MYIATIC_MELEE = create("myiatic_basic1");
    public static MultitranslatableDamageSource MYIATIC_MELEE_SOURCE;

    public static final ResourceKey<DamageType> LATCH = create("latch_1");
    public static MultitranslatableDamageSource LATCH_SOURCE;


    public static final ResourceKey<DamageType> MYIASIS = create("myiasis_1");
    public static MultitranslatableDamageSource MYIASIS_SOURCE;

    public static final ResourceKey<DamageType> NEURO = create("neuro_1");
    public static MultitranslatableDamageSource NEURO_SOURCE;

    public static final ResourceKey<DamageType> OVERSTIM = create("overstimulation1");
    public static MultitranslatableDamageSource OVERSTIM_SOURCE;

    public static final ResourceKey<DamageType> MYIATIC_EXPLODE = create("myiatic_explode");
    public static final ResourceKey<DamageType> LEAD_POISONING = create("lead");

    public static DamageSource myiatic_basic(LivingEntity entity){
        return MYIATIC_MELEE_SOURCE.instance(entity, entity);
    }
    public static DamageSource latch(LivingEntity entity){
        return LATCH_SOURCE.instance(entity, entity);
    }
    public static DamageSource myiatic_explode(LivingEntity entity){
        return damageSource(entity, MYIATIC_EXPLODE, entity);
    }


    public static DamageSource myiasis(LivingEntity entity){
        return MYIASIS_SOURCE.instance(entity, entity);
    }
    public static DamageSource neuro(LivingEntity entity){
        return NEURO_SOURCE.instance(entity, entity);
    }
    public static DamageSource overstimulation(LivingEntity entity){
        return OVERSTIM_SOURCE.instance(entity, entity);
    }
    public static DamageSource leadPoisoning(LivingEntity entity){
        return damageSource(entity, LEAD_POISONING);
    }
}
