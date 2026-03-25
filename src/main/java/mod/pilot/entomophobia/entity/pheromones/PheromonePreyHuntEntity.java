package mod.pilot.entomophobia.entity.pheromones;

import mod.pilot.entomophobia.effects.EntoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.data.EntoWorldManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PheromonePreyHuntEntity extends PheromonesEntityBase implements GeoAnimatable {
    public PheromonePreyHuntEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, EntoMobEffects.PREY.get(), EntoMobEffects.HUNT.get(),
                120, 24, 2000, 0, 4000, 1);
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

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            AABB nearby = getBoundingBox().inflate(64);
            if (level().getEntitiesOfClass(MyiaticBase.class, nearby).size() < 8) {
                for (int i = 0; i <= 8 - level().getEntitiesOfClass(MyiaticBase.class, nearby).size(); i++) {
                    EntoWorldManager.SpawnAnythingFromStorageWithRandomPos(position(), server, 20);
                }
            }
        }
    }
}
