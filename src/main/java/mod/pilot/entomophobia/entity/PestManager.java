package mod.pilot.entomophobia.entity;

import mod.pilot.entomophobia.entity.truepest.PestBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class PestManager {
    private static final Random random = new Random();
    private static final ArrayList<EntityType<? extends PestBase>> allPests = new ArrayList<>();
    public static void registerAsPest(EntityType<? extends PestBase> pest){
        allPests.add(pest);
    }
    public static ArrayList<EntityType<? extends PestBase>> getAllPestTypes(){
        return new ArrayList<>(allPests);
    }

    public static EntityType<? extends PestBase> getRandomPestType(){
        return getAllPestTypes().get(random.nextInt(allPests.size()));
    }

    public static PestBase createPestAt(Level level, Vec3 position, @Nullable LivingEntity target){
        return createPestAt(level, position, 0, PestBase.defaultAge, target);
    }
    public static PestBase createPestAt(Level level, Vec3 position, int ageType, int maxAge, @Nullable LivingEntity target){
        PestBase pest = getRandomPestType().create(level);
        if (pest == null){
            System.err.println("[PEST MANAGER] Attempted to create a pest, but getRandomPestType().create(level) returned null!");
            return null;
        }
        pest.setMaxAge(maxAge);
        pest.setAgeType(ageType);
        pest.setPos(position);
        pest.setTarget(target);
        level.addFreshEntity(pest);
        return pest;
    }

    public static void registerAll(){
        registerAsPest(EntomoEntities.SPIDER_PEST.get());
        registerAsPest(EntomoEntities.GRUB_PEST.get());
        registerAsPest(EntomoEntities.COCKROACH_PEST.get());
        registerAsPest(EntomoEntities.CENTIPEDE_PEST.get());
    }
    public static void flushList(){
        allPests.clear();
    }
}
