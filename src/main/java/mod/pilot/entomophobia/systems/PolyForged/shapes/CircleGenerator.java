package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.FlatShapeGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import mod.pilot.entomophobia.systems.PolyForged.utility.WorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CircleGenerator extends FlatShapeGenerator {
    public CircleGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           boolean replaceableOnly, int radius, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, radius, radius, radius, excluded);
    }

    public CircleGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           int maxHardness, int radius, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, radius, radius, radius, excluded);
    }

    public CircleGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                           int radius, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, radius, radius, radius, excluded);
    }

    @Override
    public boolean Build() {
        if (!isOfState(WorldShapeManager.GeneratorStates.active)) {
            return false;
        }
        ServerLevel server = getServer();
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for (int x = 0; x <= (ExcludedAxis != WorldShapeManager.Axis.X ? Xsize * 2 : 0); x++) {
            for (int y = 0; y <= (ExcludedAxis != WorldShapeManager.Axis.Y ? Ysize * 2 : 0); y++) {
                for (int z = 0; z <= (ExcludedAxis != WorldShapeManager.Axis.Z ? Zsize * 2 : 0); z++) {
                    double distance = Mth.sqrt((x - Xsize) * (x - Xsize) + (y - Ysize) * (y - Ysize) + (z - Zsize) * (z - Zsize));
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int) (center.x + (x - Xsize) - 1), (int) (center.y + (y - Ysize)), (int) (center.z + (z - Zsize) - 1)));
                    BlockState bState = server.getBlockState(bPos);
                    if (canThisBeReplaced(bState, bPos) && distance <= (double) (Xsize + Ysize + Zsize) / 2) {
                        if (BuildTracker >= 1) {
                            succeeded = ReplaceBlock(bPos);
                        } else {
                            if (getActiveTime() % (int) (1 / BuildTracker) == 0) {
                                succeeded = ReplaceBlock(bPos);
                            } else {
                                succeeded = true;
                            }
                        }
                        if (succeeded && BuildTracker <= 0) {
                            break;
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
        if (!succeeded) {
            Finish();
        }
        return succeeded;
    }
}
