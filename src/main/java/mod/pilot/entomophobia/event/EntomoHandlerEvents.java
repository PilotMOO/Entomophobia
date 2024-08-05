package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.EntomoWorldManager;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoHandlerEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        Entity e = event.getEntity();
        if (e instanceof MyiaticBase){
            EntomoWorldManager.AddThisToAllMyiatics((MyiaticBase)e);
        }
        if (e instanceof PheromonesEntityBase){
            EntomoWorldManager.AddThisToAllPheromones((PheromonesEntityBase)e);
        }
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        Entity e = event.getEntity();
        if (e instanceof MyiaticBase){
            EntomoWorldManager.RemoveThisFromAllMyiatics((MyiaticBase)e);
        }
        if (e instanceof PheromonesEntityBase){
            EntomoWorldManager.RemoveThisFromAllPheromones((PheromonesEntityBase)e);
        }
    }
}
