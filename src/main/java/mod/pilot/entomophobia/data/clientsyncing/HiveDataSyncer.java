package mod.pilot.entomophobia.data.clientsyncing;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class HiveDataSyncer {
    /**
     * Posts a Client-sided request to the server for the data of a given HiveHeart. Reserved for Client-side only.
     * <p></p>
     * Does NOT modify the data on the Server, see {@link ClientBoundSyncPacket} if you wish to modify data on the server.
     * Override; automatically grabs the UUID of the HiveHeartEntity then invokes the base method.
     * @param hh The give HiveHeartEntity that the Client requests to sync data between.
     */
    @OnlyIn(Dist.CLIENT)
    public static void request(HiveHeartEntity hh) {
        request(hh.getUUID());
    }

    /**
     * Posts a Client-sided request to the server for the data of a given HiveHeart. Reserved for Client-side only.
     * <p></p>
     * Does NOT modify the data on the Server, see {@link ClientBoundSyncPacket} if you wish to modify data on the server.
     * @param id The UUID of the given HiveHeartEntity that the Client requests to sync data between
     */
    @OnlyIn(Dist.CLIENT)
    public static void request(UUID id) {
        HiveDataSyncer.ClientRequestPacket requestPacket = new HiveDataSyncer.ClientRequestPacket(id);
        EntomoPacketSyncer.sendToServer(requestPacket);
    }

    /**
     * Creates then sends a sync packet from the server to a given client. Reserved for Server-side only.
     * @param hh The given HiveHeartEntity to sync the data between
     * @param player The ServerPlayer connected to the client that will have the data synced between
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void sync(HiveHeartEntity hh, ServerPlayer player) {
        SyncPacket syncPacket = new SyncPacket(hh);
        EntomoPacketSyncer.sendToClient(syncPacket, player);
    }

    /**
     * Attempts to sync the data from of a given HiveHeartEntity instance on the server to all the clients. Reserved for Server-side only.
     * @param hh The HiveHeartEntity to sync the data between
     * @param server The ServerLevel, for accessing all active ServerPlayers
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void syncAllClients(HiveHeartEntity hh, ServerLevel server){
        for (ServerPlayer sPlayer : server.getPlayers(p -> true)){
            sync(hh, sPlayer);
        }
    }

    /**
     * A sync packet, created in the server environment then sent to a client to sync HiveData between instances of a given HiveHeartEntity.
     * <p></p>
     * Constructors of this object are reserved for Server-side, if you are looking to send data from the client to sync on the server, see {@link ClientBoundSyncPacket}
     * @param id The UUID of the given HiveHeartEntity that the packet is associated with
     * @param packet The HiveData packet to sync between the server and a client
     */
    public record SyncPacket(UUID id, HiveSaveData.Packet packet) {
        @OnlyIn(Dist.DEDICATED_SERVER)
        public SyncPacket(HiveHeartEntity hh) {
            this(hh.getUUID(), hh.accessData());
        }
        @OnlyIn(Dist.DEDICATED_SERVER)
        public SyncPacket{}
        public static SyncPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            UUID id = buffer.readUUID();
            int corpseDew = buffer.readInt();
            int storedCount = buffer.readInt();
            HashMap<String, Integer> entities = new HashMap<>(storedCount);
            for (; storedCount > 0; --storedCount){
                entities.put(buffer.readUtf(), buffer.readInt());
            }
            return new SyncPacket(id, HiveSaveData.Packet.recreate(id, corpseDew, entities));
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(id);
            buffer.writeInt(packet.corpseDew);
            buffer.writeInt(packet.storedEntities.size());
            for (String s : packet.storedEntities.keySet()){
                buffer.writeUtf(s);
                buffer.writeInt(packet.getCountInStorage(s));
            }
        }

        public static void sync(SyncPacket syncPacket, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> middleMan.put(syncPacket.id, syncPacket));
            context.get().setPacketHandled(true);
        }
    }

    /**
     * A sync packet, created in the client environment then sent to the server to sync changes to HiveData.
     * Option to also sync it between other clients
     * <p></p>
     * Constructors of this object are reserved for Client-side, if you are looking to send data from the server to sync on a client,
     * see {@link SyncPacket}
     * @param id The UUID of the given HiveHeartEntity that the packet is associated with
     * @param packet The HiveData packet to sync between a client and the server
     * @param syncOtherClients if true, attempts to post sync packets to the other clients after being synced between the sender client and the server
     */
    public record ClientBoundSyncPacket(UUID id, HiveSaveData.Packet packet, boolean syncOtherClients) {
        @OnlyIn(Dist.CLIENT)
        public ClientBoundSyncPacket(HiveHeartEntity hh, boolean syncOtherClients) {
            this(hh.getUUID(), hh.accessData(), syncOtherClients);
        }
        @OnlyIn(Dist.CLIENT)
        public ClientBoundSyncPacket{}
        public static ClientBoundSyncPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            UUID id = buffer.readUUID();
            int corpseDew = buffer.readInt();
            boolean sync = buffer.readBoolean();
            int storedCount = buffer.readInt();
            HashMap<String, Integer> entities = new HashMap<>(storedCount);
            for (; storedCount > 0; --storedCount){
                entities.put(buffer.readUtf(), buffer.readInt());
            }
            return new ClientBoundSyncPacket(id, HiveSaveData.Packet.recreate(id, corpseDew, entities), sync);
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(id);
            buffer.writeInt(packet.corpseDew);
            buffer.writeBoolean(syncOtherClients);
            buffer.writeInt(packet.storedEntities.size());
            for (String s : packet.storedEntities.keySet()){
                buffer.writeUtf(s);
                buffer.writeInt(packet.getCountInStorage(s));
            }
        }

        public static void sync(ClientBoundSyncPacket boundSyncPacket, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (context.get().getDirection().getReceptionSide().isClient()){
                    postError("[CLIENT BOUND SYNC] A sync packet from a client to the server was discovered on the client side!");
                    return;
                }
                middleMan.put(boundSyncPacket.id, new SyncPacket(boundSyncPacket.id, boundSyncPacket.packet));
                if (boundSyncPacket.syncOtherClients) {
                    ServerPlayer serverPlayer = context.get().getSender();
                    if (serverPlayer == null) {
                        postError("[CLIENT BOUND SYNC] Sync packet FAILED to access the sender, despite packet being on the server side");
                        return;
                    }
                    ServerLevel server = serverPlayer.serverLevel();
                    syncAllClients((HiveHeartEntity)server.getEntity(boundSyncPacket.id), server);
                }
            });
            context.get().setPacketHandled(true);
        }
    }

    /**
     * A request packet, created in the client environment then sent to the server to request a sync between the given client and the server.
     * <p></p>
     * Constructors of this object are NOT reserved for a given logical side, but posting one from the server wouldn't do much but print out an error in the logs.
     * @param id The UUID of the given HiveHeartEntity that the Client requests to sync data between
     */
    public record ClientRequestPacket(UUID id) {
        public static HiveDataSyncer.ClientRequestPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            return new HiveDataSyncer.ClientRequestPacket(buffer.readUUID());
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(id);
        }
        public static void postRequest(HiveDataSyncer.ClientRequestPacket packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ServerPlayer requester = context.get().getSender();
                if (requester == null) {
                    postError("[REQUEST] Packet was invalid because requester was null");
                    return;
                }
                ServerLevel level = EntomoForgeEvents.getServer();
                if (level == null) {
                    postError("[REQUEST] ServerLevel was null during packet handling.");
                    return;
                }

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

    //This man is available on the server as well so clients can send changes to the server
    /**
     * Behold. A man.
     * <p></p>
     * A "middle man" for syncing packets (since you can't get an entity from a UUID on the client for some reason...)
     * Holds all sent sync packets and unpacks them whenever the holder HiveHeartEntity attempts to access its data.
     */
    private static final HashMap<UUID, SyncPacket> middleMan = new HashMap<>();

    /**
     * It's an accessor for the middleMan HashMap in this class, shorthand for {@code Hashmap.getOrDefault(Object key, V defaultValue)}.
     * @param id The UUID key to use to access a given SyncPacket stored for the UUID (IF present)
     * @return The stored {@link SyncPacket} in the middleMan, or {@code null} if not present
     */
    public static @Nullable SyncPacket retrieveFromMiddleMan(UUID id){
        return middleMan.getOrDefault(id, null);
    }

    /**
     * Checks the middleMan for any stored SyncPackets then unpacks and Syncs them if present.
     * @param hh The HiveHeartEntity to check for any {@link SyncPacket}s stored in the middleMan
     * @return The new data unpacked from a stored {@link SyncPacket} IF present, otherwise returns the current data of the HiveHeartEntity
     */
    public static HiveSaveData.Packet checkMiddleMan(HiveHeartEntity hh){
        HiveSaveData.Packet data = hh.accessData(true);
        SyncPacket packet = retrieveFromMiddleMan(hh.getUUID());
        if (packet != null){
            middleMan.remove(hh.getUUID());
            return packet.packet;
        }
        return data;
    }

    /**
     * Posts an error into the logger about a failed packet. Shorthand.
     * @param reason The String detailing the unique error reason
     */
    private static void postError(String reason) {
        System.err.println("[HIVE DATA SYNCER] ERROR! Invalid packet detected! Bailing...");
        System.err.println("[HIVE DATA SYNCER] Info -- Cause of invalidity: " + reason);
    }
}
