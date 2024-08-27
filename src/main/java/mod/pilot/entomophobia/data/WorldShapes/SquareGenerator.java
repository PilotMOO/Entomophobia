package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SquareGenerator extends ShapeGenerator{
    protected SquareGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int size, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()) {
            case 0 -> Y = Z = size;
            case 1 -> X = Z = size;
            case 2 -> X = Y = size;
            default -> X = Y = Z = 0;
        }
        Xsize = X; Ysize = Y; Zsize = Z;
        ExcludedAxis = excluded;
    }

    protected SquareGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int size, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, maxHardness);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()){
            case 0:{
                Y = Z = size;
            }
            case 1:{
                X = Z = size;
            }
            case 2:{
                X = Y = size;
            }
            default:{
                X = Y = Z = 0;
            }
        }
        Xsize = X; Ysize = Y; Zsize = Z;
        ExcludedAxis = excluded;
    }

    protected SquareGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int size, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()){
            case 0:{
                Y = Z = size;
            }
            case 1:{
                X = Z = size;
            }
            case 2:{
                X = Y = size;
            }
            default:{
                X = Y = Z = 0;
            }
        }
        Xsize = X; Ysize = Y; Zsize = Z;
        ExcludedAxis = excluded;
    }


    public final int Xsize;
    public final int Ysize;
    public final int Zsize;
    public final WorldShapeManager.Axis ExcludedAxis;

    @Override
    public boolean Build() {
        if (!isOfState(WorldShapeManager.GeneratorStates.active)){
            return false;
        }
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for(int x = 0; x < Xsize; ++x) {
            for(int y = 0; y < Ysize; ++y) {
                for(int z = 0; z < Zsize; ++z) {
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - (Xsize / 2)) - 1), (int)(center.y + (y - (Ysize / 2)) - 1), (int)(center.z + (z - (Zsize / 2))) - 1));
                    BlockState bState = server.getBlockState(bPos);
                    if (CanThisBeReplaced(bState, bPos)){
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
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}
