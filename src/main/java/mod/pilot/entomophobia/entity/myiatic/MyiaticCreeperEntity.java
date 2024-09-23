package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.entity.AI.AttackWithAnimationGoal;
import mod.pilot.entomophobia.entity.AI.PheromoneExplodeGoal;
import mod.pilot.entomophobia.entity.EntomoEntities;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MyiaticCreeperEntity extends MyiaticBase{
    public MyiaticCreeperEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setReach(-0.25f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 1, event ->
        {
            if (getAIState() == state.other.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("explode"));
            }
            else if (getAIState() == state.attacking.ordinal()){
                return event.setAndContinue(RawAnimation.begin().thenLoop("strike"));
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
        return MyiaticCreeperEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }


    //Goals
    @Override
    protected void registerBasicGoals() {
        super.registerBasicGoals();
        this.targetSelector.addGoal(1, new AttackWithAnimationGoal(this, 1.0D, true, 20,  15, 20));
        this.targetSelector.addGoal(1, new PheromoneExplodeGoal(this, 48, 32, 10, 30));
    }
    /**/

    //Unique Methods
    public boolean WantsToExplode(int pheromoneSearchRange, int myiaticSearchRange, int targetSearchRange){
        if (!isThereAPheromoneOfTypeXNearby(EntomoEntities.FRENZY.get(), pheromoneSearchRange)){
            double healthPercent = (getHealth() / getAttributeValue(Attributes.MAX_HEALTH)) * 100;
            int NearbyMyiaticsCount = getNearbyMyiatics(myiaticSearchRange).size();
            int NearbyTargetsCount = getValidTargets(targetSearchRange).size();
            if (healthPercent == 100){
                return NearbyMyiaticsCount > 6 && NearbyTargetsCount > 8 || getTarget() instanceof Player && NearbyMyiaticsCount > 6;
            }
            else if (healthPercent > 75){
                return NearbyMyiaticsCount > 4 && NearbyTargetsCount > 4 || getTarget() instanceof Player && NearbyMyiaticsCount > 4;
            }
            else if (healthPercent > 50){
                return NearbyMyiaticsCount > 3 && NearbyTargetsCount > 1 || getTarget() instanceof Player;
            }
            else if (healthPercent < 15){
                for (MyiaticBase M : getNearbyMyiatics(myiaticSearchRange)){
                    if (M.getTarget() != null){
                        continue;
                    }
                    return false;
                }
                return NearbyMyiaticsCount > 0 || NearbyTargetsCount > 0 || getTarget() instanceof Player;
            }
        }
        return false;
    }
    /**/

    //Overridden methods
    @Override
    protected int StateManager() {
        if (getAIState() == state.other.ordinal()){
            return state.other.ordinal();
        }
        else if (getAIState() == state.attacking.ordinal()){
            return state.attacking.ordinal();
        }
        else if (isMoving()){
            return state.walking.ordinal();
        }
        else {
            return state.idle.ordinal();
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean superFlag = true;
        Entity sourceEntity = pSource.getEntity();
        if (sourceEntity instanceof LivingEntity LEntity){
            if (TestValidEntity(LEntity)){
                for (MyiaticBase M : getNearbyMyiatics()){
                    if (M.getTarget() == null){
                        M.setTarget(LEntity);
                    }
                }
                setTarget(LEntity);
            }
            if (sourceEntity instanceof MyiaticBase && sourceEntity != this){
                superFlag = false;
            }
        }
        if (superFlag){
            return super.hurt(pSource, pAmount);
        }
        return false;
    }
    @Override
    public int getExperienceReward() {
        return 30;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.CREEPER_HURT;
    }
    /**/
}
