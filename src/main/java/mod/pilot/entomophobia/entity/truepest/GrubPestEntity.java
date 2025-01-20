package mod.pilot.entomophobia.entity.truepest;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.entity.AI.HomophobicRandomStrollGoal;
import mod.pilot.entomophobia.entity.AI.PreyPriorityNearestAttackable;
import mod.pilot.entomophobia.entity.myiatic.MyiaticZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class GrubPestEntity extends PestBase {
    public GrubPestEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticZombieEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.ARMOR, 3)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 6D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }

    @Override
    protected void registerBasicGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PreyPriorityNearestAttackable(this, true));
        this.goalSelector.addGoal(3, new HomophobicRandomStrollGoal(this, 0.75D, 40, 48, 16));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
    }

    private boolean moving(){
        return getDeltaMovement().x != 0 || getDeltaMovement().z != 0;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "MovementManager", 1, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop(moving() ? "walk" : "idle"))));
    }
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
