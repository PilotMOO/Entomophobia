package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MyiaticZombieEntity extends MyiaticBase {
    public MyiaticZombieEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setReach(2f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 2, event ->
        {
            if (getAIState() == state.flying.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("flight"));
            }
            else if (getAIState() == state.attacking.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("strike"));
            }
            else if (getAIState() == state.running.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
            }
            else if (getAIState() == state.walking.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            else{
                return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
        }));
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticBase.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 6D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AttackWithAnimationGoal(this, 1.0D, false, 10, 15));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>
                (this, LivingEntity.class,  true, livingEntity -> { return livingEntity instanceof Player;}));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>
                (this, LivingEntity.class,  true,livingEntity -> { return !Config.SERVER.blacklisted_targets.get().contains(livingEntity.getEncodeId()) && !(livingEntity instanceof AbstractFish) && !(livingEntity instanceof MyiaticBase) && !(livingEntity instanceof PheromonesEntityBase);}));
    }
}
