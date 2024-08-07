package mod.pilot.entomophobia.event;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.worlddata.WorldSaveData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Entomophobia.MOD_ID)
public class EntomoHandlerEvents {
    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {

    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event){
        Entity E = event.getEntity();
        if (E instanceof MyiaticBase && event.getLevel() instanceof ServerLevel server){
            System.out.println("Adding " + E.getEncodeId() + " to storage!");
            WorldSaveData.AddToStorage(server, E.getEncodeId());
        }
    }
}
