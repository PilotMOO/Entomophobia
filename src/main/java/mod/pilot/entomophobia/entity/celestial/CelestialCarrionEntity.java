package mod.pilot.entomophobia.entity.celestial;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class CelestialCarrionEntity extends MyiaticBase {
    public CelestialCarrionEntity(EntityType<? extends Monster> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public enum ModelType{
        empty,
        base,
        tendril,
        eye,
        claw;
        public static final int totalCount = values().length;
        public static ModelType fromInt(int index){
            return ModelType.values()[index % totalCount];
        }
    }
    public static final EntityDataAccessor<Integer> MType = SynchedEntityData.defineId(CelestialCarrionEntity.class, EntityDataSerializers.INT);
    public ModelType getMType(){return ModelType.fromInt(entityData.get(MType));}
    public int getMTypeRaw(){return entityData.get(MType);}
    public void setMType(int type) {
        entityData.set(MType, type);
    }
    public void setMType(ModelType ordinal) {setMType(ordinal.ordinal());}
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MType", entityData.get(MType));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setMType(tag.getInt("MType"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MType, 0);
    }

    private final EntityDimensions baseDimensions = new EntityDimensions(1f, 1f, false);
    private final EntityDimensions tendrilDimensions = new EntityDimensions(1.25f, 0.75f, false);
    private final EntityDimensions eyeDimensions = new EntityDimensions(0.8f, 1.9f, false);
    private final EntityDimensions clawDimensions = new EntityDimensions(1f, 0.9f, false);
    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return switch (getMType()){
            case empty -> super.getDimensions(pose);
            case base -> baseDimensions;
            case tendril -> tendrilDimensions;
            case eye -> eyeDimensions;
            case claw -> clawDimensions;
        };
    }
    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        if (MType.equals(pKey)){
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticPigEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }
    @Override
    protected void registerGoals() {
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (level() instanceof ServerLevel && getMTypeRaw() == 0) {
            setMType(random.nextInt(1, ModelType.totalCount));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()) SpreadEffects();
    }

    private static final int InfatuationSpread = 300;
    private static final int InfatuationSpreadAOE = 64;
    private static final int MyiasisSpread = 400;
    private static final int MyiasisSpreadAOE = 12;
    public void SpreadEffects(){
        if (tickCount % InfatuationSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), InfatuationSpreadAOE, InfatuationSpreadAOE, InfatuationSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.INFATUATION.get(), 1200));
            }
        }
        if (tickCount % MyiasisSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), MyiasisSpreadAOE, MyiasisSpreadAOE, MyiasisSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 600));
            }
        }
    }

    @Override
    public void push(@NotNull Entity entity) {
        super.push(entity);
        if (entity instanceof LivingEntity le){
            boolean flag = !le.hasEffect(EntomoMobEffects.MYIASIS.get()) || le.getEffect(EntomoMobEffects.MYIASIS.get()).getAmplifier() < 4;
            if (flag) le.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200, 4));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "IdleManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean canSwarm() {
        return false;
    }
}
