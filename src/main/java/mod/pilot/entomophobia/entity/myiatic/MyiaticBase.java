package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.AI.*;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import mod.pilot.entomophobia.worlddata.EntomoDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class MyiaticBase extends Monster implements GeoEntity {
    protected MyiaticBase(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -3.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, -2.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
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
        this.goalSelector.addGoal(1, new BreakBlocksInMyWayGoal(this));
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


    public boolean isChasing(){
        return getTarget() != null && this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }
    public boolean isMoving(){
        return this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
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
            if (P != null && Objects.equals(P.getEncodeId(), instance)){
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
    public void BreakBlocksInMyWay(){
        AABB breakBox = getBoundingBox().inflate(1.2);
        for (BlockPos pos : BlockPos.betweenClosed((int)breakBox.minX, (int)breakBox.minY, (int)breakBox.minZ, (int)breakBox.maxX, (int)breakBox.maxY, (int)breakBox.maxZ)){
            BlockState state = level().getBlockState(pos);
            if (state.is(BlockTags.LEAVES) || isThisGlass(state) || state.getBlock() instanceof BambooStalkBlock){
                level().removeBlock(pos, false);
                level().levelEvent(2001, pos, Block.getId(level().getBlockState(pos)));
                level().playSound(this, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1.25f);
            }
        }
    }
    protected boolean isThisGlass(BlockState state){
        Block block = state.getBlock();
        return block instanceof GlassBlock || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock || state.is(Blocks.GLASS_PANE);
    }
    /**/

    //Overridden Methods
    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && getTarget().isDeadOrDying()){
            setTarget(null);
        }
        setAIState(StateManager());
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean superFlag = true;
        Entity sourceEntity = pSource.getEntity();
        if (sourceEntity instanceof LivingEntity LEntity){
            if (TestValidEntity(LEntity)){
                for (MyiaticBase M : getNearbyMyiatics((int)(getAttributeValue(Attributes.FOLLOW_RANGE) * 2))){
                    if (M.getTarget() == null){
                        M.setTarget(LEntity);
                    }
                }
                setTarget(LEntity);
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
                if (pEntity instanceof LivingEntity LEntity){
                    LEntity.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200));

                    if (f1 > 0.0F) {
                        ((LivingEntity)pEntity).knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
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

    @Override
    public void checkDespawn() {
        Entity player = this.level().getNearestPlayer(this, -1.0D);
        if (player != null && player.distanceTo(this) < Config.SERVER.distance_to_player_until_despawn.get()){
            super.checkDespawn();
        }
        else if (Entomophobia.activeData.GetMyiaticCount() > Config.SERVER.mob_cap.get()){
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
        return Entomophobia.activeData.GetMyiaticCount() < Config.SERVER.mob_cap.get() || getTarget() != null;
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            net.minecraftforge.event.entity.living.MobEffectEvent.Applicable event = new net.minecraftforge.event.entity.living.MobEffectEvent.Applicable(this, effect);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
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
