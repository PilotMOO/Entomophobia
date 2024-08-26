package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CubeGenerator extends ShapeGenerator {
    public CubeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int size) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly);
        this.size = size;
    }
    public CubeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int size) {
        super(server, buildSpeed, blockTypes, pos, maxHardness);
        this.size = size;
    }
    public CubeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int size) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist);
        this.size = size;
    }

    public final int size;

    @Override
    public boolean Build() {
        if (!isOfState(EntomoWorldShapeManager.GeneratorStates.active)){
            return false;
        }
        //Code stolen from Harby-- thanks Harby
        boolean succeeded = false;
        for(int x = 0; x < size; ++x) {
            for(int y = 0; y < size; ++y) {
                for(int z = 0; z < size; ++z) {
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - (size / 2)) - 1), (int)(center.y + (y - (size / 2)) - 1), (int)(center.z + (z - (size / 2))) - 1));
                    BlockState bState = server.getBlockState(bPos);
                    if (CanThisBeReplaced(bState, bPos)){
                        succeeded = ReplaceBlock(bPos);
                    }
                    if (succeeded) {break;}
                }
                if (succeeded) {break;}
            }
            if (succeeded) {break;}
        }
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}