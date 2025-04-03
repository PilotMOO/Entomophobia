package mod.pilot.entomophobia.entity.celestial;

import com.google.common.collect.Lists;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Objects;

public class HiveHeartEntity extends MyiaticBase {
    public HiveHeartEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticPigEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100d)
                .add(Attributes.ARMOR, 5)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    @Override
    protected void registerGoals() {registerBasicGoals();}

    @Override
    protected void registerBasicGoals() {
        //Goals are empty rn, worry about it later
    }

    @Override
    public boolean canSwarm() {
        return false;
    }

    @Override
    public void checkDespawn() {return;}

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    //Artery rendering
    private ArrayList<Vec3> _arteryHooks;
    public ArrayList<Vec3> getOrCreateArteryHooks(){
        return Objects.requireNonNullElse(_arteryHooks, (_arteryHooks = createArteries()));
    }
    public boolean hasArteries(){return _arteryHooks != null && !_arteryHooks.isEmpty();}
    public ArrayList<Vec3> createArteries(){
        return createArteries(random.nextInt(6, 9),32, 10);
    }
    //This just returns an arraylist of positions, you need to actually assign it for it to work...
    public ArrayList<Vec3> createArteries(int count, int maxRange, int maxTries){
        //ToDo: Shoot out raycasts to locate blocks around the entity in a set radius for rendering arteries
        //Rn just creates one vector like 10 blocks above the heart for testing reasons
        return Lists.newArrayList(position().add(0, 10, 0));
    }

    //Animation handling
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "HeartManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("beat"))));
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
