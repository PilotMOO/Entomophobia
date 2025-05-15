package mod.pilot.entomophobia.data;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntomoWorldManager {

    // MOB MANAGEMENT
    public static Entity CreateNewEntityAt(EntityType<? extends Entity> entityType, Vec3 pos, Level world){
        Entity newEntity = entityType.create(world);
        assert newEntity != null;
        newEntity.setPos(pos);

        world.addFreshEntity(newEntity);
        return newEntity;
    }
    public static Entity CreateNewEntityAt(EntityType<? extends Entity> entityType, LivingEntity parent){
        return CreateNewEntityAt(entityType, parent.position(), parent.level());
    }

    public static MyiaticBase SpawnFromStorage(EntityType<? extends MyiaticBase> myiaticType, Vec3 pos, Level world){
        String ID = myiaticType.create(world).getEncodeId();
        if (Entomophobia.activeData.getQuantityOf(ID) > 0){
            Entomophobia.activeData.removeFromStorage(ID);
            return (MyiaticBase)CreateNewEntityAt(myiaticType, pos, world);
        }
        return null;
    }
    public static MyiaticBase SpawnFromStorageWithRandomPos(EntityType<? extends MyiaticBase> myiaticType, Vec3 originPos, Level world, int Magnitude){
        RandomSource rand = RandomSource.create();
        return SpawnFromStorage(myiaticType, getValidPosFor(originPos.add(rand.nextIntBetweenInclusive(-Magnitude, Magnitude), 0, rand.nextIntBetweenInclusive(-Magnitude, Magnitude)), world, myiaticType.create(world)), world);
    }
    public static MyiaticBase SpawnAnythingFromStorage(Vec3 pos, Level world){
        if (Entomophobia.activeData.getTotalInStorage() > 0){
            EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>)Entomophobia.activeData.getFirstFromStorage();
            if (type != null){
                return (MyiaticBase)CreateNewEntityAt(type, pos, world);
            }
        }
        return null;
    }
    public static MyiaticBase SpawnAnythingFromStorageWithRandomPos(Vec3 pos, Level world, int Magnitude){
        EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>)Entomophobia.activeData.getFirstFromStorage();
        if (type != null){
            RandomSource rand = RandomSource.create();
            return (MyiaticBase)CreateNewEntityAt(type, getValidPosFor(pos.add(rand.nextIntBetweenInclusive(-Magnitude, Magnitude), 0, rand.nextIntBetweenInclusive(-Magnitude, Magnitude)), world, (MyiaticBase) type.create(world)), world);
        }
        return null;
    }



    public static Vec3 getValidPosFor(Vec3 pos, Level world, MyiaticBase target){
        Vec3 currentPos = pos;
        int cycleCount = 0;
        while (!isThisLocationValid(currentPos, world, target) && cycleCount < 64){
            currentPos = nextPosToCheck(currentPos, world);
            cycleCount++;
        }
        if (cycleCount > 64){
            return null;
        }
        return currentPos;
    }
    private static Vec3 nextPosToCheck(Vec3 currentPos, Level world) {
        BlockPos BPos = new BlockPos((int)currentPos.x, (int)currentPos.y, (int)currentPos.z);
        BlockState BState = world.getBlockState(BPos);
        if (BState.isAir()){
            return currentPos.add(0, -1, 0);
        }
        return currentPos.add(0 , 1, 0);
    }
    private static boolean isThisLocationValid(Vec3 pos, Level world, MyiaticBase target){
        BlockPos BPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
        BlockState BState = world.getBlockState(BPos);
        if (BState.isAir()){
            BlockPos newPos = new BlockPos(BPos.getX(), BPos.getY() - 1, BPos.getZ());
            BlockState state = world.getBlockState(newPos);
            return !state.isAir() && state.entityCanStandOn(world.getChunkForCollisions(newPos.getX() / 16, newPos.getZ() / 16),
                    newPos, target);
        }
        return false;
    }
}
