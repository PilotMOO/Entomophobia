package mod.pilot.entomophobia.entity.pheromones;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class PheromonePreyHuntEntity extends PheromonesEntityBase implements GeoAnimatable{
    public PheromonePreyHuntEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, EntomoMobEffects.PREY.get(), EntomoMobEffects.HUNT.get(),
                20, 20, 2000, 0, 4000);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticBase.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1D);
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
}
