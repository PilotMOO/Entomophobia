package mod.pilot.entomophobia.entity.pheromones;

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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class PheromonesEntityBase extends PathfinderMob {
    public PheromonesEntityBase(EntityType<? extends PathfinderMob> pEntityType, Level pLevel, MobEffect BaseEffect, MobEffect MyiaticEffect,
                                int MSpread, int BSpread, int Timer, int amp, int life) {
        super(pEntityType, pLevel);
        MyiaticPheromoneType = MyiaticEffect;
        BasePheromoneType = BaseEffect;
        MyiaticSpreadAOE = MSpread;
        BaseSpreadAOE = BSpread;
        EffectBaseTimer = Timer;
        EffectAmp = amp;
        LifeMax = life;
    }

    //NBT and Variables
    public static MobEffect MyiaticPheromoneType;
    public static MobEffect BasePheromoneType;
    public int MyiaticSpreadAOE = 0;
    public int BaseSpreadAOE = 0;
    public static int EffectBaseTimer = 0;
    public int EffectAmp = 0;
    public int LifeMax;
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

        for (LivingEntity entity : this.level().getEntitiesOfClass(MyiaticBase.class, MyiaticAABB)){
            if (!entity.hasEffect(MyiaticPheromoneType)){
                if (entity.addEffect(new MobEffectInstance(MyiaticPheromoneType,  (int)(EffectBaseTimer * (MyiaticSpreadAOE - entity.distanceToSqr(this)) / MyiaticSpreadAOE), EffectAmp))){
                    AddLife(20);
                }
            }
        }
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, NonMyiaticAABB)){
            if (!(entity instanceof MyiaticBase) && !entity.hasEffect(BasePheromoneType) && entity != this){
                if (entity.addEffect(new MobEffectInstance(BasePheromoneType,  (int)(EffectBaseTimer * (BaseSpreadAOE - entity.distanceToSqr(this)) / BaseSpreadAOE), EffectAmp))){
                    AddLife(20);
                }
            }
        }
    }

    //Stolen from Harbinger, thanks harby :]
    protected void SpawnParticles(){
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        Level world = this.level();
        RandomSource randomSource = this.random;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int l = 0; l < 4; ++l) {
            blockpos$mutableblockpos.set(i + Mth.nextInt(randomSource, -6, 6), j + Mth.nextInt(randomSource, -6, 6), k + Mth.nextInt(randomSource, -6, 6));
            BlockState blockstate = world.getBlockState(blockpos$mutableblockpos);
            if (!blockstate.isSolidRender(world, blockpos$mutableblockpos)) {
                world.addParticle(ParticleTypes.ENTITY_EFFECT, (double) blockpos$mutableblockpos.getX() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getY() + randomSource.nextDouble(), (double) blockpos$mutableblockpos.getZ() + randomSource.nextDouble(), 0.0D, 0.1D, 0.0D);
            }
        }
    }
    /**/

    //Overridden Methods
    @Override
    public void aiStep() {
        SpawnParticles();
        AttemptToSpread();
        if (getLife() >= LifeMax){
            this.remove(RemovalReason.DISCARDED);
        }
        else{
            AddLife(1);
        }
        System.out.println("Life is " + getLife());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }
    /**/
}
