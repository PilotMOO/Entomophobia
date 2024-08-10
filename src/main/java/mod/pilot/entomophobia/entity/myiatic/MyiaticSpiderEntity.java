package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.entity.AI.AttackWithAnimationGoal;
import mod.pilot.entomophobia.entity.AI.PounceOnTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MyiaticSpiderEntity extends MyiaticBase{
    public MyiaticSpiderEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setReach(-0.3f);
    }

    public enum PounceStates{
        Disabled,
        NotPouncing,
        GettingReady,
        Midair,
        Attached
    }
    public static final EntityDataAccessor<Integer> PounceState = SynchedEntityData.defineId(MyiaticSpiderEntity.class, EntityDataSerializers.INT);
    public int getPounceState(){return entityData.get(PounceState);}
    public void setPounceState(Integer count) {entityData.set(PounceState, count);}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PounceState", entityData.get(PounceState));
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(PounceState, tag.getInt("PounceState"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PounceState, 1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 5, event ->
        {
            if (getAIState() == state.other.ordinal()){
                switch (getPounceState()){
                    case 1 -> {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                    }
                    case 2 -> {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("pounce"));
                    }
                    case 3 -> {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("flinging"));
                    }
                    case 4 -> {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("attached"));
                    }
                }
                return event.setAndContinue(RawAnimation.begin().thenLoop("pounce"));
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

    @Override
    protected void registerBasicGoals() {
        super.registerBasicGoals();
        this.targetSelector.addGoal(1, new AttackWithAnimationGoal(this, 1.0D, true, 10, 15, 20));
        this.targetSelector.addGoal(2, new PounceOnTargetGoal(this, 10, 60, 0, 15, 20, 30, 1.5D));
    }

    @Override
    protected int StateManager() {
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }
}
