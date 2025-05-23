package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.AI.AttackWithAnimationGoal;
import mod.pilot.entomophobia.entity.AI.LatchOntoTargetGoal;
import mod.pilot.entomophobia.entity.pathfinding.WallClimbingNestNavigation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyiaticSpiderEntity extends MyiaticBase{
    public MyiaticSpiderEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setReach(-0.5f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 1, event ->
        {
            if (getAIState() == state.other.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("attached"));
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
        return MyiaticZombieEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.ARMOR, 3)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }

    //Overridden methods
    @Override
    protected void registerBasicGoals() {
        super.registerBasicGoals();
        this.targetSelector.addGoal(1, new AttackWithAnimationGoal(this, 1.0D, true, 10, 15, 20));
        this.targetSelector.addGoal(2, new LatchOntoTargetGoal(this, 10, 60, 0, 15, 20, 1.5D, 0.5D));
    }
    @Override
    protected int stateManager() {
        if (getAIState() == state.other.ordinal()){
            return state.other.ordinal();
        }
        else if (isChasing() && getAIState() != state.attacking.ordinal()){
            return state.running.ordinal();
        }
        else if (isMoving() && getAIState() != state.attacking.ordinal()){
            return state.walking.ordinal();
        }
        else if (getAIState() != state.attacking.ordinal()) {
            return state.idle.ordinal();
        }
        return getAIState();
    }

    @Override
    public boolean doHurtTarget(@Nullable Entity pEntity) {
        if (getAIState() != state.other.ordinal()){
            if (pEntity instanceof LivingEntity){
                ((LivingEntity) pEntity).addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            }
            return super.doHurtTarget(pEntity);
        }
        else{
            if (pEntity != null){
                float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                if (pEntity instanceof LivingEntity) {
                    f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)pEntity).getMobType());
                }

                int i = EnchantmentHelper.getFireAspect(this);
                if (i > 0) {
                    pEntity.setSecondsOnFire(i * 4);
                }

                pEntity.invulnerableTime = 0;
                boolean flag = pEntity.hurt(EntomoDamageTypes.latch(this), f);
                if (flag) {
                    if (pEntity instanceof LivingEntity LEntity){
                        LEntity.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 100));
                        LEntity.addEffect(new MobEffectInstance(EntomoMobEffects.NEUROINTOXICATION.get(), 100));
                        int duration = 100;
                        int amp = 0;
                        if (LEntity.hasEffect(MobEffects.POISON)){
                            MobEffectInstance poisonInstance = LEntity.getEffect(MobEffects.POISON);
                            duration += poisonInstance.getDuration();
                            amp = Math.max(amp, poisonInstance.getAmplifier());
                            LEntity.removeEffect(MobEffects.POISON);
                        }
                        LEntity.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amp));
                    }
                    this.doEnchantDamageEffects(this, pEntity);
                    this.setLastHurtMob(pEntity);
                }

                return flag;
            }
            return false;
        }
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (getAIState() == state.other.ordinal()){
            if (getHealth() < getAttributeValue(Attributes.MAX_HEALTH) * 0.25){
                setTarget(null);
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean onClimbable() {
        return horizontalCollision
                && (getTarget() == null || getTarget().position().y > position().y)
                && getAIState() != state.other.ordinal();
    }
    @Override
    public void makeStuckInBlock(BlockState pState, @NotNull Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }
    }

    @Override
    public int getExperienceReward() {
        return 7;
    }
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.SPIDER_HURT;
    }
    /**/
}
