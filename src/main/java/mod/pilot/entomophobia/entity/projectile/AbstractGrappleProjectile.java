package mod.pilot.entomophobia.entity.projectile;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.worlddata.EntomoDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractGrappleProjectile extends AbstractArrow {
    protected AbstractGrappleProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel, boolean canGrappleEntities, boolean canGrappleBlocks) {
        super(pEntityType, pLevel);
        this.canGrappleEntities = canGrappleEntities;
        this.canGrappleBlocks = canGrappleBlocks;
    }

    public enum GrappledTypes{
        Not,
        Block,
        Entity,
        ReelingIn
    }
    public boolean isOfGrappleType(GrappledTypes type){
        return getGrappledType() == type.ordinal();
    }
    public static final EntityDataAccessor<Boolean> Grappled = SynchedEntityData.defineId(AbstractGrappleProjectile.class, EntityDataSerializers.BOOLEAN);
    public boolean isGrappled(){
        return entityData.get(Grappled);
    }
    public void setGrappled(boolean flag){
        entityData.set(Grappled, flag);
    }
    public static final EntityDataAccessor<Integer> GrappledType = SynchedEntityData.defineId(AbstractGrappleProjectile.class, EntityDataSerializers.INT);
    public int getGrappledType(){
        return entityData.get(GrappledType);
    }
    public void setGrappledType(int state){
        entityData.set(GrappledType, state);
    }
    public void setGrappledType(GrappledTypes state){
        entityData.set(GrappledType, state.ordinal());
    }
    public static final EntityDataAccessor<Optional<UUID>> TargetUUID = SynchedEntityData.defineId(AbstractGrappleProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    public UUID getTargetUUID(){
        return entityData.get(TargetUUID).orElse(null);
    }
    public void setTargetUUID(UUID uuid){
        entityData.set(TargetUUID, Optional.of(uuid));
    }
    @Nullable
    private Entity target;
    public Entity getTarget(){
        if (target == null){
            if (level() instanceof ServerLevel server){
                if (getTargetUUID() != null){
                    return target = server.getEntity(getTargetUUID());
                }
            }
        }
        else{
            if (getTargetUUID() != target.getUUID()){
                setTargetUUID(target.getUUID());
            }
            return target;
        }
        return null;
    }
    public void setTarget(@Nullable Entity target){
        this.target = target;
        if (target != null){
            setTargetUUID(target.getUUID());
        }
    }
    public static final EntityDataAccessor<Vector3f> grappledPos = SynchedEntityData.defineId(AbstractGrappleProjectile.class, EntityDataSerializers.VECTOR3);
    public Vec3 getGrappledPos(){
        Vector3f vec = entityData.get(grappledPos);
        return new Vec3(vec.x, vec.y, vec.z);
    }
    public void setGrappledPos(Vec3 pos){
        entityData.set(grappledPos, pos.toVector3f());
    }
    public static final EntityDataAccessor<Integer> grappleFreshness = SynchedEntityData.defineId(AbstractGrappleProjectile.class, EntityDataSerializers.INT);
    public int getFreshness(){
        return entityData.get(grappleFreshness);
    }
    public void setFreshness(int count){
        entityData.set(grappleFreshness, count);
    }
    public void staleGrapple(int count){
        setFreshness(getFreshness() - count);
    }
    public void staleGrapple(){
        staleGrapple(1);
    }

    public double Strength = 1;
    public final boolean canGrappleEntities;
    public final boolean canGrappleBlocks;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Grappled, false);
        this.entityData.define(GrappledType, GrappledTypes.Not.ordinal());
        this.entityData.define(TargetUUID, Optional.empty());
        this.entityData.define(grappledPos, new Vector3f());
        this.entityData.define(grappleFreshness, 200);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Grappled", isGrappled());
        tag.putInt("GrappledType", getGrappledType());
        if (getTargetUUID() != null){
            tag.putUUID("TargetUUID", getTargetUUID());
        }
        if (getGrappledPos() != null){
            tag.putDouble("blockPosX", getGrappledPos().x);
            tag.putDouble("blockPosY", getGrappledPos().y);
            tag.putDouble("blockPosZ", getGrappledPos().z);
        }
        tag.putInt("grappleFreshness", getFreshness());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(Grappled, tag.getBoolean("Grappled"));
        entityData.set(GrappledType, tag.getInt("GrappledType"));
        if (tag.hasUUID("TargetUUID")){
            setTargetUUID(tag.getUUID("TargetUUID"));
        }
        setGrappledPos(new Vec3(tag.getDouble("blockPosX"), tag.getDouble("blockPosY"), tag.getDouble("blockPosZ")));
        setFreshness(tag.getInt("grappleFreshness"));
    }

    public void DragTargetToParent(double strength){
        Entity parent = getOwner();
        Entity target = getTarget();
        if (parent != null && target != null){
            Vec3 force = EntomoDataManager.GetDirectionFromAToB(target, parent).multiply(strength, strength, strength);
            target.setDeltaMovement(getDeltaMovement().add(force));
        }
    }
    public void DragParentToPos(double strength){
        Entity parent = getOwner();
        Vec3 pos = getGrappledPos();
        if (parent != null && pos != null){
            Vec3 force = EntomoDataManager.GetDirectionFromAToB(parent, pos).multiply(strength, strength, strength);
            parent.setDeltaMovement(getDeltaMovement().add(force));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity parent = getOwner();
        Entity target = hitResult.getEntity();

        setGrappled(true);
        setGrappledType(GrappledTypes.Entity);
        setGrappledPos(target.position());
        setTarget(target);
        if (parent instanceof Mob mob && target instanceof LivingEntity LTarget){
            if (mob instanceof MyiaticBase MBase){
                if (MBase.TestValidEntity(LTarget)){
                    mob.setTarget(LTarget);
                }
            }
            else{
                mob.setTarget(LTarget);
            }
        }
        setDeltaMovement(0, 0, 0);
        setPosToGrapple();
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHit) {
        super.onHitBlock(blockHit);
        setGrappled(true);
        setGrappledType(GrappledTypes.Block);
        setGrappledPos(blockHit.getLocation());
    }
    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        return !isGrappled() && target != getOwner();
    }

    @Override
    public void tick() {
        super.tick();
        if (getOwner() != null){
            staleGrapple();
            if (getFreshness() > 0 && !isOfGrappleType(GrappledTypes.ReelingIn)){
                setPosToGrapple();
                if (isGrappled() && getOwner() instanceof LivingEntity LE){
                    if (isOfGrappleType(GrappledTypes.Entity) && getTarget() != null){
                        if (!LE.hasLineOfSight(getTarget())){
                            staleGrapple();
                        }
                        DragTargetToParent(Strength);
                        if (LE.distanceTo(getTarget()) < 2){
                            StopGrappling();
                        }
                    }
                    if (isOfGrappleType(GrappledTypes.Block)){
                        if (LE.level().clip(new ClipContext(new Vec3(getX(), getEyeY(), getZ()), getGrappledPos(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS){
                            staleGrapple();
                        }
                        DragParentToPos(Strength);
                        if (Mth.sqrt((float)LE.distanceToSqr(getGrappledPos())) < 2){
                            StopGrappling();
                        }
                    }
                }

                setNoGravity(distanceTo(getOwner()) < 30);
            }
            else{
                StopGrappling();
            }
        }
        else{
            this.discard();
        }
    }
    protected void setPosToGrapple(){
        if (isOfGrappleType(GrappledTypes.Entity) && getTarget() != null){
            setPos(getTarget().position().add(0, getTarget().getBbHeight() / 2, 0));
        }
        if (isOfGrappleType(GrappledTypes.Block)){
            setPos(getGrappledPos());
        }
    }

    public void StopGrappling() {
        setGrappled(false);
        setGrappledType(GrappledTypes.ReelingIn);
        setTarget(null);
        setNoPhysics(true);
        Entity parent = getOwner();
        if ((parent != null && this.distanceTo(getOwner()) > 2) && getFreshness() > -200){
            Vec3 directionTo = parent.position().subtract(position()).normalize();
            this.setDeltaMovement(directionTo.multiply(Strength, Strength, Strength));
        }
        else{
            this.discard();
        }
    }

    public void shoot(Vec3 direction, float pVelocity, float pInaccuracy, Entity parent, double strength, int freshness) {
        setOwner(parent);
        setPos(parent.position().add(0, 0.5, 0));
        Strength = strength;
        setFreshness(freshness);

        super.shoot(direction.x, direction.y, direction.z, pVelocity, pInaccuracy);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isOfGrappleType(GrappledTypes.Not);
    }
    @Override
    protected float getWaterInertia() {
        return 1.0f;
    }
}
