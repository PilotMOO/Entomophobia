package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.AI.*;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.interfaces.IDodgable;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import mod.pilot.entomophobia.data.BooleanCache;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Predicate;

public abstract class MyiaticBase extends Monster implements GeoEntity {
    protected MyiaticBase(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -3.0f);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 4.0f);
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
    public void setAIState(state ordinal) {entityData.set(AIState, ordinal.ordinal());}
    public static final EntityDataAccessor<Float> Reach = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.FLOAT);
    public float getReach(){return entityData.get(Reach);}
    public void setReach(Float count) {entityData.set(Reach, count);}
    public static final EntityDataAccessor<Boolean> EncouragedDespawn = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.BOOLEAN);
    public boolean getEncouragedDespawn(){return entityData.get(EncouragedDespawn);}
    public void setEncouragedDespawn(boolean flag) {entityData.set(EncouragedDespawn, flag);}

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AIState", entityData.get(AIState));
        tag.putFloat("Reach", entityData.get(Reach));
        tag.putBoolean("EncouragedDespawn", entityData.get(EncouragedDespawn));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AIState, tag.getInt("AIState"));
        entityData.set(Reach, tag.getFloat("Reach"));
        entityData.set(EncouragedDespawn, tag.getBoolean("EncouragedDespawn"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AIState, 0);
        this.entityData.define(Reach, 0f);
        this.entityData.define(EncouragedDespawn, false);
    }
    /**/

    //Goals
    protected ArrayList<Pair<Integer, Goal>> QueuedGoals = new ArrayList<>();
    protected ArrayList<Goal> QueuedRemoveGoals = new ArrayList<>();
    public void QueGoal(int priority, Goal goal){
        QueuedGoals.add(new Pair<>(priority, goal));
    }
    public void QueRemoveGoal(Goal goal){
        QueuedRemoveGoals.add(goal);
    }
    public void RegisterQueuedGoals(){
        if (QueuedGoals.size() == 0) return;
        ArrayList<Pair<Integer, Goal>> toUnque = new ArrayList<>(QueuedGoals);
        for (Pair<Integer, Goal> queued : toUnque){
            this.goalSelector.addGoal(queued.getA(), queued.getB());
        }
        QueuedGoals.clear();
    }
    public void ClearQueuedRemovedGoals(){
        if (QueuedRemoveGoals.size() == 0) return;
        ArrayList<Goal> toUnque = new ArrayList<>(QueuedRemoveGoals);
        for (Goal queued : toUnque){
            this.goalSelector.removeGoal(queued);
        }
        QueuedRemoveGoals.clear();
    }
    @Override
    protected void registerGoals() {
        registerBasicGoals();
        registerFlightGoals();
        registerPheromoneGoals();
    }
    protected void registerBasicGoals(){
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PreyPriorityNearestAttackable(this, true));
        this.goalSelector.addGoal(3, new HomophobicRandomStrollGoal(this, 0.75D, 40, 48, 16));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LocateAndEatFoodOffTheFloorGoal(this, 20));
        this.goalSelector.addGoal(1, new BreakBlocksInMyWayGoal(this));
        if (canSwarm() && getDistanceToClosestNest() == -1 || getDistanceToClosestNest() > 2048){
            this.goalSelector.addGoal(2, new FormNestSwarmGoal(this, 600, 5));
        }
    }
    protected void registerFlightGoals(){}
    protected void registerPheromoneGoals(){
        this.targetSelector.addGoal(2, new SpawnPheromonesGoal(this, EntomoEntities.PREYHUNT.get(), 600, this::PreyHuntPredicate));
    }
    /**/

    //Custom Methods
    public DamageSource getDamageSource(){
        return EntomoDamageTypes.myiatic_basic(this);
    }
    protected int StateManager(){
        if (getAIState() == state.other.ordinal()){
            return state.other.ordinal();
        }
        else if (getAIState() == state.flying.ordinal()){
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
    protected boolean PreyHuntPredicate(MyiaticBase parent){
        return getNearbyMyiatics(128).size() > 5 && getValidTargets().size() > 3;
    }
    /**/

    //Overridden Methods
        /*Continuous Methods*/
    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && getTarget().isDeadOrDying()){
            setTarget(null);
        }
        setAIState(StateManager());
    }
    @Override
    public void aiStep() {
        RegisterQueuedGoals();
        ClearQueuedRemovedGoals();
        super.aiStep();
    }

    /*Damage-related*/
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean superFlag = true;
        Entity sourceEntity = pSource.getEntity();
        if (sourceEntity instanceof LivingEntity LEntity){
            if (TestValidEntity(LEntity)){
                for (MyiaticBase M : getNearbyMyiatics((int)(getAttributeValue(Attributes.FOLLOW_RANGE) * 2))){
                    if (M.getTarget() == null || (M.getTarget() instanceof Targeting target1 && target1.getTarget() != M
                            && getTarget() instanceof Targeting target2 && target2.getTarget() == this)){
                        M.setTarget(LEntity);
                    }
                }
                setTarget(LEntity);
            }
            if (sourceEntity instanceof MyiaticBase){
                superFlag = false;
            }
            if (this instanceof IDodgable dodgable && sourceEntity == getTarget()){
                superFlag = !dodgable.TryToDodge(this);
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

            boolean flag = pEntity.hurt(getDamageSource(), f);
            if (flag) {
                if (pEntity instanceof LivingEntity LEntity){
                    LEntity.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200));

                    if (f1 > 0.0F) {
                        ((LivingEntity)pEntity).knockback((f1 * 0.5F), Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                    }
                }

                this.doEnchantDamageEffects(this, pEntity);
                this.setLastHurtMob(pEntity);
            }

            return flag;


        }
        return false;
    }
    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (isInSwarm()){
            this.LeaveSwarm(false);
        }
    }

        /*Movement*/
    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (isInFluidType() && getTarget() != null){
            double waterMoveSpeed = (getAttributeValue(Attributes.MOVEMENT_SPEED) * getWaterSlowDown()) * 0.5;
            setDeltaMovement(getDeltaMovement().add(getDirectionToTarget()).multiply(waterMoveSpeed, waterMoveSpeed, waterMoveSpeed));

            if (getTarget().getY() < getY() && getAirSupply() > getMaxAirSupply() / 4){
                double newYSpeed = getDeltaMovement().y / 0.8;
                setDeltaMovement(getDeltaMovement().x, newYSpeed, getDeltaMovement().y);
            }
            getLookControl().setLookAt(getTarget());
            getLookControl().tick();
        }
        super.travel(pTravelVector);
    }

        /*Despawning*/
    @Override
    public void checkDespawn() {
        if (getTarget() != null) return;
        if (getEncouragedDespawn()) this.discard();

        Entity player = this.level().getNearestPlayer(this, -1.0D);
        if (player != null && player.distanceTo(this) < Config.SERVER.distance_to_player_until_despawn.get()){
            super.checkDespawn();
        }
        else if (EntomoGeneralSaveData.GetMyiaticCount() > Config.SERVER.mob_cap.get()){
            this.discard();
        }
    }
    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return EntomoGeneralSaveData.GetMyiaticCount() > Config.SERVER.mob_cap.get() && getTarget() == null;
    }
    @Override
    public boolean isPersistenceRequired() {
        return EntomoGeneralSaveData.GetMyiaticCount() < Config.SERVER.mob_cap.get() || getTarget() != null;
    }

    /*Booleans*/
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            net.minecraftforge.event.entity.living.MobEffectEvent.Applicable event = new net.minecraftforge.event.entity.living.MobEffectEvent.Applicable(this, effect);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
    }

        /*Static variable methods*/
    @Override
    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }
    /**/

    //Helper Methods and Shorthands
        /*Movement*/
    public boolean isChasing(){
        return getTarget() != null && this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }
    public boolean isMoving(){
        return this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }

    /*Positional Helpers*/
    public ArrayList<LivingEntity> getValidTargets(int searchRange){
        AABB nearby = getBoundingBox().inflate(searchRange);
        return new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, nearby, this::TestValidEntity));
    }
    public ArrayList<LivingEntity> getValidTargets(){
        return getValidTargets((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public ArrayList<LivingEntity> getNearbyPrey(){
        AABB nearby = getBoundingBox().inflate((int)getAttributeValue(Attributes.FOLLOW_RANGE));
        return new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, nearby, (P) -> TestValidEntity(P) && P.hasEffect(EntomoMobEffects.PREY.get())));
    }
    public LivingEntity getClosestPrey(){
        LivingEntity closest = null;
        double distance = Double.MAX_VALUE;

        for (LivingEntity L : getNearbyPrey()){
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
    public ArrayList<MyiaticBase> getNearbyMyiatics(int searchRange, Predicate<MyiaticBase> myiaticPredicate){
        AABB nearby = getBoundingBox().inflate(searchRange);
        return new ArrayList<>(level().getEntitiesOfClass(MyiaticBase.class, nearby, myiaticPredicate));
    }
    public ArrayList<MyiaticBase> getNearbyMyiatics(Predicate<MyiaticBase> myiaticPredicate){
        return getNearbyMyiatics((int)getAttributeValue(Attributes.FOLLOW_RANGE), myiaticPredicate);
    }
    public ArrayList<MyiaticBase> getNearbyMyiatics(int searchRange){
        return getNearbyMyiatics(searchRange, (M) -> true);
    }
    public ArrayList<MyiaticBase> getNearbyMyiatics(){
        return getNearbyMyiatics((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public MyiaticBase getClosestMyiatic(int searchRange){
        MyiaticBase closest = null;
        double distance = Double.MAX_VALUE;

        for (MyiaticBase M : getNearbyMyiatics(searchRange)){
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
    public MyiaticBase getClosestMyiatic(){
        return getClosestMyiatic((int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public boolean isThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type, int searchRange){
        String instance = type.create(level()).getEncodeId();

        AABB nearby = getBoundingBox().inflate(searchRange);
        ArrayList<PheromonesEntityBase> AllPheromones = new ArrayList<>(level().getEntitiesOfClass(PheromonesEntityBase.class, nearby));

        for (PheromonesEntityBase P : AllPheromones){
            if (Objects.equals(P.getEncodeId(), instance)){
                return true;
            }
        }
        return false;
    }
    public boolean isThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type){
        return isThereAPheromoneOfTypeXNearby(type, (int)getAttributeValue(Attributes.FOLLOW_RANGE));
    }
    public Vec3 getDirectionTo(Vec3 pos){
        if (pos != null){
            return pos.subtract(position()).normalize();
        }
        return null;
    }
    public Vec3 getDirectionFrom(Vec3 pos){
        if (pos != null){
            return position().subtract(pos).normalize();
        }
        return null;
    }
    public Vec3 getDirectionToTarget(){
        if (getTarget() != null){
            return getDirectionTo(getTarget().position());
        }
        return null;
    }
    public Vec3 getDirectionFromTarget(){
        if (getTarget() != null){
            return getDirectionFrom(getTarget().position());
        }
        return null;
    }
    public double getDistanceToClosestNest() {
        double distance = -1;
        for (Nest nest : NestManager.getActiveNests()){
            if (distance == -1 || distance > nest.origin.distanceTo(position())) {
                distance = nest.origin.distanceTo(position());
            }
        }
        return distance;
    }
    public boolean isThereABlockUnderMe(int VerticalSearchRange){
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

    /*Targeting*/
    private static final BooleanCache<LivingEntity> TargetCache = new BooleanCache<>(256, MyiaticBase::CachePredicate);
    private static final Set<String> blacklist = new HashSet<>(Config.SERVER.blacklisted_targets.get());
    private static boolean CachePredicate(LivingEntity e) {
        if (e instanceof Player p) return !(p.isCreative() || p.isSpectator());
        if (e instanceof MyiaticBase) return false;
        if ( e instanceof AbstractFish) return false;
        else return !isInsideOfTargetBlacklist(e);
    }
    public static boolean isInsideOfTargetBlacklist(LivingEntity e){
        return e != null && isInsideOfTargetBlacklist(e.getEncodeId());
    }
    public static boolean isInsideOfTargetBlacklist(String ID){
        if (ID == null) return false;
        boolean flag = false;
        for (String s : blacklist){
            if (flag) break;
            if (s.endsWith(":")){
                String IDSolo = s.replace(":", "");
                flag = ID.split(":")[0].equals(IDSolo);
            }else{
                flag = ID.equals(s);
            }
        }
        return flag;
    }
    public boolean TestValidEntity(LivingEntity e) {
        if (e instanceof Creeper && !isInsideOfTargetBlacklist(e)) return hasEffect(EntomoMobEffects.FRENZY.get());
        else return TargetCache.Test(e);
    }
    /**/

    //Swarm Management
    private Swarm currentSwarm;
    public boolean canSwarm() {
        return true;
    }
    public Swarm getSwarm(){
        return currentSwarm;
    }
    public boolean isInSwarm(){
        return getSwarm() != null;
    }
    public boolean amITheCaptain(){
        return getSwarm() != null && getSwarm().getCaptain() == this;
    }
    public boolean TryToRecruit(@NotNull Swarm swarm){
        if (canSwarm() && getSwarm() == null){
            boolean joinFlag = swarm.AttemptToRecruit(this);
            if (joinFlag) currentSwarm = swarm;
            return joinFlag;
        }
        return false;
    }

    public void ForceJoin(@NotNull Swarm swarm, boolean ignoreCap){
        if (getSwarm() == swarm) return;

        if (ignoreCap || swarm.getRecruitCount() < swarm.getMaxRecruits()) {
            swarm.addToUnits(this);
            swarm.AssignAllOrdersFor(this);
            currentSwarm = swarm;
        }
        System.out.println(this + " was forced to join " + swarm + " against their will!");
    }
    public void LeaveSwarm(boolean disbandIfCaptain){
        if (getSwarm() != null){
            getSwarm().DropMember(this, disbandIfCaptain);
        }
        currentSwarm = null;
    }
    public void SwitchSwarm(Swarm newSwarm, boolean disbandIfCaptain){
        getSwarm().DropMember(this, disbandIfCaptain);
        boolean joinFlag = newSwarm.AttemptToRecruit(this);
        if (joinFlag){
            currentSwarm = newSwarm;
        }
    }
    /**/
}
