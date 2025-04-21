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
    }

    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
    public static <T> void sendToClient(T packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
