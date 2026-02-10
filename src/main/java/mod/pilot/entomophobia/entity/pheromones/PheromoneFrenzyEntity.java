package mod.pilot.entomophobia.entity.pheromones;

import mod.pilot.entomophobia.effects.EntomoMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PheromoneFrenzyEntity extends PheromonesEntityBase implements GeoAnimatable {
    public PheromoneFrenzyEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, null, EntomoMobEffects.FRENZY.get(),
                32, 0, 400, 0, 2500, 2);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
