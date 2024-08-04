package mod.pilot.entomophobia.entity.myiatic;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.EntomoWorldManager;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.Myiasis;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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
        this.entityData.define(AIState, -1);
        this.entityData.define(Reach, 1f);
    }
    /**/

    //Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(1, new PreyPriorityNearestAttackable(this, true));
        registerFlightGoals();
    }

    protected void registerFlightGoals(){

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
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
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
                if (target != null) {
                    if (StrikePos == AttackTicker && mob.distanceTo(target) < getAttackReachSqr(target) /*&& mob.hasLineOfSight(mob.getTarget())*/){
                        mob.doHurtTarget(target);
                        mob.setDeltaMovement(mob.getDeltaMovement().add(getForward().multiply(1.05, 0, 1.05)));
                    }
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
    protected class PreyPriorityNearestAttackable extends NearestAttackableTargetGoal<LivingEntity> {
        final MyiaticBase mob;
        boolean wasPrey = false;
        public PreyPriorityNearestAttackable(MyiaticBase parent, boolean pMustSee) {
            super(parent, LivingEntity.class, pMustSee, parent::TestValidEntity);
            mob = parent;
        }

        @Override
        public void tick() {
            super.tick();
            if (wasPrey && !target.hasEffect(EntomoMobEffects.PREY.get())){
                findTarget();
                this.mob.setTarget(this.target);
            }
        }

        @Override
        protected void findTarget() {
            LivingEntity prey = EntomoWorldManager.GetNearbyPrey(mob);
            if (prey != null && mob.hasEffect(EntomoMobEffects.HUNT.get()) && mob.TestValidEntity(prey)){
                this.target = prey;
                wasPrey = true;
            }
            else{
                this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                if (this.target == null) {
                    this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType,
                            this.getTargetSearchArea(this.getFollowDistance()), mob::TestValidEntity),
                            this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                    wasPrey = false;
                }
            }
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
    protected boolean TryToDodge() {
        if (CanDodge() && verticalCollisionBelow){
            int Direction = getRandom().nextIntBetweenInclusive(0, 1) != 0 ? -1 : 1;
            setDeltaMovement(getDeltaMovement().add(Vec3.directionFromRotation(new Vec2 (getRotationVector().x, getRotationVector().y + 90 * Direction)).multiply(1.1, 0, 1.1)));
            return true;
        }
        return false;
    }
    protected boolean CanDodge(){
        return false;
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
                setTarget((LivingEntity)sourceEntity);
            }
            if (sourceEntity instanceof MyiaticBase){
                superFlag = false;
            }
            if (sourceEntity == getTarget()){
                if (getRandom().nextIntBetweenInclusive(0, 10) <= 3){
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
            return super.doHurtTarget(pEntity);
        }
        return false;
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
                return false;
            }
            else if (e instanceof AbstractFish){
                return false;
            }
            return true;
        }
        return false;
    }

    public LivingEntity FindValidTarget(){
        LivingEntity BestValidTarget = null;

        if (this.hasEffect(EntomoMobEffects.HUNT.get())){
            AABB PreySearchaabb = new AABB(this.blockPosition()).inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE) + Config.SERVER.hunt_bonus_range.get());

            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, PreySearchaabb, this::TestValidEntity)){
                if (target.hasEffect(EntomoMobEffects.PREY.get())){
                    if (BestValidTarget == null){
                        BestValidTarget = target;
                    }
                    else if (BestValidTarget.distanceTo(this) > target.distanceTo(this)){
                        BestValidTarget = target;
                    }
                }
            }
        }
        if (BestValidTarget == null){
            AABB BaseSearchaabb = new AABB(this.blockPosition()).inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE));

            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, BaseSearchaabb, this::TestValidEntity)){
                if (BestValidTarget == null){
                    BestValidTarget = target;
                }
                else if (BestValidTarget.distanceTo(this) > target.distanceTo(this)){
                    BestValidTarget = target;
                }
            }
        }

        return BestValidTarget;
    }
    /**/

    //FlightManagement
    protected class FlyToGoal extends Goal {
        final MyiaticBase parent;
        Vec3 finalPos;
        int ActiveFlightTime;
        int FlightState;
        boolean IsFlying;
        int FlightCD;
        final int MaxFlightCD;
        final int MaxAscensionTime;
        final int MaxGlideTime;
        final int TargetHeightThreshold;
        double VFlightSpeed;
        double HFlightSpeed;
        enum FlightStates{
            Disabled,
            NotFlying,
            Ascending,
            Gliding,
            Falling
        }
        boolean CheckFly(){
            return FlightState != FlightStates.Disabled.ordinal() && !IsFlying && FlightCD <= 0 && WantsToTakeOff();
        }
        boolean WantsToTakeOff(){
            if (parent.getNavigation().getPath() != null && parent.getNavigation().getPath().getEndNode() != null){
                return parent.getNavigation().getPath().getEndNode().y - parent.position().y > TargetHeightThreshold;
            }
            return false;
        }
        boolean AmITooHigh(LivingEntity target){
            if (target != null){
                return position().y - target.position().y > TargetHeightThreshold;
            }
            return false;
        }
        /**/

        public FlyToGoal(MyiaticBase parent, @Nullable Vec3 targetPos, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                         int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed){
            this.parent = parent;
            finalPos = targetPos;
            ActiveFlightTime = 0;
            FlightState = 1;
            IsFlying = false;
            FlightCD = 0;
            MaxFlightCD = maxFlightCD;
            MaxAscensionTime = maxAscensionTime;
            MaxGlideTime = maxGlideTime;
            TargetHeightThreshold = targetHeightThreshold;
            VFlightSpeed = vFlightSpeed / 1000;
            HFlightSpeed = hFlightSpeed / 1000;
        }
        public FlyToGoal(MyiaticBase parent, @Nullable LivingEntity target, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                         int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed){
            this.parent = parent;
            if (target != null){
                finalPos = target.position();
            }
            ActiveFlightTime = 0;
            FlightState = 1;
            IsFlying = false;
            FlightCD = 0;
            MaxFlightCD = maxFlightCD;
            MaxAscensionTime = maxAscensionTime;
            MaxGlideTime = maxGlideTime;
            TargetHeightThreshold = targetHeightThreshold;
            VFlightSpeed = vFlightSpeed;
            HFlightSpeed = hFlightSpeed;
        }

        //Goal-related Methods and overrides
        @Override
        public boolean canUse() {
            return finalPos != null;
        }
        @Override
        public void tick() {
            parent.getNavigation().moveTo(finalPos.x, finalPos.y, finalPos.z, 1);
            if (FlightState != FlightStates.Gliding.ordinal()){
                parent.getLookControl().setLookAt(finalPos);
            }
            if (FlightCD > 0){
                FlightCD--;
            }
            if (CheckFly()){
                StartFlyCycle();
            }
            if (IsFlying){
                FlightManager();
            }
        }

        @Override
        public void stop() {
            ManageStateSwitch(FlightStates.NotFlying);
        }
        /**/

        //General Use Methods
        protected void FlightManager(){
            parent.getLookControl().setLookAt(finalPos);
            switch (FlightState){
                case 2 -> Ascend();
                case 3 -> Glide();
                case 4 ->{
                    if (parent.verticalCollisionBelow){
                        ManageStateSwitch(FlightStates.NotFlying);
                    }
                }
            }
        }

        protected void StartFlyCycle(){
            System.out.println("Starting FlyCycle");
            parent.getLookControl().setLookAt(finalPos);
            ManageStateSwitch(FlightStates.Ascending);
        }
        protected void ManageStateSwitch(FlightStates flightStates){
            if (flightStates.ordinal() != FlightState){
                switch (flightStates.ordinal()){
                    case 0 ->{
                        IsFlying = false;
                        FlightState = -1;
                        ActiveFlightTime = -1;
                        parent.setAIState(state.idle.ordinal());
                        parent.setNoGravity(false);
                    }
                    case 1 -> HandleLand(FlightState);
                    case 2 ->{
                        IsFlying = true;
                        FlightState = 2;
                        ActiveFlightTime = MaxAscensionTime;
                        parent.setAIState(state.flying.ordinal());
                        parent.setNoGravity(true);
                    }
                    case 3 ->{
                        IsFlying = true;
                        FlightState = 3;
                        ActiveFlightTime = MaxGlideTime;
                        parent.setAIState(state.flying.ordinal());
                        parent.setNoGravity(false);
                    }
                    case 4 ->{
                        IsFlying = false;
                        FlightState = 4;
                        ActiveFlightTime = -1;
                        FlightCD = MaxFlightCD;
                        parent.setAIState(state.flying.ordinal());
                        parent.setNoGravity(false);
                    }
                }
            }
        }

        protected void Ascend(){
            if (ActiveFlightTime > 0 && !AmITooHigh(getTarget())){
                double hSpeed = CalculateSpeed();
                Vec3 forwards = parent.getForward().multiply(hSpeed, 0, hSpeed);
                double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
                double ySpeedMax = Mth.abs((float)parent.getDeltaMovement().y) > VFlightSpeed ? parent.getDeltaMovement().y : VFlightSpeed;
                double zSpeedMax = Mth.abs((float)parent.getDeltaMovement().z) > Mth.abs((float)forwards.z) ? parent.getDeltaMovement().z : forwards.z;
                parent.setDeltaMovement(xSpeedMax, ySpeedMax, zSpeedMax);
                ActiveFlightTime--;
            }
            else{
                ManageStateSwitch(FlightStates.Gliding);
            }
        }
        protected void Glide(){
            if (!verticalCollisionBelow){
                if (ActiveFlightTime > 0){
                    double hSpeed = CalculateSpeed();
                    Vec3 forwards = parent.getForward().multiply(hSpeed, 0, hSpeed);
                    double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
                    double ySpeedMax = -VFlightSpeed / 2;
                    double zSpeedMax = Mth.abs((float)parent.getDeltaMovement().z) > Mth.abs((float)forwards.z) ? parent.getDeltaMovement().z : forwards.z;
                    parent.setDeltaMovement(xSpeedMax, ySpeedMax, zSpeedMax);
                    ActiveFlightTime--;
                    parent.resetFallDistance();
                }
                else{
                    ManageStateSwitch(FlightStates.Falling);
                }
            }
            else{
                ManageStateSwitch(FlightStates.NotFlying);
            }
        }
        protected void HandleLand(int priorState){
            IsFlying = false;
            FlightState = 1;
            ActiveFlightTime = MaxAscensionTime;
            FlightCD = MaxFlightCD;
            parent.setAIState(state.idle.ordinal());
            parent.setNoGravity(false);

            if (priorState == FlightStates.Falling.ordinal()){
                causeFallDamage(fallDistance, 1.25f, damageSources().fall());
            }
            else{
                resetFallDistance();
            }
        }

        protected double CalculateSpeed(){
            //Gets the absolute average between the distance of the target and the mob of both X and Z coords
            double AvgDistance2d = Mth.abs((float)((parent.position().x - finalPos.x) * (parent.position().z - finalPos.z))) / 2;
            //Gets the absolute distance between the target and the mob
            double YDistance = Mth.abs((float)(parent.position().y - finalPos.y));
            //Tan = Opposite over Adjacent. Geometry!
            double TanOfParentPoint = AvgDistance2d / YDistance;
            // Divides the Tangent by 100-- turning it into a decimal multipier to be used to convert the final output
            double DividedDifference = 100 / TanOfParentPoint;
            //Returns the smaller number-- if Tan was 90, DividedDifference would return 1.111...1 which is not exactly needed
            double Mulitplier = Math.min(1, DividedDifference);
            //Multiplies the multiper by the speed and returns it
            return HFlightSpeed * Mulitplier;
        }
        /**/
    }
    protected class FlyToHostileTarget extends FlyToGoal{
        public FlyToHostileTarget(MyiaticBase parent, int maxFlightCD, int maxAscensionTime, int maxGlideTime,
                                  int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
            super(parent, parent.getTarget(), maxFlightCD, maxAscensionTime, maxGlideTime,
                    targetHeightThreshold, vFlightSpeed, hFlightSpeed);
        }
        @Override
        boolean WantsToTakeOff() {
            return IsMyTargetTooHigh(parent.getTarget()) || parent.distanceTo(parent.getTarget()) > 20;
        }
        @Override
        boolean CheckFly(){
            return FlightState != FlightStates.Disabled.ordinal() && !IsFlying && FlightCD <= 0 && WantsToTakeOff();
        }

        @Override
        public boolean canUse() {
            if (parent.getTarget() != null){
                super.finalPos = parent.getTarget().position();
            }
            return super.canUse() && parent.getTarget() != null;
        }

        @Override
        public void tick() {
            parent.getNavigation().moveTo(parent.getTarget(), 1);
            if (FlightState != FlightStates.Gliding.ordinal()){
                parent.getLookControl().setLookAt(parent.getTarget());
            }
            if (FlightCD > 0){
                FlightCD--;
            }
            if (CheckFly() && parent.getLookControl().isLookingAtTarget()){
                StartFlyCycle();
            }
            if (IsFlying){
                FlightManager();
                StrikeWhileGliding(parent.getTarget());
            }
        }

        @Override
        protected void HandleLand(int priorState) {
            super.HandleLand(priorState);
            if (parent.getTarget() != null){
                parent.getNavigation().moveTo(parent.getTarget(), 1.0d);
            }
        }

        @Override
        protected void Ascend() {
            super.Ascend();
            CheckThenDropIfClear(parent.getTarget());
        }

        @Override
        protected void Glide() {
            super.Glide();
            CheckThenDropIfClear(parent.getTarget());
        }
        @Override
        protected double CalculateSpeed() {
            if (IsMyTargetTooHigh(parent.getTarget())){
                return super.CalculateSpeed();
            }
            return HFlightSpeed * 1.25;
        }

        //Subclass-Specific Methods
        boolean IsMyTargetTooHigh(LivingEntity target){
            if (target != null){
                System.out.println("Target is this high " + (target.position().y - parent.position().y));
                return target.position().y - parent.position().y > TargetHeightThreshold;
            }
            return false;
        }
        protected void StrikeWhileGliding(LivingEntity target){
            if (target != null){
                double range = parent.getBbWidth() * 2.0F * parent.getBbWidth() * 2.0F + target.getBbWidth();
                if (parent.distanceTo(target) < range){
                    parent.doHurtTarget(target);
                }
            }
        }
        protected boolean CheckThenDropIfClear(LivingEntity target){
            if (target != null){
                double distanceV = parent.position().y - target.position().y;
                if (distanceV > 0 && distanceV < TargetHeightThreshold){
                    float distance2d = new Vec2((float)parent.position().x, (float)parent.position().z).distanceToSqr(new Vec2((float)target.position().x, (float)target.position().z));
                    if ((Mth.sqrt(distance2d) < 1.5) || IsThereABlockUnderMe(TargetHeightThreshold) && Mth.sqrt(distance2d) < 3){
                        ManageStateSwitch(FlightStates.NotFlying);
                        parent.setNoGravity(false);
                        parent.setDeltaMovement(0, 0, 0);
                        return true;
                    }
                }
            }
            return false;
        }
    }
    protected class PleaseDontBreakMyLegs extends FlyToGoal{
        public PleaseDontBreakMyLegs(MyiaticBase parent, int maxGlideTime, int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
            super(parent, parent.position().add(parent.getForward().multiply(hFlightSpeed, 0, hFlightSpeed)), 0, 0, maxGlideTime, targetHeightThreshold, vFlightSpeed, hFlightSpeed);
        }

        @Override
        public boolean canUse() {
            return !IsFlying && FlightState != FlightStates.Falling.ordinal() && parent.fallDistance > parent.getMaxFallDistance();
        }

        @Override
        public boolean canContinueToUse() {
            return FlightState != FlightStates.NotFlying.ordinal();
        }

        @Override
        boolean WantsToTakeOff() {
            return FlightState == FlightStates.NotFlying.ordinal() && parent.fallDistance > parent.getMaxFallDistance();
        }

        @Override
        protected void StartFlyCycle() {
            parent.getLookControl().setLookAt(finalPos);
            ManageStateSwitch(FlightStates.Gliding);
        }

        @Override
        protected void Glide() {
            if (!verticalCollisionBelow){
                if (ActiveFlightTime > 0){
                    double hSpeed = CalculateSpeed();
                    Vec3 forwards = parent.getForward().multiply(hSpeed, 0, hSpeed);
                    double xSpeedMax = Mth.abs((float)parent.getDeltaMovement().x) > Mth.abs((float)forwards.x) ? parent.getDeltaMovement().x : forwards.x;
                    double ySpeedMax = Math.min(parent.getDeltaMovement().y / 2, -VFlightSpeed * 4);
                    double zSpeedMax = Mth.abs((float)parent.getDeltaMovement().z) > Mth.abs((float)forwards.z) ? parent.getDeltaMovement().z : forwards.z;
                    parent.setDeltaMovement(xSpeedMax, ySpeedMax, zSpeedMax);
                    ActiveFlightTime--;
                    parent.resetFallDistance();
                }
                else{
                    ManageStateSwitch(FlightStates.Falling);
                }
            }
            else{
                ManageStateSwitch(FlightStates.NotFlying);
            }
        }

        @Override
        protected double CalculateSpeed() {
            return HFlightSpeed;
        }
    }
    protected class GlideDownToFoes extends FlyToHostileTarget{
        public GlideDownToFoes(MyiaticBase parent, int maxGlideTime, int targetHeightThreshold, double vFlightSpeed, double hFlightSpeed) {
            super(parent, 0, 0, maxGlideTime, targetHeightThreshold, vFlightSpeed, hFlightSpeed);
        }

        @Override
        boolean WantsToTakeOff() {
            return FlightState != FlightStates.Disabled.ordinal() && parent.getTarget().blockPosition().getY() + 3 < parent.blockPosition().getY() && !IsFlying && FlightCD == 0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && parent.distanceTo(parent.getTarget()) < 10;
        }

        @Override
        protected void StartFlyCycle() {
            parent.getLookControl().setLookAt(parent.getTarget());
            ManageStateSwitch(FlightStates.Gliding);
        }
    }
    /**/
}
