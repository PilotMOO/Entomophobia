package mod.pilot.entomophobia.data.clientsyncing;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;

//Stole this from harbinger's github ;)
public class EntomoPacketSyncer {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Entomophobia.MOD_ID, "main"), () -> VERSION,
            VERSION::equals, VERSION::equals);

    private static final AtomicInteger packetId = new AtomicInteger(0);
    public static void registerPackets(){
        /*CHANNEL.messageBuilder(ExamplePacket.class, packetId.getAndIncrement())
                .encoder(ExamplePacket::encode)
                .decoder(ExamplePacket::new)
                .consumerMainThread(ExamplePacket::handle)
                .add();*/
        //Artery Syncing
        CHANNEL.messageBuilder(ArteryClientSyncer.ServerSyncPacket.class, packetId.getAndIncrement())
                .encoder(ArteryClientSyncer.ServerSyncPacket::writeToBuffer)
                .decoder(ArteryClientSyncer.ServerSyncPacket::decodeFromBuffer)
                .consumerMainThread(ArteryClientSyncer.ServerSyncPacket::sync)
                .add();
        CHANNEL.messageBuilder(ArteryClientSyncer.ClientRequestPacket.class, packetId.getAndIncrement())
                .encoder(ArteryClientSyncer.ClientRequestPacket::writeToBuffer)
                .decoder(ArteryClientSyncer.ClientRequestPacket::decodeFromBuffer)
                .consumerMainThread(ArteryClientSyncer.ClientRequestPacket::postRequest)
                .add();

        //Hive Data Syncing
        CHANNEL.messageBuilder(HiveDataSyncer.SyncPacket.class, packetId.getAndIncrement())
                .encoder(HiveDataSyncer.SyncPacket::writeToBuffer)
                .decoder(HiveDataSyncer.SyncPacket::decodeFromBuffer)
                .consumerMainThread(HiveDataSyncer.SyncPacket::sync)
                .add();
        CHANNEL.messageBuilder(HiveDataSyncer.ClientBoundSyncPacket.class, packetId.getAndIncrement())
                .encoder(HiveDataSyncer.ClientBoundSyncPacket::writeToBuffer)
                .decoder(HiveDataSyncer.ClientBoundSyncPacket::decodeFromBuffer)
                .consumerMainThread(HiveDataSyncer.ClientBoundSyncPacket::sync)
                .add();
        CHANNEL.messageBuilder(HiveDataSyncer.ClientRequestPacket.class, packetId.getAndIncrement())
                .encoder(HiveDataSyncer.ClientRequestPacket::writeToBuffer)
                .decoder(HiveDataSyncer.ClientRequestPacket::decodeFromBuffer)
                .consumerMainThread(HiveDataSyncer.ClientRequestPacket::postRequest)
                .add();

        //Event Start Syncing
        CHANNEL.messageBuilder(EventStartSyncer.ServerSyncPacket.class, packetId.getAndIncrement())
                .encoder(EventStartSyncer.ServerSyncPacket::writeToBuffer)
                .decoder(EventStartSyncer.ServerSyncPacket::decodeFromBuffer)
                .consumerMainThread(EventStartSyncer.ServerSyncPacket::sync)
                .add();
        CHANNEL.messageBuilder(EventStartSyncer.ClientRequestPacket.class, packetId.getAndIncrement())
                .encoder(EventStartSyncer.ClientRequestPacket::writeToBuffer)
                .decoder(EventStartSyncer.ClientRequestPacket::decodeFromBuffer)
                .consumerMainThread(EventStartSyncer.ClientRequestPacket::postRequest)
                .add();
    }

    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
    public static <T> void sendToClient(T packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
