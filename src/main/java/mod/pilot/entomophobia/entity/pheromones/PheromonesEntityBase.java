package mod.pilot.entomophobia.entity.pheromones;

import mod.pilot.entomophobia.effects.PheromonesBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class PheromonesEntityBase extends PathfinderMob {
    public PheromonesEntityBase(EntityType<? extends PathfinderMob> pEntityType, Level pLevel, PheromonesBase BaseEffect, PheromonesBase MyiaticEffect,
                                int MSpread, int BSpread, int Timer, int amp, int life, double falloff) {
        super(pEntityType, pLevel);
        MyiaticPheromoneType = MyiaticEffect;
        BasePheromoneType = BaseEffect;
        MyiaticSpreadAOE = MSpread;
        BaseSpreadAOE = BSpread;
        EffectBaseTimer = Timer;
        EffectAmp = amp;
        LifeMax = life;
        Falloff = falloff;
    }

    //NBT and Variables
    public static PheromonesBase MyiaticPheromoneType;
    public static PheromonesBase BasePheromoneType;
    public int MyiaticSpreadAOE;
    public int BaseSpreadAOE;
    public static int EffectBaseTimer = 0;
    public int EffectAmp;
    public int LifeMax;
    public double Falloff;
    public static final EntityDataAccessor<Integer> Life = SynchedEntityData.defineId(PheromonesEntityBase.class, EntityDataSerializers.INT);
    public int getLife(){return entityData.get(Life);}
    public void setLife(Integer count) {entityData.set(Life, count);}
    public void SubtractLife(int count){entityData.set(Life, getLife() - count);}
    public void AddLife(int count){entityData.set(Life, getLife() + count);}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", entityData.get(Life));
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(Life, tag.getInt("Life"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Life, 0);
    }
    /**/

    //Methods
    protected void AttemptToSpread() {
        AABB MyiaticAABB = new AABB(blockPosition()).inflate(MyiaticSpreadAOE);
        AABB NonMyiaticAABB = new AABB(blockPosition()).inflate(BaseSpreadAOE);

        if (MyiaticPheromoneType != null){
            for (LivingEntity entity : this.level().getEntitiesOfClass(MyiaticBase.class, MyiaticAABB, (M) -> !M.hasEffect(MyiaticPheromoneType))){
                entity.addEffect(new MobEffectInstance(MyiaticPheromoneType, (int)(EffectBaseTimer * ((BaseSpreadAOE - entity.distanceTo(this)) / BaseSpreadAOE) * Falloff), EffectAmp));
                AddLife(20);
            }
        }
        if (BasePheromoneType != null){
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, NonMyiaticAABB, (E) -> {return !(E instanceof MyiaticBase) && !(E instanceof PheromonesEntityBase) && !E.hasEffect(BasePheromoneType);})){
                entity.addEffect(new MobEffectInstance(BasePheromoneType, (int)(EffectBaseTimer * ((BaseSpreadAOE - entity.distanceTo(this)) / BaseSpreadAOE) * Falloff), EffectAmp));
                AddLife(20);
            }
        }
    }

    //Stolen from Harbinger, thanks harby :]
    protected void SpawnParticles(){
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        Level world = this.level();
        int Mcolor = 0;
        if (MyiaticPheromoneType != null){
            Mcolor = MyiaticPheromoneType.getColor();
        }
        double m1 = (float)(Mcolor >> 16 & 255) / 255.0F;
        double m2 = (float)(Mcolor >> 8 & 255) / 255.0F;
        double m3 = (float)(Mcolor & 255) / 255.0F;
        int Bcolor = 0;
        if (BasePheromoneType != null){
            Bcolor = BasePheromoneType.getColor();
        }
        double b1 = (float)(Bcolor >> 16 & 255) / 255.0F;
        double b2 = (float)(Bcolor >> 8 & 255) / 255.0F;
        double b3 = (float)(Bcolor & 255) / 255.0F;
        RandomSource randomSource = this.random;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        if (Mcolor != 0){
            for (int l = 0; l < MyiaticSpreadAOE / 2; ++l) {
                blockpos$mutableblockpos.set(i + Mth.nextInt(randomSource, -MyiaticSpreadAOE, MyiaticSpreadAOE), j + Mth.nextInt(randomSource, -MyiaticSpreadAOE, MyiaticSpreadAOE), k + Mth.nextInt(randomSource, -MyiaticSpreadAOE, MyiaticSpreadAOE));
                BlockState blockstate = world.getBlockState(blockpos$mutableblockpos);
                if (!blockstate.isSolidRender(world, blockpos$mutableblockpos)) {
                    world.addParticle(ParticleTypes.ENTITY_EFFECT, (double) blockpos$mutableblockpos.getX() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getY() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getZ() + randomSource.nextDouble(), m1, m2, m3);
                }
            }
        }
        if (Bcolor != 0){
            for (int l = 0; l < BaseSpreadAOE / 2; ++l) {
                blockpos$mutableblockpos.set(i + Mth.nextInt(randomSource, -BaseSpreadAOE, BaseSpreadAOE), j + Mth.nextInt(randomSource, -BaseSpreadAOE, BaseSpreadAOE), k + Mth.nextInt(randomSource, -BaseSpreadAOE, BaseSpreadAOE));
                BlockState blockstate = world.getBlockState(blockpos$mutableblockpos);
                if (!blockstate.isSolidRender(world, blockpos$mutableblockpos)) {
                    world.addParticle(ParticleTypes.ENTITY_EFFECT, (double) blockpos$mutableblockpos.getX() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getY() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getZ() + randomSource.nextDouble(), b1, b2, b3);
                }
            }
        }
    }
    /**/

    //Overridden Methods

    @Override
    public void tick() {
        super.tick();
        SpawnParticles();
        AttemptToSpread();
        if (getLife() >= LifeMax){
            this.remove(RemovalReason.DISCARDED);
        }
        else{
            AddLife(1);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }
    /**/

    public static AttributeSupplier.Builder createAttributes(){
        return PheromonesEntityBase.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5D)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.FOLLOW_RANGE, 0)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.ATTACK_SPEED, 0D);
    }
}
