package mod.pilot.entomophobia.data.WorldShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SphereGenerator extends ShapeGenerator {
    public SphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly);
        this.radius = radius;
    }
    public SphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius) {
        super(server, buildSpeed, blockTypes, pos, maxHardness);
        this.radius = radius;
    }
    public SphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @org.jetbrains.annotations.Nullable List<BlockState> whitelist, @org.jetbrains.annotations.Nullable List<BlockState> blacklist, int radius) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist);
        this.radius = radius;
    }

    public final int radius;

    @Override
    public void Build() {
        if (!isOfState(EntomoWorldShapeManager.GeneratorStates.active)){
            return;
        }
        //Code stolen from Harby-- thanks Harby
        boolean succeeded = false;
        for(int x = 0; x <= 2*radius; ++x) {
            for(int y = 0; y <= 2*radius; ++y) {
                for(int z = 0; z <= 2*radius; ++z) {
                    double distance = Mth.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius) + (z - radius) * (z - radius));
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + x), (int)(center.y + y), (int)(center.z + z)));
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
            System.out.println("Job's done :3");
            Finish();
        }
    }
}