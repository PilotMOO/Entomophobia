package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class SwarmSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_swarm_world_data";

    public SwarmSaveData(){
        super();
        server = EntomoForgeEvents.getServer();
    }
    public static void SetActiveSwarmData(ServerLevel server){
        Entomophobia.activeSwarmData = server.getDataStorage().computeIfAbsent(SwarmSaveData::load, SwarmSaveData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull SwarmSaveData activeData(){
        return Entomophobia.activeSwarmData;
    }
    public static void Dirty(){
        if (Entomophobia.activeSwarmData == null) return;
        Entomophobia.activeSwarmData.setDirty();
    }
    public static SwarmSaveData load(CompoundTag tag){
        SwarmSaveData data = new SwarmSaveData();
        new SwarmPackager(data, tag).UnpackSwarms();
        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        new SwarmPackager(this, tag).PackSwarms();
        return tag;
    }

    public ArrayList<SwarmPackager.PackagedSwarm> toUnpack = new ArrayList<>();

    private ServerLevel server;
    public ServerLevel getServer(){
        return server;
    }
    public void setServer(ServerLevel server){
        this.server = server;
    }

    public static class SwarmPackager{
        private SwarmPackager(SwarmSaveData data, CompoundTag tag){
            this.data = data;
            this.tag = tag;
        }
        private final SwarmSaveData data;
        private final CompoundTag tag;
        private final StringBuilder builder = new StringBuilder();
        private void CleanBuilder(){
            builder.setLength(0);
        }

        public void PackSwarms(){
            ArrayList<Swarm> swarms = SwarmManager.getSwarms();
            CleanBuilder();
            int tracker = 0;
            for (int i = 0; i < swarms.size(); i++){
                Swarm toPack = swarms.get(i);
                int idSize = builder.append("swarm").append(i).length();
                System.out.println("Trying to pack a swarm with I.D. " + builder + "...");

                tag.putUUID(builder.append("CaptainUUID").toString(), toPack.getCaptain().getUUID()); builder.setLength(idSize);
                tag.putByte(builder.append("Type").toString(), toPack.getSwarmType()); builder.setLength(idSize);
                tag.putByte(builder.append("State").toString(), toPack.getSwarmState()); builder.setLength(idSize);

                Vec3 finalPos = toPack.getDestination();
                if (finalPos != null){
                    tag.putDouble(builder.append("FPosX").toString(), finalPos.x); builder.setLength(idSize);
                    tag.putDouble(builder.append("FPosY").toString(), finalPos.y); builder.setLength(idSize);
                    tag.putDouble(builder.append("FPosZ").toString(), finalPos.z); builder.setLength(idSize);
                }

                tag.putInt(builder.append("MaxRecruits").toString(), toPack.getMaxRecruits()); builder.setLength(idSize);

                System.out.println("Packed up a Swarm with I.D. " + builder + "!");
                CleanBuilder();
                tracker++;
            }
            if (tracker > 0) {
                System.out.println("Finished packing " + tracker + " Swarm(s)!");
            } else {
                System.out.println("There were no swarms to pack!");
            }
        }
        public void UnpackSwarms(){
            CleanBuilder();
            builder.append("swarm");
            int tracker = 0;
            for (int i = 0; tag.contains(builder.append(i).append("Type").toString()); i++){
                builder.setLength(builder.length() - 4);
                int idLength = builder.length();

                UUID captainUUID = tag.getUUID(builder.append("CaptainUUID").toString()); builder.setLength(idLength);

                byte type = tag.getByte(builder.append("Type").toString()); builder.setLength(idLength);
                byte state = tag.getByte(builder.append("State").toString()); builder.setLength(idLength);

                Vec3 fPos = null;
                if (tag.contains(builder.append("FPosX").toString())){
                    double x = tag.getDouble(builder.toString()); builder.setLength(idLength);
                    double y = tag.getDouble(builder.append("FPosY").toString()); builder.setLength(idLength);
                    double z = tag.getDouble(builder.append("FPosZ").toString()); builder.setLength(idLength);
                    fPos = new Vec3(x, y, z);
                }

                int maxUnits = tag.getInt(builder.append("MaxRecruits").toString()); builder.setLength(idLength);

                data.toUnpack.add(new PackagedSwarm(captainUUID, type, state, fPos, maxUnits));
                System.out.println("Partially unpacked Swarm with I.D. " + builder + "!");
                builder.setLength(5);
                tracker++;
            }
            System.out.println("Tag did not contain " + builder);
            if (tracker > 0){
                System.out.println("Partially unpacked " + tracker + " Swarm(s)!");
            }else{
                System.out.println("There were no swarms to unpack!");
            }
        }

        public record PackagedSwarm(UUID captain, byte type, byte state, @Nullable Vec3 finalPos, int maxUnits){
            public void Unpack(MyiaticBase captain){
                SwarmManager.CreateSwarmFromBlueprint(captain, type, state, finalPos, maxUnits);
                Entomophobia.activeSwarmData.toUnpack.remove(this);
            }
        }
    }
}
