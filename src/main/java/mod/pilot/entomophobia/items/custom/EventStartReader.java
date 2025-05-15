package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.EventStart.EventStart;
import net.minecraft.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventStartReader extends Item {
    private static final Logger log = LoggerFactory.getLogger(EventStartReader.class);

    public EventStartReader(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        boolean clientSide = player.level().isClientSide();
        if (!clientSide){
            long start = Util.getNanos();
            while (Util.getNanos() - start < 10000000L){
                continue;
            }
        }

        System.err.println("[ACCESSING EVENT START DATA]");
        System.out.println("Accessing from logical [" + (clientSide ? "CLIENT" : "SERVER") + "]");
        System.out.println("Is over? " + EventStart.eventOver);
        System.out.println("Has started? " + EventStart.eventStarted);
        System.out.println("Fade timer: [" + EventStart.fade + "]");
        System.out.println("Fade State: [" + EventStart.fadeState.name() + "]");
        System.err.println("[DATA ACCESSED]");

        return super.use(level, player, pUsedHand);
    }
}
