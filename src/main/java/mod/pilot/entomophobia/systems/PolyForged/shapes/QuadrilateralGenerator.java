package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.ShapeGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import mod.pilot.entomophobia.systems.PolyForged.utility.WorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuadrilateralGenerator extends ShapeGenerator {
    public QuadrilateralGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                  boolean replaceableOnly, int X, int Y, int Z) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly);
        Xsize = X;
        Ysize = Y;
        Zsize = Z;
    }
    public QuadrilateralGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                  int maxHardness, int X, int Y, int Z) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness);
        Xsize = X;
        Ysize = Y;
        Zsize = Z;
    }
    public QuadrilateralGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                  @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int X, int Y, int Z) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist);
        Xsize = X;
        Ysize = Y;
        Zsize = Z;
    }

    public final int Xsize;
    public final int Ysize;
    public final int Zsize;

    @Override
    public boolean Build() {
        if (!isOfState(WorldShapeManager.GeneratorStates.active)){
            return false;
        }
        ServerLevel server = getServer();
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        //Code stolen from Harby-- thanks Harby
        boolean succeeded = false;
        for(int x = 0; x < Xsize; ++x) {
            for(int y = 0; y < Ysize; ++y) {
                for(int z = 0; z < Zsize; ++z) {
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - (Xsize / 2)) - 1), (int)(center.y + (y - (Ysize / 2)) - 1), (int)(center.z + (z - (Zsize / 2))) - 1));
                    BlockState bState = server.getBlockState(bPos);
                    if (canThisBeReplaced(bState, bPos)){
                        if (BuildTracker > 1){
                            succeeded = ReplaceBlock(bPos);
                        }
                        else{
                            if (getActiveTime() % (1 / BuildTracker) == 0){
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
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}