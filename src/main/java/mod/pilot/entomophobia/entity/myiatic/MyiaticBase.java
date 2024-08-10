package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.AI.*;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import mod.pilot.entomophobia.worlddata.WorldSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class MyiaticBase extends Monster implements GeoEntity {
    protected MyiaticBase(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //NBT
    public enum state{
        idle,
        walking,
        running,
        flying,
        attacking,
        other
    }
    public static final EntityDataAccessor<Integer> AIState = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.INT);
    public int getAIState(){return entityData.get(AIState);}
    public void setAIState(Integer count) {entityData.set(AIState, count);}
    public static final EntityDataAccessor<Float> Reach = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.FLOAT);
    public float getReach(){return entityData.get(Reach);}
    public void setReach(Float count) {entityData.set(Reach, count);}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AIState",entityData.get(AIState));
        tag.putFloat("Reach",entityData.get(Reach));
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AIState, tag.getInt("AIState"));
        entityData.set(Reach, tag.getFloat("Reach"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AIState, 0);
        this.entityData.define(Reach, 0f);
    }

    public static int DodgeChance = 0;
    /**/

    //Goals
    @Override
    protected void registerGoals() {
        registerBasicGoals();
        registerFlightGoals();
        registerPheromoneGoals();
    }
    protected void registerBasicGoals(){
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PreyPriorityNearestAttackable(this, true));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.75D, 80));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LocateAndEatFoodOffTheFloorGoal(this, 20));
    }
    protected void registerFlightGoals(){}
    protected void registerPheromoneGoals(){
        this.targetSelector.addGoal(2, new SpawnPheromonesGoal(this, EntomoEntities.PREYHUNT.get(), 600, this::PreyHuntPredicate));
    }
    /**/

    //Custom Methods
    public DamageSource GetDamageSource(){
        return EntomoDamageTypes.myiatic_basic(this);
    }
    public boolean isChasing(){
        return this.isAggressive() && this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }
    public boolean isMoving(){
        return this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }

    protected int StateManager(){
        if (getAIState() == state.flying.ordinal()){
            return getAIState();
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
    public boolean IsThereABlockUnderMe(int VerticalSearchRange){
        BlockPos pos = blockPosition();
        for (int i = 1; i < VerticalSearchRange + 1; i++){
            Level world = level();
            BlockPos newPos = new BlockPos(pos.getX(), pos.getY() - i, pos.getZ());
            BlockState state = world.getBlockState(newPos);
            if (!state.isAir() && state.entityCanStandOn(world.getChunkForCollisions((int)this.position().x / 16, (int)this.position().z / 16),
            newPos, this)){
                return true;
            }
        }
        return false;
    }
    public ArrayList<LivingEntity> GetValidTargets(int searchRange){
        AABB nearby = getBoundingBox().inflate(searchRange);
        return new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, nearby, this::TestValidEntity));
    }
    public ArrayList<LivingEntity> GetValidTargets(){
        return GetValidTargets((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public ArrayList<LivingEntity> GetNearbyPrey(){
        AABB nearby = getBoundingBox().inflate((int)getAttributeValue(Attributes.FOLLOW_RANGE));
        return new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, nearby, (P) -> TestValidEntity(P) && P.hasEffect(EntomoMobEffects.PREY.get())));
    }
    public LivingEntity GetClosestPrey(){
        LivingEntity closest = null;
        double distance = Double.MAX_VALUE;

        for (LivingEntity L : GetNearbyPrey()){
            if (closest != null){
                if (L.distanceTo(this) < distance){
                    closest = L;
                    distance = L.distanceTo(this);
                }
            }
            else{
                closest = L;
                distance = L.distanceTo(this);
            }
        }
        return closest;
    }
    public ArrayList<MyiaticBase> GetNearbyMyiatics(int searchRange, Predicate<MyiaticBase> myiaticPredicate){
        AABB nearby = getBoundingBox().inflate(searchRange);
        return new ArrayList<>(level().getEntitiesOfClass(MyiaticBase.class, nearby, myiaticPredicate));
    }
    public ArrayList<MyiaticBase> GetNearbyMyiatics(Predicate<MyiaticBase> myiaticPredicate){
        return GetNearbyMyiatics((int)getAttributeValue(Attributes.FOLLOW_RANGE), myiaticPredicate);
    }
    public ArrayList<MyiaticBase> GetNearbyMyiatics(int searchRange){
        return GetNearbyMyiatics(searchRange, (M) -> true);
    }
    public ArrayList<MyiaticBase> GetNearbyMyiatics(){
        return GetNearbyMyiatics((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public MyiaticBase GetClosestMyiatic(int searchRange){
        MyiaticBase closest = null;
        double distance = Double.MAX_VALUE;

        for (MyiaticBase M : GetNearbyMyiatics(searchRange)){
            if (closest != null){
                if (M.distanceTo(this) < distance){
                    closest = M;
                    distance = M.distanceTo(this);
                }
            }
            else{
                closest = M;
                distance = M.distanceTo(this);
            }
        }
        return closest;
    }
    public MyiaticBase GetClosestMyiatic(){
        return GetClosestMyiatic((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public boolean IsThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type, int searchRange){
        String instance = type.create(level()).getEncodeId();

        AABB nearby = getBoundingBox().inflate(searchRange);
        ArrayList<PheromonesEntityBase> AllPheromones = new ArrayList<>(level().getEntitiesOfClass(PheromonesEntityBase.class, nearby));

        for (PheromonesEntityBase P : AllPheromones){
            if (P != null && Objects.equals(P.getEncodeId(), instance)){
                return true;
            }
        }
        return false;
    }
    public boolean IsThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type){
        return IsThereAPheromoneOfTypeXNearby(type, (int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }

    protected boolean TryToDodge() {
        if (CanDodge() && verticalCollisionBelow && !horizontalCollision){
            int Direction = getRandom().nextIntBetweenInclusive(0, 1) != 0 ? -1 : 1;
            setDeltaMovement(getDeltaMovement().add(Vec3.directionFromRotation(new Vec2 (getRotationVector().x, getRotationVector().y + 135 * Direction)).multiply(1.25, 0, 1.25)));
            return true;
        }
        return false;
    }
    protected boolean CanDodge(){
        return false;
    }
    protected boolean PreyHuntPredicate(MyiaticBase parent){
        return GetNearbyMyiatics(128).size() > 5 && GetValidTargets().size() > 3;
    }
    /**/

    //Overridden Methods
    @Override
    public void tick() {
        super.tick();
        setAIState(StateManager());
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean superFlag = true;
        Entity sourceEntity = pSource.getEntity();
        if (sourceEntity instanceof LivingEntity){
            if (TestValidEntity((LivingEntity)sourceEntity)){
                for (MyiaticBase M : GetNearbyMyiatics((int)(getAttributeValue(Attributes.FOLLOW_RANGE) * 2))){
                    if (M.getTarget() == null){
                        M.setTarget((LivingEntity)sourceEntity);
                    }
                }
                setTarget((LivingEntity)sourceEntity);
            }
            if (sourceEntity instanceof MyiaticBase){
                superFlag = false;
            }
            if (sourceEntity == getTarget()){
                if (getRandom().nextIntBetweenInclusive(0, 10) <= DodgeChance){
                    superFlag = !TryToDodge();
                }
            }
        }
        if (superFlag){
            return super.hurt(pSource, pAmount);
        }
        return false;
    }
    @Override
    public boolean doHurtTarget(@Nullable Entity pEntity) {
        if (pEntity != null){
            if (pEntity instanceof LivingEntity){
                ((LivingEntity) pEntity).addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200));
            }
            float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (pEntity instanceof LivingEntity) {
                f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)pEntity).getMobType());
                f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
            }

            int i = EnchantmentHelper.getFireAspect(this);
            if (i > 0) {
                pEntity.setSecondsOnFire(i * 4);
            }

            boolean flag = pEntity.hurt(GetDamageSource(), f);
            if (flag) {
                if (f1 > 0.0F && pEntity instanceof LivingEntity) {
                    ((LivingEntity)pEntity).knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                this.doEnchantDamageEffects(this, pEntity);
                this.setLastHurtMob(pEntity);
            }

            return flag;        }
        return false;
    }

    @Override
    public void checkDespawn() {
        Entity player = this.level().getNearestPlayer(this, -1.0D);
        if (player != null && player.distanceTo(this) < Config.SERVER.distance_to_player_until_despawn.get()){
            super.checkDespawn();
        }
        else if (Entomophobia.activeData.GetMyiaticCount() < Config.SERVER.mob_cap.get()){
            this.discard();
        }
    }
    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        if (Entomophobia.activeData.GetMyiaticCount() > Config.SERVER.mob_cap.get() && getTarget() == null){
            return true;
        }
        return false;
    }
    @Override
    public boolean isPersistenceRequired() {
        return Entomophobia.activeData.GetMyiaticCount() < Config.SERVER.mob_cap.get() && getTarget() != null;
    }
    /**/

    //Better Targeting
    public boolean TestValidEntity(LivingEntity e) {
        if (e instanceof LivingEntity){
            if (e instanceof Player && ((Player)e).isCreative() || e.isSpectator()){
                return false;
            }
            else if (e instanceof MyiaticBase || e instanceof PheromonesEntityBase){
                return false;
            }
            else if (Config.SERVER.blacklisted_targets.get().contains((e.getEncodeId()))){
                return e instanceof Creeper && hasEffect(EntomoMobEffects.FRENZY.get());
            }
            else return !(e instanceof AbstractFish);
        }
        return false;
    }
    /**/
}
