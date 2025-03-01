package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.ShapeGenerator;
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

public class SphereGenerator extends ShapeGenerator {
    public SphereGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           boolean replaceableOnly, int radius) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly);
        this.radius = radius;
    }
    public SphereGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           int maxHardness, int radius) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness);
        this.radius = radius;
    }
    public SphereGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                           @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int radius) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist);
        this.radius = radius;
    }

    public final int radius;

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
        for(int x = 0; x <= radius * 2; x++) {
            for(int y = 0; y <= radius * 2; y++) {
                for(int z = 0; z <= radius * 2; z++) {
                    double distance = Mth.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius) + (z - radius) * (z - radius));
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - radius) - 1), (int)(center.y + (y - radius) - 1), (int)(center.z + (z - radius) - 1)));
                    BlockState bState = server.getBlockState(bPos);
                    if (canThisBeReplaced(bState, bPos) && distance <= radius){
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