package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.worlddata.EntomoDataManager;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Myiasis extends MobEffect {
    public Myiasis() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    private static HashMap<Entity, Integer> RotMashmap = new HashMap<>();

    private static int infectedDuration(LivingEntity target) {
        var check = RotMashmap.get(target);
        if (check != null){
            return check;
        }
        return -1;
    }
    private static void RotFor(LivingEntity target){
        RotMashmap.replace(target, infectedDuration(target) + 1);
    }
    private static void StartRot(LivingEntity target){
        RotMashmap.put(target, 0);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        if (!(target instanceof MyiaticBase || target instanceof Player) && EntomoDataManager.GetConvertedFor(target) != null){
            if (infectedDuration(target) == -1){
                StartRot(target);
            }
            MobEffectInstance effect = target.getEffect(this);
            assert effect != null;
            if (infectedDuration(target) < Config.SERVER.myiatic_convert_timer.get()){
                if(target.isDeadOrDying()){
                    if (!effect.isInfiniteDuration()){
                        target.addEffect(new MobEffectInstance(this, -1));
                    }
                    target.deathTime--;
                    target.setInvulnerable(true);
                    if (target instanceof Mob){
                        ((Mob)target).setNoAi(true);
                    }
                    RotFor(target);
                    target.aiStep();

                    BlockPos blockPos = target.blockPosition();
                    if (target.level().getBlockState(blockPos).isAir()){
                        BlockPos newPos = new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());
                        int cycleCounter = 0;
                        while (target.level().getBlockState(newPos).isAir() && cycleCounter < 384){
                            newPos = new BlockPos(newPos.getX(), newPos.getY() - 1, newPos.getZ());
                            cycleCounter++;
                        }
                        if (cycleCounter >= 384){
                            newPos = null;
                        }
                        if (newPos != null){
                            target.setPos(new Vec3(target.getX(), newPos.getY() + 1, target.getZ()));
                        }
                    }
                }
                else{
                    TickDamage(target, amp);
                }
            }
            else{
                ConvertMob(target);
            }
        }
        else if(target instanceof Player){
            TickDamage(target, amp);
        }
        else{
            target.removeEffect(this);
        }
    }

    private void TickDamage(LivingEntity target, int amp) {
        if (amp > 0 && target.tickCount % (600 / amp) == 0){
            target.hurt(EntomoDamageTypes.myiasis(target), amp);
        }
    }

    private void ConvertMob(LivingEntity target) {
        EntityType<?> EType = EntomoDataManager.GetConvertedFor(target);

        Entity newEntity = EType.create(target.level());
        newEntity.copyPosition(target);
        target.level().addFreshEntity(newEntity);
        target.level().playSound(target, target.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0f, 1.25f);

        RotMashmap.remove(target);
        target.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration > 0;
    }
}
