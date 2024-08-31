package mod.pilot.entomophobia.data.PolyForged.Shapes;

import mod.pilot.entomophobia.data.PolyForged.WeightedVectorLineGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeightedCircleVectorLineGenerator extends WeightedVectorLineGenerator {
    public WeightedCircleVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight);
    }
    public WeightedCircleVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, weight);
    }
    public WeightedCircleVectorLineGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight);
    }

    @Override
    public boolean Build() {
        double distance = getStart().distanceTo(getEnd());
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for (int i = 0; i < distance; i++){
            for (int x = 0; x <= weight; x++){
                for (int y = 0; y <= weight; y++){
                    for (int z = 0; z <= weight; z++){
                        Vec3 buildPos = i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i));
                        double distanceToCore = Mth.sqrt((x - (float) weight / 2) * (x - (float) weight / 2) + (y - (float) weight / 2) * (y - (float) weight / 2) + (z - (float) weight / 2) * (z - (float) weight / 2));
                        BlockPos bPos = new BlockPos((int) (buildPos.x + x - weight / 2), (int) (buildPos.y + y - weight / 2), (int) (buildPos.z + z - weight / 2));
                        BlockState bState = server.getBlockState(bPos);
                        if (CanThisBeReplaced(bState, bPos) && distanceToCore <= (double) weight / 2){
                            if (BuildTracker >= 1){
                                succeeded = ReplaceBlock(bPos);
                            }
                            else{
                                if (getActiveTime() % (int)(1 / BuildTracker) == 0){
                                    succeeded = ReplaceBlock(bPos);
                                }
                                else{
                                    succeeded = true;
                                }
                            }
                        }
                        if (succeeded){
                            BuildTracker--;
                            if (BuildTracker <= 0){
                                break;
                            }
                        }
                    }
                    if (succeeded && BuildTracker <= 0) {break;}
                }
                if (succeeded && BuildTracker <= 0) {break;}
            }
            if (succeeded && BuildTracker <= 0) {break;}
        }
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}
