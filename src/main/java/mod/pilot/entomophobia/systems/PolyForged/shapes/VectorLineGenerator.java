package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.VectorShapeGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorLineGenerator extends VectorShapeGenerator {
    public VectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                               boolean replaceableOnly, Vec3 start, Vec3 end, boolean hydrophobic) {
        super(server, buildSpeed, blockTypes, start, hydrophobic, replaceableOnly, new ArrayList<>(Arrays.asList(start, end)));
    }
    public VectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                               int maxHardness, Vec3 start, Vec3 end, boolean hydrophobic) {
        super(server, buildSpeed, blockTypes, start, hydrophobic, maxHardness, new ArrayList<>(Arrays.asList(start, end)));
    }
    public VectorLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                               @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                               Vec3 start, Vec3 end, boolean hydrophobic) {
        super(server, buildSpeed, blockTypes, start, hydrophobic, whitelist, blacklist, new ArrayList<>(Arrays.asList(start, end)));
    }

    protected final static int start = 0;
    public final Vec3 getStart(){
        return Vectors.get(start);
    }
    protected final static int end = 1;
    public final Vec3 getEnd(){
        return Vectors.get(end);
    }
    public final Vec3 directionFromStartToFinish(){
        return directionFromAToB(getStart(), getEnd());
    }

    @Override
    public boolean Build() {
        ServerLevel server = getServer();
        double distance = getStart().distanceTo(getEnd());
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for (int i = 0; i < distance; i++){
            Vec3 buildPos = i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i));
            BlockPos bPos = new BlockPos((int)buildPos.x - 1, (int)buildPos.y - 1, (int)buildPos.z - 1);
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
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}
