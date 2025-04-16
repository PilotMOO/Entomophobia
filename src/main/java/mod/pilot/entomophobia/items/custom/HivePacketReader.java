package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HivePacketReader extends Item {
    public HivePacketReader(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof HiveHeartEntity HH && !pPlayer.level().isClientSide()){
            HiveSaveData.Packet packet = HH.accessData();
            System.out.println("------");
            System.out.println("HiveSaveData Packet for the given Hive Heart: " + packet);
            if (packet != null) {
                System.out.println("Packet info:");
                System.out.println("Corpsedew-- " + packet.corpseDew);
                System.out.println("Saved entities [");
                for (String s : packet.storedEntities.keySet()){
                    System.out.println("Entity " + s + "; count: " + packet.getCountInStorage(s));
                }
                System.out.println("]");
            }
            System.out.println("------");
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
