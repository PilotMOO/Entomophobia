package mod.pilot.entomophobia;

import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.effects.PheromonePrey;
import mod.pilot.entomophobia.effects.PheromonesBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class EntomoWorldManager {

    // MOB MANAGEMENT

    private static final ArrayList<MyiaticBase> AllMyiatics = new ArrayList<>();

    public static boolean AddThisToAllMyiatics(MyiaticBase target){
        for (MyiaticBase M : AllMyiatics){
            if (target == M){
                return false;
            }
        }
        AllMyiatics.add(target);
        return true;
    }
    public static boolean RemoveThisFromAllMyiatics(MyiaticBase target){
        for (MyiaticBase M : AllMyiatics){
            if (target == M){
                AllMyiatics.remove(target);
                return true;
            }
        }
        return false;
    }
    public static boolean IsThisInsideOfAllMyiatics(MyiaticBase target){
        for (MyiaticBase M : AllMyiatics){
            if (target == M){
                return true;
            }
        }
        return false;
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(Vec3 pos, int searchRange, Predicate<MyiaticBase> MyiaticPredic){
        ArrayList<MyiaticBase> NearbyMyiatics = new ArrayList<>();

        for (MyiaticBase M : AllMyiatics){
            if (Mth.sqrt((float)M.distanceToSqr(pos)) < searchRange){
                if (MyiaticPredic.test(M)){
                    NearbyMyiatics.add(M);
                }
            }
        }
        return NearbyMyiatics;
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(Vec3 pos, int searchRange){
        return GetNearbyMyiatics(pos, searchRange, (E) -> true);
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(LivingEntity parent, int searchRange, Predicate<MyiaticBase> MyiaticPredic){
        return GetNearbyMyiatics(parent.position(), searchRange, MyiaticPredic);
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(LivingEntity parent, Predicate<MyiaticBase> MyiaticPredic){
        return GetNearbyMyiatics(parent.position(), (int)parent.getAttribute(Attributes.FOLLOW_RANGE).getValue(), MyiaticPredic);
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(LivingEntity parent, int searchRange){
        return GetNearbyMyiatics(parent.position(), searchRange, (E) -> true);
    }
    public static ArrayList<MyiaticBase> GetNearbyMyiatics(LivingEntity target){
        return GetNearbyMyiatics(target.position(), (int)target.getAttribute(Attributes.FOLLOW_RANGE).getValue(), (E) -> true);
    }
    public static MyiaticBase GetClosestMyiaticInRange(LivingEntity parent, int searchRange){
        MyiaticBase NearbyMyiatic = null;

        for (MyiaticBase M : AllMyiatics){
            if (M.distanceTo(parent) < searchRange){
                if (NearbyMyiatic != null){
                    if (M.distanceTo(parent) < NearbyMyiatic.distanceTo(parent)){
                        NearbyMyiatic = M;
                    }
                }
                else{
                    NearbyMyiatic = M;
                }
            }
        }
        return NearbyMyiatic;
    }
    public static MyiaticBase GetClosestMyiaticInRange(LivingEntity parent){
        return GetClosestMyiaticInRange(parent, (int)parent.getAttributeValue(Attributes.FOLLOW_RANGE));
    }



    public static ArrayList<LivingEntity> GetValidTargetsFor(MyiaticBase target){
        AABB nearby = target.getBoundingBox().inflate(target.getAttributeValue(Attributes.FOLLOW_RANGE));
        return new ArrayList<>(target.level().getEntitiesOfClass(LivingEntity.class, nearby, target::TestValidEntity));
    }
    public static ArrayList<LivingEntity> GetValidTargetsFor(MyiaticBase target, int SearchRange){
        AABB nearby = target.getBoundingBox().inflate(SearchRange);
        return new ArrayList<>(target.level().getEntitiesOfClass(LivingEntity.class, nearby, target::TestValidEntity));
    }


    public static LivingEntity CreateNewEntityAt(EntityType<? extends LivingEntity> entityType, Vec3 pos, Level world){
        LivingEntity newEntity = entityType.create(world);
        assert newEntity != null;
        newEntity.setPos(pos);

        world.addFreshEntity(newEntity);
        return newEntity;
    }
    public static LivingEntity CreateNewEntityAt(EntityType<? extends LivingEntity> entityType, LivingEntity parent){
        return CreateNewEntityAt(entityType, parent.position(), parent.level());
    }



    //  PHEROMONE MANAGEMENT

    private static final ArrayList<PheromonesEntityBase> AllPheromones = new ArrayList<>();
    public static boolean AddThisToAllPheromones(PheromonesEntityBase target){
        for (PheromonesEntityBase P : AllPheromones){
            if (P == target){
                return false;
            }
        }
        AllPheromones.add(target);
        return true;
    }
    public static boolean RemoveThisFromAllPheromones(PheromonesEntityBase target){
        for (PheromonesEntityBase P : AllPheromones){
            if (P == target){
                AllPheromones.remove(target);
                return true;
            }
        }
        return false;
    }
    public static boolean IsThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type, int searchRange, Vec3 pos, Level world){
        String instance = type.create(world).getEncodeId();

        for (PheromonesEntityBase P : AllPheromones){
            if (P != null && Objects.equals(P.getEncodeId(), instance) && Mth.sqrt((float)P.distanceToSqr(pos)) < searchRange){
                return true;
            }
        }
        return false;
    }
    public static boolean IsThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type, int searchRange, LivingEntity parent){
        return IsThereAPheromoneOfTypeXNearby(type, searchRange, parent.position(), parent.level());
    }
    public static boolean IsThereAPheromoneOfTypeXNearby(EntityType<? extends PheromonesEntityBase> type, LivingEntity parent){
        return IsThereAPheromoneOfTypeXNearby(type, (int)parent.getAttribute(Attributes.FOLLOW_RANGE).getValue(), parent.position(), parent.level());
    }


    private static final ArrayList<LivingEntity> AllPrey = new ArrayList<>();
    public static boolean IsThisPrey(LivingEntity target){
        for (LivingEntity LE : AllPrey){
            if (LE == target){
                return true;
            }
        }
        return false;
    }

    public static boolean AddThisAsPrey(LivingEntity target){
        if (!IsThisPrey(target)){
            AllPrey.add(target);
            return true;
        }
        return false;
    }
    public static boolean RemoveThisAsPrey(LivingEntity target){
        if (IsThisPrey(target)){
            AllPrey.remove(target);
            return true;
        }
        return false;
    }
    public static LivingEntity GetNearbyPrey(MyiaticBase parent){
        double PreySearchRange = parent.getAttribute(Attributes.FOLLOW_RANGE).getValue() + Config.SERVER.hunt_bonus_range.get();
        LivingEntity closestPrey = null;

        for (LivingEntity target : AllPrey){
            if (!target.isDeadOrDying() && target.hasEffect(EntomoMobEffects.PREY.get())){
                if (parent.TestValidEntity(target) && target.distanceTo(parent) < PreySearchRange){
                    if (closestPrey != null){
                        if (target.distanceTo(parent) < closestPrey.distanceTo(parent)){
                            closestPrey = target;
                        }
                    }
                    else{
                        closestPrey = target;
                    }
                }
            }
        }

        return closestPrey;
    }

    public static MobEffectInstance ApplyPheromoneTo(PheromonesBase effect, LivingEntity target, int duration, int amp){
        MobEffectInstance instance = new MobEffectInstance(effect, duration, amp);
        if (effect.ApplicableToMyiatic && target instanceof MyiaticBase){
            target.addEffect(instance);
        }
        else if (effect.ApplicableToNon && !(target instanceof MyiaticBase)){
            target.addEffect(instance);
            if (effect instanceof PheromonePrey){
                AddThisAsPrey(target);
            }
        }

        return instance;
    }
}
