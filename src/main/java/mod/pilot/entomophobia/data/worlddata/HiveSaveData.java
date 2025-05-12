package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.data.clientsyncing.HiveDataSyncer;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.event.EntomoForgeEvents;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HiveSaveData extends SavedData {
    public static final String NAME = Entomophobia.MOD_ID + "_hive_world_data";

    public HiveSaveData(){
        super();
        server = EntomoForgeEvents.getServer();
    }
    public static void setActiveHiveData(ServerLevel server){
        Entomophobia.activeHiveData = server.getDataStorage().computeIfAbsent(HiveSaveData::load, HiveSaveData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull HiveSaveData activeData(){
        return Entomophobia.activeHiveData;
    }
    public static void dirty(){
        if (Entomophobia.activeHiveData == null) return;
        Entomophobia.activeHiveData.setDirty();
    }
    public static HiveSaveData load(CompoundTag tag){
        //System.out.println("[HIVE SAVE DATA] Storing tag to access later...");
        HiveSaveData data = new HiveSaveData();
        storeTag(tag);
        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        //System.out.println("[HIVE SAVE DATA] Attempting to save all packets...");
        packets.forEach(p -> p.pack(tag));
        return tag;
    }


    private ServerLevel server;
    public ServerLevel getServer(){
        return server;
    }
    public void setServer(ServerLevel server){
        this.server = server;
    }

    public static void storeTag(CompoundTag tag){
        HiveSaveData.tag = tag;
    }
    private static CompoundTag tag;

    public static @Nullable Packet retrieveData(HiveHeartEntity retriever){
        if (tag == null) return null;
        Packet data = null;
        String check = retriever.getStringUUID() + "_corpsedew";
        if (tag.contains(check)){
            data = Packet.unpack(tag, retriever.getUUID());
        }
        return data;
    }
    public static @NotNull Pair<@org.jetbrains.annotations.Nullable Packet,
            @org.jetbrains.annotations.Nullable HiveHeartEntity> locateClosestDataAndAccessor(Vec3 pos){
        Packet packet;
        HiveHeartEntity hh;

        Nest nest = NestManager.getClosestNest(pos);
        if (nest == null) return empty();
        else{
            packet = nest.accessData();
            hh = nest.accessHiveHeart();
        }

        if (packet == null){
            if (NestManager.getActiveNests().size() > 1) {
                double dist = Double.MAX_VALUE;

                Packet packet1;
                double dist1;
                for (Nest n : NestManager.getActiveNests()) {
                    if ((packet1 = n.accessData()) != null
                            && (dist1 = n.distanceTo(pos)) < dist){
                        packet = packet1;
                        hh = n.accessHiveHeart();
                        dist = dist1;
                    }
                }
                return pack(packet, hh);
            } else return empty();
        } else return pack(packet, hh);
    }
    private static Pair<Packet, HiveHeartEntity> empty(){
        return new Pair<>(null, null);
    }
    private static Pair<Packet, HiveHeartEntity> pack(Packet packet, HiveHeartEntity hh){
        return new Pair<>(packet, hh);
    }

    public static final ArrayList<Packet> packets = new ArrayList<>(){
        @Override
        public boolean add(Packet packet) {
            Packet remove = null;
            for (Packet packet1 : this){
                if (packet1.hiveHeart.equals(packet.hiveHeart)){
                    remove = packet1;
                }
            }
            if (remove != null) this.remove(remove);
            return super.add(packet);
        }
    };


    public static Packet createNewDataPacket(UUID hiveHeart){
        return new Packet(hiveHeart, true);
    }

    public static class Packet {
        public static Packet unpack(CompoundTag tag, UUID hiveHeart){
            return new Packet(tag, hiveHeart);
        }

        private Packet(CompoundTag tag, UUID hiveHeart){
            System.out.println("[HIVE SAVE DATA] Attempting to unpack a packet with a UUID of " + hiveHeart);

            this.hiveHeart = hiveHeart;

            String identifier = hiveHeart + "_";
            int idLength = identifier.length();
            cleanBuilder(); builder.append(identifier);

            storedEntities = new HashMap<>();

            final String entityPrepend = "stored_";
            final String countPrepend = "_count";
            final int compoundPrepend =  idLength + entityPrepend.length();
            builder.append(entityPrepend);
            for (int i = 0; tag.contains(builder.append(i).toString()); i++){
                String encode = tag.getString(builder.toString());
                builder.append(countPrepend);
                int count = tag.getInt(builder.toString());

                storedEntities.put(encode, count);

                builder.setLength(compoundPrepend);
            }

            builder.setLength(idLength);
            builder.append("corpsedew");
            this.corpseDew = tag.getInt(builder.toString());
            cleanBuilder();

            HiveSaveData.packets.add(this);
            dirty();
        }

        public static Packet recreate(UUID id, int corpseDew, HashMap<String, Integer> storedEntities){
            Packet p = new Packet(id, false);
            p.corpseDew = corpseDew;
            p.storedEntities = storedEntities;
            HiveSaveData.packets.add(p);
            dirty();
            return p;
        }
        private Packet(UUID hiveHeart, boolean setup){
            this.hiveHeart = hiveHeart;
            if (setup) {
                setup();
                HiveSaveData.packets.add(this);
                dirty();
            }
        }

        public final UUID hiveHeart;
        public HiveHeartEntity getHiveHeart(ServerLevel server){
            return (HiveHeartEntity)server.getEntity(hiveHeart);
        }

        StringBuilder builder = new StringBuilder();
        private void cleanBuilder(){
            builder.setLength(0);
        }


        public HashMap<String, Integer> storedEntities;
        public Packet addToStorage(LivingEntity ID) {
            return this.addToStorage(ID, 1);
        }
        public Packet addToStorage(LivingEntity ID, int count){
            return this.addToStorage(ID.getEncodeId(), count);
        }
        public Packet addToStorage(String ID){
            return this.addToStorage(ID, 1);
        }
        public Packet addToStorage(String ID, int count){
            if (storedEntities.containsKey(ID)){
                count += storedEntities.get(ID);
                storedEntities.replace(ID, count);
            } else storedEntities.put(ID, count);
            dirty();
            return this;
        }

        public Packet removeFromStorage(LivingEntity ID) {
            return this.removeFromStorage(ID, 1);
        }
        public Packet removeFromStorage(LivingEntity ID, int count){
            return this.removeFromStorage(ID.getEncodeId(), count);
        }
        public Packet removeFromStorage(String ID){
            return this.removeFromStorage(ID, 1);
        }
        public Packet removeFromStorage(String ID, int count){
            if (storedEntities.containsKey(ID)){
                int newCount = storedEntities.get(ID) - count;
                newCount = Math.max(newCount, 0);
                storedEntities.replace(ID, newCount);
                dirty();
            }
            return this;
        }

        public int getCountInStorage(LivingEntity ID){
            return getCountInStorage(ID.getEncodeId());
        }
        public int getCountInStorage(@Nullable String ID){
            return storedEntities.getOrDefault(ID, 0);
        }
        public int getTotalInStorage(){
            int count = 0;
            for (String s : storedEntities.keySet()){
                count += getCountInStorage(s);
            }
            return count;
        }

        public @Nullable LivingEntity getEntityFromStorage(String encodeID, Level level){
            return getEntityFromStorage(encodeID, level, false);
        }
        public @Nullable LivingEntity getEntityFromStorage(String encodeID, Level level, boolean phantom){
            if (getCountInStorage(encodeID) == 0) return null;
            if (!phantom) removeFromStorage(encodeID);
            EntityType<?> eT = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(encodeID));
            assert eT != null;
            return (LivingEntity)eT.create(level);
        }
        public @Nullable LivingEntity getAnythingFromStorage(Level level){
            return getAnythingFromStorage(level, false);
        }
        public @Nullable LivingEntity getAnythingFromStorage(Level level, boolean phantom){
            System.out.println("Attempting to get smth from storage...");
            String encode = null;
            Set<String> values = storedEntities.keySet();
            int index = level.getRandom().nextInt(values.size());
            int i = 0;
            for (String s : values){
                if (i == index) {
                    encode = s;
                    break;
                } else i++;
            }
            if (encode == null){
                System.err.println("[LOCALIZED HIVE DATA] [STORED ENTITIES] Oops! Failed to locate an encode I.D. of index ["
                        + index + "] when attempting to retrieve from storage! :[");
            }
            System.out.println("Located [" + encode + "] in storage, attempting to create...");
            System.out.println("Amount of [" + encode + "] in storage: " + getCountInStorage(encode));
            return getEntityFromStorage(encode, level, phantom);
        }

        /**
         * Puts an entity instance into the hashmap if it isn't already present--
         * this is so the passive myiatic accumulation can accumulate myiatics that have been "unlocked" without requiring them to go into storage.
         * @param encode The Encode ID of the given entity
         */
        public Packet registerAsUnlockedEntity(String encode){
            storedEntities.putIfAbsent(encode, 0);
            return this;
        }
        /**
         * Override of the method (see original)--- shorthand--- so you don't have to invoke LivingEntity.getEncodeID() every invoke of this method.
         * @param entity the LivingEntity to get the Encode ID from
         */
        public Packet registerAsUnlockedEntity(LivingEntity entity){
            return registerAsUnlockedEntity(entity.getEncodeId());
        }

        public Packet incrementCorpsedew(){
            return incrementCorpsedew(1);
        }
        public Packet incrementCorpsedew(int count){
            corpseDew += count;
            corpseDew = Math.max(corpseDew, 0);
            dirty();
            return this;
        }
        public int corpseDew;

        public void thenSync(ServerLevel server){
            thenSync(getHiveHeart(server));
        }
        public void thenSync(HiveHeartEntity hh){
            if (hiveHeart.equals(hh.getUUID())) {
                Level level = hh.level();
                if (level.isClientSide) {
                    HiveDataSyncer.pushClientChanges(hh, true);
                } else if (level instanceof ServerLevel server) {
                    HiveDataSyncer.syncAllClients(hh, server);
                }
            } else System.err.println("[HIVE SAVE DATA] Error! Attempted to sync changes across clients the UUID of the sender and the packet are inconsistent!");
        }

        private void setup(){
            this.storedEntities = new HashMap<>();
            this.corpseDew = 0;
            dirty();
        }

        public void pack(CompoundTag tag){
            System.out.println("[HIVE SAVE DATA] Attempting to pack a packet with a UUID of " + hiveHeart);

            String identifier = hiveHeart + "_";
            int idLength = identifier.length();
            cleanBuilder(); builder.append(identifier);

            int i = 0;
            final String entityPrepend = "stored_";
            final String countPrepend = "_count";
            final int compoundPrepend =  idLength + entityPrepend.length();
            builder.append(entityPrepend);
            for (String encode : storedEntities.keySet()){
                builder.append(i);
                tag.putString(builder.toString(), encode);
                builder.append(countPrepend);
                tag.putInt(builder.toString(), getCountInStorage(encode));

                builder.setLength(compoundPrepend);
                i++;
            }
            builder.setLength(idLength);
            builder.append("corpsedew");
            tag.putInt(builder.toString(), corpseDew);
            cleanBuilder();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Packet packet1
                    && packet1.hiveHeart.equals(this.hiveHeart)
                    && packet1.corpseDew == this.corpseDew
                    && packet1.storedEntities.equals(this.storedEntities)){
                return true;
            }
            return false;
        }
    }
}
