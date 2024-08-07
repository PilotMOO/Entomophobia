package mod.pilot.entomophobia.entity.pheromones;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.PheromonesBase;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.worlddata.EntomoWorldManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public class PheromonePreyHuntEntity extends PheromonesEntityBase implements GeoAnimatable{
    public PheromonePreyHuntEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, (PheromonesBase)EntomoMobEffects.PREY.get(), (PheromonesBase)EntomoMobEffects.HUNT.get(),
                120, 24, 2000, 0, 4000);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }

    @Override
    public void tick() {
        super.tick();
        AABB nearby = getBoundingBox().inflate(64);
        if (level().getEntitiesOfClass(MyiaticBase.class, nearby).size() < 8){
            EntomoWorldManager.SpawnFromStorageWithRandomPos(EntomoEntities.MYIATIC_ZOMBIE.get(), position(), level(), 20);
        }
    }
}
