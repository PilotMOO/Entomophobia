package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.StackingEffectBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.worlddata.WorldSaveData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoHandlerEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof MyiaticBase && event.getLevel() instanceof ServerLevel){
            Entomophobia.activeData.AddToMyiaticCount();
            System.out.println("MyiaticCount is " + Entomophobia.activeData.GetMyiaticCount());
        }
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof MyiaticBase M && M.level() instanceof ServerLevel){
            Entomophobia.activeData.RemoveFromMyiaticCount();
            System.out.println("MyiaticCount is " + Entomophobia.activeData.GetMyiaticCount());
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        Entity E = event.getEntity();
        if (E instanceof MyiaticBase M && !M.isDeadOrDying() && event.getLevel() instanceof ServerLevel){
            System.out.println("Adding " + M.getEncodeId() + " to storage!");
            Entomophobia.activeData.AddToStorage(M.getEncodeId());
            Entomophobia.activeData.RemoveFromMyiaticCount();
            System.out.println("MyiaticCount is " + Entomophobia.activeData.GetMyiaticCount());
        }
    }

    @SubscribeEvent
    public static void ServerStart(ServerStartedEvent event){
        WorldSaveData.SetActiveData(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void PotionApplication(MobEffectEvent.Added event){
        MobEffectInstance oldEffect = event.getOldEffectInstance();
        MobEffectInstance newEffect = event.getEffectInstance();
        if (oldEffect != null && oldEffect.getEffect() instanceof StackingEffectBase stacking){
            LivingEntity target = event.getEntity();
            int CumulativeDuration = oldEffect.getDuration() + newEffect.getDuration();
            int amp = (int)Mth.absMax(oldEffect.getAmplifier(), newEffect.getAmplifier());

            target.removeEffect(oldEffect.getEffect());
            target.addEffect(new MobEffectInstance(oldEffect.getEffect(), CumulativeDuration, amp));
        }
    }
}
