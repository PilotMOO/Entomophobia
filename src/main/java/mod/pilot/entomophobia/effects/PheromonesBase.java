package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public abstract class PheromonesBase extends MobEffect {
    public PheromonesBase(int color, boolean spread, boolean nonApplic, boolean myiaticApplic, int MyiaticSpread, int BaseSpread, int falloff) {
        super(MobEffectCategory.HARMFUL, color);
        CanSpread = spread;
        ApplicableToNon = nonApplic;
        ApplicableToMyiatic = myiaticApplic;
        MyiaticSpreadAOE = MyiaticSpread;
        BaseSpreadAOE = BaseSpread;
        Falloff = falloff;
    }

    public boolean CanSpread;
    public boolean ApplicableToNon;
    public boolean ApplicableToMyiatic;
    public int MyiaticSpreadAOE;
    public int BaseSpreadAOE;
    public int Falloff;

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        System.out.println("I'm going to hurt you");
        if (pLivingEntity instanceof MyiaticBase){
            if (!ApplicableToMyiatic){
                pLivingEntity.removeEffect(this);
            }
            else{
                MyiaticEffectTick(pLivingEntity, pAmplifier);
            }
        }
        else{
            if (!ApplicableToNon){
                pLivingEntity.removeEffect(this);
            }
            else{
                BaseEffectTick(pLivingEntity, pAmplifier);
            }
        }

        if (CanSpread){
            AttemptToSpreadToNearbyTargets(pLivingEntity.blockPosition(), pAmplifier, pLivingEntity);
        }
    }

    protected void MyiaticEffectTick(LivingEntity target, int amp) {}
    protected void BaseEffectTick(LivingEntity target, int amp) {}

    protected void AttemptToSpreadToNearbyTargets(BlockPos blockPos, int pAmplifier, LivingEntity origin) {
        AABB MyiaticAABB = new AABB(blockPos).inflate(MyiaticSpreadAOE);
        AABB NonMyiaticAABB = new AABB(blockPos).inflate(BaseSpreadAOE);

        MobEffectInstance originEffect = origin.getEffect(this);
        if (originEffect != null){
            for (LivingEntity entity : origin.level().getEntitiesOfClass(MyiaticBase.class, MyiaticAABB)){
                double duration = originEffect.getDuration();
                duration = duration * (((MyiaticSpreadAOE - entity.distanceToSqr(blockPos.getCenter())) / MyiaticSpreadAOE) * Falloff);
                entity.addEffect(new MobEffectInstance(this, (int)duration, pAmplifier));
            }
            for (LivingEntity entity : origin.level().getEntitiesOfClass(LivingEntity.class, NonMyiaticAABB)){
                if (!(entity instanceof MyiaticBase)){
                    double duration = originEffect.getDuration();
                    duration = duration * (((BaseSpreadAOE - entity.distanceToSqr(blockPos.getCenter())) / BaseSpreadAOE) * Falloff);
                    entity.addEffect(new MobEffectInstance(this, (int)duration, pAmplifier));
                }
            }
        }
    }
}
