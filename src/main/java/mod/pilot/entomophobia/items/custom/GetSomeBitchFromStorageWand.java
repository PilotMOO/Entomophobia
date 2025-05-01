package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

public class GetSomeBitchFromStorageWand extends Item {
    public GetSomeBitchFromStorageWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(@NotNull UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel server)) return InteractionResult.PASS;

        Player player = pContext.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Vec3 pos = pContext.getClickLocation();
        Pair<HiveSaveData.Packet, HiveHeartEntity> pair = HiveSaveData.locateClosestDataAndAccessor(pos);
        HiveSaveData.Packet packet = pair.getA();
        if (packet == null){
            printToPlayer(player, "Failed to locate packet");
            return InteractionResult.PASS;
        }

        LivingEntity entity = packet.getAnythingFromStorage(server);
        if (entity == null){
            printToPlayer(player, "Failed to get something from storage for some fucking reason");
            return InteractionResult.PASS;
        }
        entity.setPos(pos);
        server.addFreshEntity(entity);
        printToPlayer(player, "huzz [" + entity.getEncodeId() + "], amount left: " + packet.getCountInStorage(entity.getEncodeId()));
        packet.thenSync(server);

        return super.useOn(pContext);
    }

    private void printToPlayer(Player player, String string){
        player.displayClientMessage(Component.literal(string), true);
    }
}
