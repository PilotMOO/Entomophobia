package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.EntomoDataManager;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
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
        if (!(target instanceof MyiaticBase || target instanceof PheromonesEntityBase || target instanceof Player) && EntomoDataManager.GetConvertedFor(target) != null){
            if (infectedDuration(target) == -1){
                StartRot(target);
            }
            MobEffectInstance effect = target.getEffect(this);
            assert effect != null;
            if (infectedDuration(target) < Config.SERVER.myiatic_convert_timer.get()){
                if(target.getHealth() <= 0){
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
                }
            }
            else{
                ConvertMob(target);
            }
        }
        else if(target instanceof Player){

        }
        else{
            target.removeEffect(this);
        }
    }

    private void ConvertMob(LivingEntity target) {
        EntityType<?> EType = EntomoDataManager.GetConvertedFor(target);

        assert EType != null;
        Entity newEntity = EType.create(target.level());
        newEntity.copyPosition(target);
        target.level().addFreshEntity(newEntity);
        target.level().playSound(target, target.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0f, 1.0f);

        RotMashmap.remove(target);
        target.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration > 0;
    }
}
