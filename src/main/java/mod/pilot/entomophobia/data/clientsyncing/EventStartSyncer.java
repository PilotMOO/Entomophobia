package mod.pilot.entomophobia.data.clientsyncing;

import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.EventStart.EventStart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EventStartSyncer {
    public static void request() {
        EntomoPacketSyncer.sendToServer(new ClientRequestPacket());
    }
    public static void sync(boolean over, boolean started, int fade, EventStart.FadeState state, ServerPlayer player) {
        sync(new ServerSyncPacket(over, started, fade, state), player);
    }
    public static void sync(ServerSyncPacket syncPacket, ServerPlayer player){
        EntomoPacketSyncer.sendToClient(syncPacket, player);
    }
    public static void syncAllClients(ServerSyncPacket syncPacket, ServerLevel server){
        for (ServerPlayer sPlayer : server.getPlayers(p -> true)){
            sync(syncPacket, sPlayer);
        }
    }

    public record ServerSyncPacket(boolean over, boolean started, int fade, EventStart.FadeState state) {

        public static ServerSyncPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            return new ServerSyncPacket(buffer.readBoolean(), buffer.readBoolean(), buffer.readInt(), EventStart.FadeState.fromInt(buffer.readInt()));
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeBoolean(over);
            buffer.writeBoolean(started);
            buffer.writeInt(fade);
            buffer.writeInt(state.ordinal());
        }

        public static void sync(ServerSyncPacket syncPacket, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                LogicalSide side = context.get().getDirection().getReceptionSide();
                if (side.isClient()) EventStart.Client.update(syncPacket);
                else postError("[SYNC] Sync packet was invoked on the logical server! Very cringe.");
            });
            context.get().setPacketHandled(true);
        }
    }

    public record ClientRequestPacket() {
        public static ClientRequestPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            return new ClientRequestPacket();
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {}
        public static void postRequest(ClientRequestPacket packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ServerPlayer requester = context.get().getSender();
                if (requester == null) {
                    postError("[REQUEST] Packet was invalid because requester was null"); return;
                }
                sync(EventStart.buildPacket(), requester);
            });
            context.get().setPacketHandled(true);
        }
    }

    private static void postError(String reason) {
        System.err.println("[EVENT START CLIENT SYNCER] ERROR! Invalid packet detected! Bailing...");
        System.err.println("[EVENT START CLIENT SYNCER] Info -- Cause of invalidity: " + reason);
    }
}
