package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        final int StrikePos;
        final MyiaticBase mob;
        public AttackWithAnimationGoal(MyiaticBase pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, int strikePos, int SwingAnimationLength) {
            super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
            mob = pMob;
            StrikePos = strikePos;
            AnimLength = SwingAnimationLength;
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

        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = mob.getTarget();
            if (target != null) {
                if(mob.distanceTo(target) <= getAttackReachSqr(target)){
                    CurrentlyAttacking = true;
                }
            }
            if (CurrentlyAttacking){
                mob.setAIState(state.attacking.ordinal());
                AttackTicker++;
                if (StrikePos == AttackTicker){
                    mob.doHurtTarget(target);
                    mob.setDeltaMovement(mob.getDeltaMovement().add(getForward().multiply(1.1, 0, 1.1)));
                }
                if (AttackTicker >= AnimLength){
                    FinalizeAttack();
                }
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            return super.getAttackReachSqr(pAttackTarget) + getReach();
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

    @Override
    public boolean doHurtTarget(@Nullable Entity pEntity) {
        if (pEntity != null){
            return super.doHurtTarget(pEntity);
        }
        return false;
    }

    /**/
}
