package mod.pilot.entomophobia.entity.celestial;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CarrioniteEntity extends Entity implements GeoEntity {
    public CarrioniteEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //ToDo: Figure out how to make it only render for specific players
    @Override
    protected void defineSynchedData() {

    }
    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {

    }
    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {

    }
    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "IdleManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
