package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HiveHeartReader extends Item {
    public HiveHeartReader(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof HiveHeartEntity HH){
            boolean clientSide = pPlayer.level().isClientSide();
            if (!clientSide){
                long start = Util.getNanos();
                while (Util.getNanos() - start < 700000L){
                    continue;
                }
            }
            HiveSaveData.Packet packet = HH.accessData();
            System.out.println("------");
            System.out.println("[ACCESSING HIVE HEART...]");
            System.out.println("[ACCESSING FROM LOGICAL " + (clientSide ? "CLIENT" : "SERVER") + "]");
            System.out.println("Has nervous system? " + (HH.nervousSystem != null));
            if (HH.nervousSystem != null){
                System.out.println("Nervous system details:");
                System.out.println("UUID of hive heart: [" + HH.nervousSystem.hiveHeartUUID + "]");
                System.out.println("associated nest: [" + HH.nervousSystem.nest + "]");
                System.out.println("ServerLevel: [" + HH.nervousSystem.serverLevel + "]");
            }
            System.out.println("[END SECTION]");
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
            System.out.println("[END SECTION]");
            System.out.println("------");
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
