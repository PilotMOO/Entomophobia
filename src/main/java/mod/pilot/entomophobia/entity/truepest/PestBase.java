package mod.pilot.entomophobia.entity.truepest;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class PestBase extends MyiaticBase {
    public static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PestBase.class, EntityDataSerializers.INT);
    public int getAge(){return entityData.get(AGE);}
    public void setAge(int age) {entityData.set(AGE, age);}
    public void AgeBy(int age) {setAge(getAge() + age);}
    public void AgeByOne() {AgeBy(1);}
    public static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(PestBase.class, EntityDataSerializers.INT);
    public int getMaxAge(){return entityData.get(MAX_AGE);}
    public void setMaxAge(int age) {entityData.set(MAX_AGE, age);}
    public boolean amITooOld(){
        return getAge() > getMaxAge() + 1;
    }
    public enum AgeType{
        ALWAYS((p) -> true),
        WHEN_NOT_TARGETING((p) -> p.getTarget() == null),
        RESET_WHEN_TARGETING((p) -> {
            if (p.getTarget() == null) return true;
            else{
                p.setAge(0);
                return false;
            }
        }),
        REMOVE_IF_NOT_TARGETING((p) ->{
            if (p.getTarget() == null){
                p.discard();
                return false;
            } else return true;
        });

        AgeType(Predicate<PestBase> agePredicate){
            this.agePredicate = agePredicate;
        }
        public final Predicate<PestBase> agePredicate;
        public boolean shouldAge(PestBase toTest){
            return agePredicate.test(toTest);
        }
        public int toInt(){
            return this.ordinal();
        }
        public static AgeType fromInt(int type){
            return AgeType.values()[type % AgeType.values().length];
        }
    }
    public static final EntityDataAccessor<Integer> AGE_TYPE = SynchedEntityData.defineId(PestBase.class, EntityDataSerializers.INT);
    public int getAgeTypeRaw(){return entityData.get(AGE_TYPE);}
    public AgeType getAgeType(){return AgeType.fromInt(getAgeTypeRaw());}
    public void setAgeType(int type) {entityData.set(AGE_TYPE, type);}

    protected PestBase(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("age", getAge());
        tag.putInt("max_age", getMaxAge());
        tag.putInt("age_type", getAgeTypeRaw());
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAge(tag.getInt("age"));
        setMaxAge(tag.getInt("max_age"));
        setAgeType(tag.getInt("age_type"));
    }
    public static final int defaultAge = 1200;
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(MAX_AGE, defaultAge);
        this.entityData.define(AGE_TYPE, 0);
    }

    @Override
    protected void registerPheromoneGoals(){}

    @Override
    public void tick() {
        super.tick();
        if (getAgeType().shouldAge(this)) AgeByOne();
        if (!this.isRemoved() && amITooOld()){
            System.out.println("Old af");
            System.out.println("Age: " + getAge());
            System.out.println("MAX Age: " + getMaxAge());
            this.discard();
        }
    }

    @Override
    public void onAddedToWorld() {
        if (getMaxAge() == 0){
            setMaxAge(defaultAge);
        }
        super.onAddedToWorld();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 1.25f;
    }
    @Override
    protected float getSoundVolume() {
        return 0.75f;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, getVoicePitch());
    }
    @Override
    public boolean canSwarm() {
        return false;
    }
}
