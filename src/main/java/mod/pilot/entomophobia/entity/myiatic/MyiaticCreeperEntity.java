package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.EntomoWorldManager;
import mod.pilot.entomophobia.entity.AI.AttackWithAnimationGoal;
import mod.pilot.entomophobia.entity.AI.Flight.FlyToHostileTargetGoal;
import mod.pilot.entomophobia.entity.AI.Flight.GlideDownToFoesGoal;
import mod.pilot.entomophobia.entity.AI.Flight.PleaseDontBreakMyLegsGoal;
import mod.pilot.entomophobia.entity.AI.PheromoneExplodeGoal;
import mod.pilot.entomophobia.entity.EntomoEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MyiaticCreeperEntity extends MyiaticBase{
    public MyiaticCreeperEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setReach(-0.25f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 2, event ->
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
                .add(Attributes.ATTACK_DAMAGE, 3D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }


    //Goals
    @Override
    protected void registerBasicGoals() {
        super.registerBasicGoals();
        this.targetSelector.addGoal(1, new AttackWithAnimationGoal(this, 1.0D, true, 15, 20));
        this.targetSelector.addGoal(1, new PheromoneExplodeGoal(this, 48, 32, 10, 30));
    }
    /**/

    //Unique Methods
    public boolean WantsToExplode(int pheromoneSearchRange, int myiaticSearchRange, int targetSearchRange){
        if (!EntomoWorldManager.IsThereAPheromoneOfTypeXNearby(EntomoEntities.FRENZY.get(), pheromoneSearchRange, this)){
            double healthPercent = (getHealth() / getAttributeValue(Attributes.MAX_HEALTH)) * 100;
            int NearbyMyiatics = EntomoWorldManager.GetNearbyMyiatics(this, myiaticSearchRange).size();
            int NearbyTargets = EntomoWorldManager.GetValidTargetsFor(this, targetSearchRange).size();
            if (healthPercent == 100){
                return NearbyMyiatics > 6 && NearbyTargets > 4;
            }
            else if (healthPercent > 75){
                return NearbyMyiatics > 4 && NearbyTargets > 2;
            }
            else if (healthPercent > 50){
                return NearbyMyiatics > 3 && NearbyTargets > 1;
            }
            else if (healthPercent < 15){
                for (MyiaticBase M : EntomoWorldManager.GetNearbyMyiatics(this, myiaticSearchRange)){
                    if (M.getTarget() != null){
                        continue;
                    }
                    return false;
                }
                return NearbyMyiatics > 0 || NearbyTargets > 0;
            }
        }
        return false;
    }
    /**/

    //Overridden inherited methods
    @Override
    protected int StateManager() {
        if (getAIState() == state.other.ordinal()){
            return getAIState();
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
    /**/
}
