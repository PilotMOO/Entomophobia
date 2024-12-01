package mod.pilot.entomophobia.blocks.custom;

import mod.pilot.entomophobia.data.EntomoDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MyiaticFleshBlock extends Block {
    public MyiaticFleshBlock(Properties pProperties) {
        super(pProperties);
    }

    public static Block getDecayed(){
        //ToDo Add dead flesh block
        return null;
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos bPos, @NotNull BlockState bState) {
        if (level.getRandom().nextDouble() <= 0.25 && level instanceof ServerLevel){
            SpawnPestFromBlock(bPos, (Level)level, null);
        }
    }

    public static final ArrayList<EntityType<?>> Pests = new ArrayList<>();
    public static void RegisterAsFleshBlockPest(String ID){
        RegisterAsFleshBlockPest(EntomoDataManager.getEntityFromString(ID));
    }
    public static void RegisterAsFleshBlockPest(EntityType<?> toRegister){
        if (Pests.contains(toRegister) || toRegister == null) return;
        Pests.add(toRegister);
    }
    public static EntityType<?> getRandomPestEntityType(RandomSource random){
        return Pests.get(random.nextInt(Pests.size()));
    }
    public static void SpawnPestFromBlock(@NotNull BlockPos bPos, @NotNull Level level, @Nullable LivingEntity target){
        Entity toReturn = getRandomPestEntityType(level.random).create(level);
        if (toReturn == null) return;
        toReturn.setPos(bPos.getCenter());
        if (toReturn instanceof Mob M){
            M.setTarget(target);
        }
        level.addFreshEntity(toReturn);
    }
}
