package mod.pilot.entomophobia.data.clientsyncing;

import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class ArteryClientSyncer {

    public static void request(HiveHeartEntity hh) {
        request(hh.getUUID());
    }

    public static void request(UUID id) {
        ClientRequestPacket requestPacket = new ClientRequestPacket(id);
        EntomoPacketSyncer.sendToServer(requestPacket);
    }

    public static void sync(HiveHeartEntity hh, ServerPlayer player) {
        ServerSyncPacket syncPacket = new ServerSyncPacket(hh);
        EntomoPacketSyncer.sendToClient(syncPacket, player);
    }

    public record ServerSyncPacket(UUID id, ArrayList<HiveHeartEntity.Artery> arteries) {
        public ServerSyncPacket(HiveHeartEntity hh) {
            this(hh.getUUID(), hh.getOrCreateArteryHooks());
        }
        public static ServerSyncPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            UUID id = buffer.readUUID();
            int size = buffer.readInt();
            ArrayList<HiveHeartEntity.Artery> arteries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                double x = buffer.readDouble();
                double y = buffer.readDouble();
                double z = buffer.readDouble();
                float base = buffer.readFloat();
                float tip = buffer.readFloat();
                arteries.add(new HiveHeartEntity.Artery(new Vec3(x, y, z), base, tip));
            }
            return new ServerSyncPacket(id, arteries);
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(id);
            buffer.writeInt(arteries.size());
            for (HiveHeartEntity.Artery a : arteries) {
                buffer.writeDouble(a.position.x);
                buffer.writeDouble(a.position.y);
                buffer.writeDouble(a.position.z);
                buffer.writeFloat(a.baseThickness);
                buffer.writeFloat(a.tipThickness);
            }
        }

        public static void sync(ServerSyncPacket serverSyncPacket, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                LogicalSide side = context.get().getDirection().getReceptionSide();
                if (side.isClient()){
                    middleMan.put(serverSyncPacket.id, serverSyncPacket);
                } else if (side.isServer()) {
                    postError("[SYNC] Sync packet was invoked on the logical server! Very cringe.");
                } else postError("[SYNC] Something has gone HORRIBLY wrong and a packet is neither on the client nor a server???");
            });
            context.get().setPacketHandled(true);
        }
    }

    public record ClientRequestPacket(UUID id) {
        public static ClientRequestPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            return new ClientRequestPacket(buffer.readUUID());
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(id);
        }
        public static void postRequest(ClientRequestPacket packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ServerPlayer requester = context.get().getSender();
                if (requester == null) {
                    postError("[REQUEST] Packet was invalid because requester was null");
                    return;
                }

                ServerLevel level = requester.serverLevel();
                Entity entity = level.getEntity(packet.id);
                if (entity == null) {
                    postError("[REQUEST] Entity was null! Maybe it is unloaded or does not exist?");
                    return;
                }
                if (!(entity instanceof HiveHeartEntity hh)) {
                    postError("[REQUEST] Packet was invalid because contained entity UUID was NOT a HiveHeartEntity");
                    return;
                }
                sync(hh, requester);
            });
            context.get().setPacketHandled(true);
        }
    }

    private static void postError(String reason) {
        System.err.println("[ARTERY CLIENT SYNCER] ERROR! Invalid packet detected! Bailing...");
        System.err.println("[ARTERY CLIENT SYNCER] Info -- Cause of invalidity: " + reason);
    }

    //Behold, the man.
    @OnlyIn(Dist.CLIENT)
    private static final HashMap<UUID, ServerSyncPacket> middleMan = new HashMap<>();
    @OnlyIn(Dist.CLIENT)
    public static @Nullable ServerSyncPacket retrieveFromMiddleMan(UUID id){
        return middleMan.getOrDefault(id, null);
    }
    @OnlyIn(Dist.CLIENT)
    public static void checkMiddleMan(HiveHeartEntity hh){
        ServerSyncPacket packet = retrieveFromMiddleMan(hh.getUUID());
        if (packet != null) {
            hh.setArteries(packet.arteries);
            middleMan.remove(hh.getUUID());
        }
    }
}
