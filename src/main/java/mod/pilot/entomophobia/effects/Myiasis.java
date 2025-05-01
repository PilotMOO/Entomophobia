package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.particles.EntomoParticles;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Myiasis extends MobEffect implements IStackingEffect {
    public Myiasis() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    private static final HashMap<Entity, Integer> rotMashmap = new HashMap<>();

    private static int infectedDuration(LivingEntity target) {
        return rotMashmap.getOrDefault(target, -1);
    }
    private static void rot(LivingEntity target){
        rotMashmap.replace(target, infectedDuration(target) + 1);
    }
    private static void startRot(LivingEntity target){
        rotMashmap.put(target, 0);
    }

    private static final int convertTime = Config.SERVER.myiatic_convert_timer.get();
    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        if (!(target instanceof MyiaticBase || target instanceof Player) && EntomoDataManager.getConvertedFor(target) != null){
            if (infectedDuration(target) == -1){
                startRot(target);
            }
            MobEffectInstance effect = target.getEffect(this);
            assert effect != null;
            if (infectedDuration(target) < convertTime){
                if(target.isDeadOrDying()){
                    if (!effect.isInfiniteDuration()){
                        target.addEffect(new MobEffectInstance(this, -1));
                    }
                    target.deathTime--;
                    target.setInvulnerable(true);
                    if (target instanceof Mob){
                        ((Mob)target).setNoAi(true);
                    }
                    rot(target);
                    target.aiStep();

                    BlockPos blockPos = target.blockPosition();
                    if (target.level().getBlockState(blockPos).isAir()){
                        BlockPos newPos = blockPos.below();
                        int cycleCounter = 0;
                        while (target.level().getBlockState(newPos).isAir() && cycleCounter < 384){
                            newPos = newPos.below();
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
                    tickDamage(target, amp);
                }
            }
            else{
                convertMob(target);
            }
        }
        else if(target instanceof Player){
            tickDamage(target, amp);
        }
        else{
            target.removeEffect(this);
        }

        if (target.level() instanceof ServerLevel server){
            if (target.tickCount % 140 == 0){
                RandomSource random = target.getRandom();
                double xOffset = random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25;
                double yOffset = target.getBbHeight() / 2 + random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25;
                double zOffset = random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25;

                server.sendParticles(EntomoParticles.FLY_PARTICLE.get(), target.getX(), target.getY(), target.getZ(),
                        random.nextInt(2, 7),
                        xOffset, yOffset, zOffset, 0);
            }
        }
    }

    private void tickDamage(LivingEntity target, int amp) {
        if (amp > 0 && target.tickCount % (300 / amp) == 0){
            target.hurt(EntomoDamageTypes.myiasis(target), amp);
        }
    }

    private void convertMob(LivingEntity target) {
        EntityType<?> EType = EntomoDataManager.getConvertedFor(target);

        Entity newEntity = EType.create(target.level());
        assert newEntity != null;
        newEntity.copyPosition(target);
        target.level().addFreshEntity(newEntity);
        target.level().playSound(null, target.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0f, 1.25f);

        if (newEntity instanceof LivingEntity le){
            HiveSaveData.Packet packet = HiveSaveData.locateClosestDataAndAccessor(le.position()).getA();
            if (packet != null && target.level() instanceof ServerLevel server){
                packet.registerAsUnlockedEntity(le).thenSync(server);
            }
        }
        if (newEntity instanceof MyiaticBase M && M.canSwarm()){
            Swarm closest = SwarmManager.getClosestSwarm(M.position());
            if (closest != null) {
                M.tryToRecruit(closest);
            }
        }

        rotMashmap.remove(target);
        target.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration > 0;
    }


    @Override
    public int getWrapAroundThreshold() {
        return 1200;
    }

    @Override
    public int getMinimumWrapDuration() {
        return 80;
    }
    @Override
    public boolean isDegradable() {
        return true;
    }
}
