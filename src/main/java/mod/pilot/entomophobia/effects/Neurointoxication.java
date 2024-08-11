package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.StackingEffectBase;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import oshi.util.tuples.Pair;

import java.util.HashMap;

public class Neurointoxication extends StackingEffectBase {
    public Neurointoxication() {
        super(MobEffectCategory.HARMFUL, -8182798);
    }

    private static HashMap<LivingEntity, Pair<Float, Float>> HeadRotMap = new HashMap<>();
    private static Pair<Float, Float> PriorHeadRotFor(LivingEntity target) {
        if (HeadRotMap.containsKey(target)){
            return HeadRotMap.get(target);
        }
        return null;
    }
    private static void NewHeadRotFor(LivingEntity target, float newYRot, float newXRot){
        if (HeadRotMap.containsKey(target)){
            HeadRotMap.replace(target, new Pair<>(newYRot, newXRot));
        }
        else{
            HeadRotMap.put(target, new Pair<>(newYRot, newXRot));
        }
    }
    public static void RemoveTargetFromHeadRotMap(LivingEntity target){
        HeadRotMap.remove(target);
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amp){
        if (target.tickCount % 20 == 0){
            target.hurt(EntomoDamageTypes.neuro(target), amp * 2);
        }
        if (target.getDeltaMovement().y > 0){
            target.setDeltaMovement(target.getDeltaMovement().multiply(0, 0.5, 0));
        }
        /*
        if (PriorHeadRotFor(target) != null){
            target.setYRot(PriorHeadRotFor(target).getA());
            target.setXRot(PriorHeadRotFor(target).getB());
        }
        NewHeadRotFor(target, target.getYRot(), target.getXRot());
        */

        /*
        Pair<Float, Float> oldViewPair = PriorHeadRotFor(target);
        if (oldViewPair != null){
            NewHeadRotFor(target, (float) (target.getYRot() - ((target.getYRot() - oldViewPair.getA()) * 0.8)), (float) (target.getXRot() - ((target.getXRot() - oldViewPair.getB()) * 0.8)));
            target.setYRot(target.getYRot() - ((target.getYRot() - oldViewPair.getA()) * 0.8) != 0 && Mth.abs((float) (target.getYRot() - ((target.getYRot() - oldViewPair.getA()) * 0.8))) > 180 ? (float) (target.getYRot() - ((target.getYRot() - oldViewPair.getA()) * 0.8)) : target.getYRot());
            target.setXRot(target.getXRot() - ((target.getXRot() - oldViewPair.getB()) * 0.8) != 0 && Mth.abs((float)(target.getXRot() - ((target.getXRot() - oldViewPair.getB()) * 0.8))) > 180 ? (float) (target.getXRot() - ((target.getXRot() - oldViewPair.getB()) * 0.8)) : target.getXRot());
        }
        else{
            NewHeadRotFor(target, target.getYRot(), target.getXRot());
        }
         */
    }
}
