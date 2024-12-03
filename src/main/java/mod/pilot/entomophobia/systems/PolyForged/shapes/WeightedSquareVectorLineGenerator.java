package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.WeightedVectorLineGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeightedSquareVectorLineGenerator extends WeightedVectorLineGenerator {
    public WeightedSquareVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, boolean replaceableOnly,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, hydrophobic, weight);
    }

    public WeightedSquareVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, int maxHardness,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, hydrophobic, weight);
    }

    public WeightedSquareVectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                             @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end,  hydrophobic, weight);
    }

    @Override
    public boolean Build() {
        double distance = getStart().distanceTo(getEnd());
        ServerLevel server = getServer();
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for (int i = 0; i < distance; i++){
            for (int x = -(weight / 2); x < weight / 2; x++){
                for (int y = -(weight / 2); y < weight / 2; y++){
                    for (int z = -(weight / 2); z < weight / 2; z++){
                        Vec3 buildPos = i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i));
                        BlockPos bPos = new BlockPos((int)buildPos.x + x - 1 , (int)buildPos.y + y - 1, (int)buildPos.z + z - 1);
                        BlockState bState = server.getBlockState(bPos);
                        if (canThisBeReplaced(bState, bPos)){
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
                            if (!(Hydrophobic && !bState.getFluidState().isEmpty())){
                                BuildTracker--;
                            }
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
