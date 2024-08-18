package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.StackingEffectBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticCowEntity;
import mod.pilot.entomophobia.entity.projectile.AbstractGrappleProjectile;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.worlddata.WorldSaveData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event;
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

    @SubscribeEvent
    public static void MilkTheEvilCow(PlayerInteractEvent.EntityInteract event){
        if (event.getTarget() instanceof MyiaticCowEntity MCow){
            Player player = event.getEntity();
            if (player.getMainHandItem().is(Items.BUCKET)){
                player.level().playSound(MCow, MCow.blockPosition(), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.getMainHandItem().shrink(1);
                player.getInventory().add(new ItemStack(EntomoItems.POISONOUS_MILK.get()));
            }
        }
    }

    @SubscribeEvent
    public static void GrappleImpactManager(ProjectileImpactEvent event){
        Projectile grapple = event.getProjectile() instanceof AbstractGrappleProjectile ? event.getProjectile() : null;
        if (grapple != null){
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.DEFAULT);
        }
    }
}
