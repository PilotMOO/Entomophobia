package mod.pilot.entomophobia.entity.celestial;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import javax.swing.text.Style;
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
    private ArrayList<Artery> _arteryHooks;
    public ArrayList<Artery> getOrCreateArteryHooks(){
        if (!hasArteries()) _arteryHooks = createArteries();
        return _arteryHooks;
    }
    public boolean hasArteries(){return _arteryHooks != null && !_arteryHooks.isEmpty();}
    public ArrayList<Artery> createArteries(){
        return createArteries(random.nextInt(6, 9),32, 10);
    }
    //This just returns an arraylist of positions, you need to actually assign it for it to work...
    public ArrayList<Artery> createArteries(int count, int maxRange, int maxTries){
        ArrayList<Artery> toReturn = new ArrayList<>();
        for (; count > 0; --count){
            Vec3 pos;
            Vec3 direction;
            int cycle = maxTries;
            boolean valid = false;
            do{
                pos = position();
                direction = new Vec3(0, 1, 0)
                        .xRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)))
                        .yRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)))
                        .zRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)));
                int i = maxRange;
                boolean flag = true;
                BlockState bState;
                while (--i >= 0 && flag){
                    pos = pos.add(direction);
                    bState = level().getBlockState(BlockPos.containing(pos));
                    flag = bState.canBeReplaced();
                }
                if (!flag){
                    valid = true;
                    break;
                }
            }while(--cycle >= 0);
            if (valid){
                Pair<Float, Float> pair = randomArterySizePair();
                boolean flip = random.nextBoolean();
                float a = flip ? pair.getB() : pair.getA();
                float b = flip ? pair.getA() : pair.getB();
                toReturn.add(new Artery(_blockPosCenterOf(pos), a, b));
            }
        }
        return toReturn;
    }

    public Pair<Float, Float> randomArterySizePair(){
        float a, b;
        switch (random.nextInt(6)){
            case 0 ->{
                a = 0.3f; b = 0.75f;
            }
            case 1 ->{
                a = 0.1f; b = 0.3f;
            }
            case 2 ->{
                a = 0.25f; b = 0.4f;
            }
            case 3 ->{
                a = 0.35f; b = 0.5f;
            }
            case 4 ->{
                a = 0.25f; b = 0.5f;
            }
            case 5 ->{
                a = 0.15f; b = 0.4f;
            }
            default ->{
                a = 0.25f; b = 0.25f;
            }
        }
        return new Pair<>(a, b);
    }

    /**
     * Floors the supplied Vec3 argument, adds 0.5 to the Y, then returns the new Vec3--
     *  rounding all of its positions to an integer before having 0.5 added to all values.
     *  Mimics the same result as BlockPos.containing(Vec3).getCenter(), while being less performance-hungry.
     * @param vec The Vec3 to mimic the blockCenter of
     * @return A Vec3 identical to if BlockPos.containing(Vec3).getCenter() was called on it instead.
     */
    private Vec3 _blockPosCenterOf(Vec3 vec){
        double x, y, z;
        x = Math.floor(vec.x) + 0.5d;
        y = Math.floor(vec.y) + 0.5d;
        z = Math.floor(vec.z) + 0.5d;
        return new Vec3(x, y, z);
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

    public record Artery(Vec3 position, float startThickness, float endThickness){}
}
