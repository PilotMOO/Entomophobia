package mod.pilot.entomophobia.data.clientsyncing;

import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class ArteryClientSyncer {
    private static ServerLevel server(){
        return EntomoForgeEvents.getServer();
    }

    public static void request(HiveHeartEntity hh){
        request(hh.getUUID());
    }
    public static void request(UUID id){
        ClientRequestPacket requestPacket = new ClientRequestPacket(id);
        EntomoPacketSyncer.sendToServer(requestPacket);
    }
    public static void sync(HiveHeartEntity hh, ServerPlayer player){
        ServerSyncPacket syncPacket = new ServerSyncPacket(hh);
        EntomoPacketSyncer.sendToClient(syncPacket, player);
    }

    public static class ServerSyncPacket {
        ArrayList<HiveHeartEntity.Artery> arteries;
        public UUID id;
        private ServerSyncPacket(HiveHeartEntity hh){
            System.out.println("Creating sync packet with UUID ["  + hh.getUUID() + "], is server side? " + (!hh.level().isClientSide));
            arteries = hh.getOrCreateArteryHooks();
            this.id = hh.getUUID();
        }
        public static ServerSyncPacket decodeFromBuffer(FriendlyByteBuf buffer){
            return new ServerSyncPacket(buffer);
        }
        private ServerSyncPacket(FriendlyByteBuf buffer){
            this.id = buffer.readUUID();

            ArrayList<HiveHeartEntity.Artery> toRead = new ArrayList<>();
            int count = buffer.readableBytes();
            final int byteCount = 32; //8 for each double, 4 per the two floats, [8 * 3 + 4 * 2 = 32]
            while ((count -= byteCount) >= 0){
                double x, y, z;
                x = buffer.readDouble();
                y = buffer.readDouble();
                z = buffer.readDouble();
                Vec3 pos = new Vec3(x, y, z);

                float base, tip;
                base = buffer.readFloat();
                tip = buffer.readFloat();
                toRead.add(new HiveHeartEntity.Artery(pos, base, tip));
            }
            System.out.println("Amount of unpacked arteries: " + toRead.size());
            this.arteries = toRead;
        }
        public void writeToBuffer(FriendlyByteBuf buffer){
            buffer.writeUUID(id);
            for (HiveHeartEntity.Artery a : arteries){
                buffer.writeDouble(a.position.x);
                buffer.writeDouble(a.position.y);
                buffer.writeDouble(a.position.z);

                buffer.writeFloat(a.baseThickness);
                buffer.writeFloat(a.tipThickness);
            }
        }

        public static void sync(ServerSyncPacket serverSyncPacket, Supplier<NetworkEvent.Context> context){
            context.get().enqueueWork(() ->{
                Entity entity = server().getEntity(serverSyncPacket.id);
                if (!(entity instanceof HiveHeartEntity hh)){
                    postError("[SYNC] Packet was invalid because contained entity UUID was NOT a HiveHeartEntity");
                    return;
                }
                hh.setArteries(serverSyncPacket.arteries);
                System.out.println("Synced arteries between server and a client! Amount synced: " + serverSyncPacket.arteries.size());
                System.out.println("should be on the client at least--- is it? " + (!hh.level().isClientSide));
            });
            context.get().setPacketHandled(true);
        }
    }
    public static class ClientRequestPacket{
        public UUID id;
        protected ClientRequestPacket(UUID id){
            System.out.println("Creating request packet with UUID ["  + id + "]");
            this.id = id;
        }
        public static ClientRequestPacket decodeFromBuffer(FriendlyByteBuf buffer){
            return new ClientRequestPacket(buffer);
        }
        private ClientRequestPacket(FriendlyByteBuf buffer){
            this.id = buffer.readUUID();
        }
        public void writeToBuffer(FriendlyByteBuf buffer){
            buffer.writeUUID(id);
        }

        public static void postRequest(ClientRequestPacket packet, Supplier<NetworkEvent.Context> context){
            context.get().enqueueWork(() ->{
                ServerPlayer requester = context.get().getSender();
                if (requester == null){
                    postError("[REQUEST] Packet was invalid because requester was null");
                }
                Entity entity = server().getEntity(packet.id);
                if (!(entity instanceof HiveHeartEntity hh)){
                    postError("[REQUEST] Packet was invalid because contained entity UUID was NOT a HiveHeartEntity");
                    return;
                }
                sync(hh, requester);
                System.out.println("Sent sync from server to client!");
            });
            context.get().setPacketHandled(true);
        }
    }

    private static void postError(String reason){
        System.err.println("[ARTERY CLIENT SYNCER] ERROR! Invalid packet was detected in syncer! Bailing...");
        System.err.println("[ARTERY CLIENT SYNCER] Info-- Cause of invalidity: " + reason);
    }
}
