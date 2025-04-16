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
    public static void setActiveSwarmData(ServerLevel server){
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
    public static void CleanPackagedSwarms() {
        ArrayList<SwarmPackager.PackagedSwarm> toRemove = new ArrayList<>();
        for (SwarmPackager.PackagedSwarm pSwarm : activeData().toUnpack){
            if (pSwarm.isFullyUnpacked()) toRemove.add(pSwarm);
        }
        activeData().toUnpack.removeAll(toRemove);
    }

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

                ArrayList<MyiaticBase> units = toPack.getUnits();
                builder.append("unit");
                int unitIDSize = idSize + 4;
                for (int index = 0; index < units.size(); index++){
                    builder.append(index);
                    tag.putUUID(builder.toString(), units.get(index).getUUID());
                    System.out.println("Packed up a UUID with I.D. " + builder);
                    builder.setLength(unitIDSize);
                }
                builder.setLength(idSize);

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

                ArrayList<UUID> unitUUIDs = new ArrayList<>();
                builder.append("unit");
                int unitIDSize = idLength + 4;
                for (int index = 0; tag.contains(builder.append(index).toString()); index++){
                    unitUUIDs.add(tag.getUUID(builder.toString()));
                    System.out.println("Unpacked a UUID with I.D. " + builder);
                    builder.setLength(unitIDSize);
                }

                data.toUnpack.add(new PackagedSwarm(captainUUID, type, state, fPos, maxUnits, unitUUIDs));

                builder.setLength(idLength);
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

        public static class PackagedSwarm{
            public PackagedSwarm(UUID captain, byte type, byte state, @Nullable Vec3 finalPos, int maxUnits, ArrayList<UUID> recruitUUIDs){
                this.captainUUID = captain;
                this.type = type;
                this.state = state;
                this.finalPos = finalPos;
                this.maxUnits = maxUnits;
                this.recruits = recruitUUIDs;
                this.recruitsSnapshot = new ArrayList<>(recruitUUIDs);
                this.unpackedSwarm = null;
            }
            public final UUID captainUUID;
            public final byte type;
            public final byte state;
            public final Vec3 finalPos;
            public final int maxUnits;
            public final ArrayList<UUID> recruits;
            private final ArrayList<UUID> recruitsSnapshot;
            public Swarm unpackedSwarm;

            public final ArrayList<MyiaticBase> awaitingApplication = new ArrayList<>();

            public boolean isFullyUnpacked(){
                return recruits.size() == 0 && awaitingApplication.size() == 0;
            }

            public void UnpackSwarm(MyiaticBase captain){
                unpackedSwarm = SwarmManager.CreateSwarmFromBlueprint(captain, type, state, finalPos, maxUnits);
            }
            public int UnpackAndAddUnit(MyiaticBase newUnit, boolean checkApplication){
                UUID uuid = newUnit.getUUID();
                if (uuid.equals(captainUUID)) return 0;

                if (!checkApplication || CheckApplication(newUnit.getUUID())){
                    if (unpackedSwarm == null){
                        System.out.println("Can't assign new unit " + newUnit + " because unpackedSwarm was null, putting in waiting list...");
                        awaitingApplication.add(newUnit);
                        recruits.remove(uuid);
                        return -1;
                    }
                    else{
                        System.out.println("Application succeeded!");
                        newUnit.ForceJoin(unpackedSwarm, true);
                        recruits.remove(uuid);
                        return 1;
                    }
                }
                System.out.println(newUnit + " failed application!");
                return 0;
            }

            private boolean CheckApplication(UUID application) {
                System.out.println("Testing application UUID " + application);
                for (UUID uuid : recruitsSnapshot){
                    System.out.println("Comparing to UUID " + uuid + "...");
                    if (uuid.equals(application)) {
                        System.out.println("Valid!");
                        return true;
                    }
                }
                System.out.println("Invalid!");
                return false;
            }

            public void EvaluateQueuedApplications() {
                if (unpackedSwarm == null){
                    System.out.println("Cannot Evaluate queued applications because the current swarm isn't unpacked!");
                    return;
                }
                int tracker = 0; //temp for testing
                for (MyiaticBase M : awaitingApplication){
                    if (CheckApplication(M.getUUID())){
                        System.out.println("Application for " + M + " was valid, recruiting...");
                        tracker++;
                        M.ForceJoin(unpackedSwarm, true);
                    }
                }
                awaitingApplication.clear();
                if (tracker == 0){
                    System.out.println("No valid applications were in que...");
                }
                else{
                    System.out.println("Unqueued " + tracker + " valid applications from que!");
                }
            }
        }
    }
}
