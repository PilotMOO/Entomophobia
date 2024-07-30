package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public abstract class MyiaticBase extends Monster implements GeoEntity {
    protected MyiaticBase(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //NBT
    protected enum state{
        idle,
        walking,
        running,
        flying,
        attacking
    }
    public static final EntityDataAccessor<Integer> AIState = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.INT);
    public int getAIState(){return entityData.get(AIState);}
    public void setAIState(Integer count) {entityData.set(AIState, count);}
    public static final EntityDataAccessor<Float> Reach = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.FLOAT);
    public float getReach(){return entityData.get(Reach);}
    public void setReach(Float count) {entityData.set(Reach, count);}
    public static final EntityDataAccessor<Boolean> IsFlying = SynchedEntityData.defineId(MyiaticBase.class, EntityDataSerializers.BOOLEAN);
    public boolean getIsFlying() {return entityData.get(IsFlying);}
    public void setIsFlying(boolean bool) {entityData.set(IsFlying, bool);}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AIState",entityData.get(AIState));
        tag.putBoolean("IsFlying",entityData.get(IsFlying));
        tag.putFloat("Reach",entityData.get(Reach));
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AIState, tag.getInt("AIState"));
        entityData.set(IsFlying, tag.getBoolean("IsFlying"));
        entityData.set(Reach, tag.getFloat("Reach"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AIState, -1);
        this.entityData.define(IsFlying, false);
        this.entityData.define(Reach, 1f);
    }
    /**/

    //Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>
                (this, LivingEntity.class,  true, livingEntity -> { return livingEntity instanceof Player;}));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>
                (this, LivingEntity.class,  true,livingEntity -> { return !Config.SERVER.blacklisted_targets.get().contains(livingEntity.getEncodeId()) && !(livingEntity instanceof AbstractFish) && !(livingEntity instanceof MyiaticBase) && !(livingEntity instanceof PheromonesEntityBase);}));
    }

    protected class AttackWithAnimationGoal extends MeleeAttackGoal {
        final int AnimLength;
        boolean CurrentlyAttacking;
        int AttackTicker = 0;
        final ArrayList<AttackAnimationDamageType> StrikeDetails;
        int AttackCD = 0;
        final int CDMax;
        final MyiaticBase mob;
        public AttackWithAnimationGoal(MyiaticBase pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, ArrayList<AttackAnimationDamageType> strikeDetails, int SwingAnimationLength, int CD) {
            super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
            mob = pMob;
            StrikeDetails = strikeDetails;
            AnimLength = SwingAnimationLength;
            CDMax = CD;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !getIsFlying();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !getIsFlying();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
            double d0 = this.getAttackReachSqr(pEnemy);
            if (pDistToEnemySqr <= d0 && !CurrentlyAttacking && AttackCD <= 0) {
                this.resetAttackCooldown();
                CurrentlyAttacking = true;
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (CurrentlyAttacking){
                mob.setAIState(state.attacking.ordinal());
                AttackTicker++;
                if (mob.getTarget() != null){
                    for (AttackAnimationDamageType AADT : StrikeDetails){
                        if (AADT.Pos == AttackTicker){
                            if (mob.getPerceivedTargetDistanceSquareForMeleeAttack(mob.getTarget()) <= mob.getReach()){
                                mob.MyiaticHurtTarget(mob.getTarget(), AADT.Knockback, AADT.Iframes);
                            }
                        }
                    }
                }
                if (AttackTicker >= AnimLength){
                    FinalizeAttack();
                }
            }
            if (AttackCD > 0){
                AttackCD--;
            }
        }

        @Override
        public void stop() {
            FinalizeAttack();
            super.stop();
        }

        private void FinalizeAttack(){
            AttackTicker = 0;
            CurrentlyAttacking = false;
            mob.setAIState(state.idle.ordinal());
        }
    }
    /**/

    //Custom Methods
    public boolean isChasing(){
        return this.isAggressive() && this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }
    public boolean isMoving(){
        return this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;
    }

    protected int StateManager(){
        if (getIsFlying()){
            return state.flying.ordinal();
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
    /**/

    //Overridden Methods
    @Override
    public void tick() {
        super.tick();
        setAIState(StateManager());
    }

    public boolean MyiaticHurtTarget(Entity entity, boolean knockback, boolean Iframes){
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = knockback ? (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK) : 0;

        boolean flag = entity.hurt(EntomoDamageTypes.myiatic_basic(this), f);
        if (flag) {
            if (f1 > 0.0F && entity instanceof LivingEntity livingEntity) {
                livingEntity.knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
            if (Iframes){
                entity.invulnerableTime = 0;
            }
        }
        return flag;
    }
    /**/

    //Nested Classes
    protected class AttackAnimationDamageType{
        public final int Pos;
        public final boolean Knockback;
        public final boolean Iframes;
        public AttackAnimationDamageType(int pos, boolean knockback, boolean iframes){
            Pos = pos;
            Knockback = knockback;
            Iframes = iframes;
        }
        public AttackAnimationDamageType(boolean Combo, int pos){
            Knockback = !Combo;
            Iframes = !Combo;
            Pos = pos;
        }
    }
    /**/
}
