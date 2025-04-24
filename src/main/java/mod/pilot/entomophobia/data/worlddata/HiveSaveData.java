package mod.pilot.entomophobia.data.worlddata;

import mod.pilot.entomophobia.Entomophobia;
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
        System.out.println("[HIVE SAVE DATA] Storing tag to access later...");
        HiveSaveData data = new HiveSaveData();
        storeTag(tag);
        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        System.out.println("[HIVE SAVE DATA] Attempting to save all packets...");
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
    public static @Nullable Packet locateClosestData(Vec3 pos){
        Packet packet;

        Nest nest = NestManager.getClosestNest(pos);
        if (nest == null) return null;
        else packet = nest.accessData();

        if (packet == null){
            if (NestManager.getActiveNests().size() > 1) {
                double dist = Double.MAX_VALUE;

                Packet packet1;
                double dist1;
                for (Nest n : NestManager.getActiveNests()) {
                    if ((packet1 = n.accessData()) != null
                            && (dist1 = n.distanceTo(pos)) < dist){
                        packet = packet1;
                        dist = dist1;
                    }
                }
                return packet;
            } else return null;
        } else return packet;
    }

    public static final ArrayList<Packet> packets = new ArrayList<>();


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
        public void addToStorage(LivingEntity ID) {
            this.addToStorage(ID, 1);
        }
        public void addToStorage(LivingEntity ID, int count){
            this.addToStorage(ID.getEncodeId(), count);
        }
        public void addToStorage(String ID){
            this.addToStorage(ID, 1);
        }
        public void addToStorage(String ID, int count){
            if (storedEntities.containsKey(ID)){
                count += storedEntities.get(ID);
                storedEntities.replace(ID, count);
            } else storedEntities.put(ID, count);
            dirty();
        }

        public void removeFromStorage(LivingEntity ID) {
            this.removeFromStorage(ID, 1);
        }
        public void removeFromStorage(LivingEntity ID, int count){
            this.removeFromStorage(ID.getEncodeId(), count);
        }
        public void removeFromStorage(String ID){
            this.removeFromStorage(ID, 1);
        }
        public void removeFromStorage(String ID, int count){
            if (storedEntities.containsKey(ID)){
                count -= storedEntities.get(ID);
                count = Math.max(count, 0);
                storedEntities.replace(ID, count);
                dirty();
            }
        }

        public int getCountInStorage(LivingEntity ID){
            return getCountInStorage(ID.getEncodeId());
        }
        public int getCountInStorage(String ID){
            return storedEntities.getOrDefault(ID, 0);
        }

        public @Nullable LivingEntity getEntityFromStorage(String encodeID, Level level, boolean phantom){
            if (getCountInStorage(encodeID) == 0) return null;
            if (!phantom) removeFromStorage(encodeID);
            EntityType<?> eT = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(encodeID));
            assert eT != null;
            return (LivingEntity)eT.create(level);
        }
        public @Nullable LivingEntity getAnythingFromStorage(Level level, boolean phantom){
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
            return getEntityFromStorage(encode, level, phantom);
        }

        /**
         * Puts an entity instance into the hashmap if it isn't already present--
         * this is so the passive myiatic accumulation can accumulate myiatics that have been "unlocked" without requiring them to go into storage.
         * @param encode The Encode ID of the given entity
         */
        public void registerAsUnlockedEntity(String encode){
            storedEntities.putIfAbsent(encode, 0);
        }
        /**
         * Override of the method (see original)--- shorthand--- so you don't have to invoke LivingEntity.getEncodeID() every invoke of this method.
         * @param entity the LivingEntity to get the Encode ID from
         */
        public void registerAsUnlockedEntity(LivingEntity entity){
            registerAsUnlockedEntity(entity.getEncodeId());
        }

        public void incrementCorpsedew(int count){
            corpseDew += count;
            corpseDew = Math.max(corpseDew, 0);
            dirty();
        }
        public int corpseDew;

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
