package mod.pilot.entomophobia.systems.PolyForged.Shapes;

import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RandomizedHollowSphereGenerator extends HollowSphereGenerator{
    public RandomizedHollowSphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness, trueHollow);
        BuildChance = buildChance;
    }

    public RandomizedHollowSphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness, trueHollow);
        BuildChance = buildChance;
    }

    public RandomizedHollowSphereGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness, trueHollow);
        BuildChance = buildChance;
    }

    public final double BuildChance;

    @Override
    public boolean Build() {
        if (!isOfState(WorldShapeManager.GeneratorStates.active)){
            return false;
        }
        ServerLevel server = getServer();
        //Code stolen from Harby-- thanks Harby
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for(int x = 0; x <= 2*radius; ++x) {
            for(int y = 0; y <= 2*radius; ++y) {
                for(int z = 0; z <= 2*radius; ++z) {
                    double distance = Mth.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius) + (z - radius) * (z - radius));
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - radius) - 1), (int)(center.y + (y - radius) - 1), (int)(center.z + (z - radius) - 1)));
                    BlockState bState = server.getBlockState(bPos);
                    if (CanThisBeReplaced(bState, bPos) && distance <= radius && (TrueHollow || distance > radius - thickness)){
                        if (BuildTracker > 1){
                            if (server.random.nextDouble() <= BuildChance){
                                succeeded = TrueHollow && distance <= radius - thickness ? ReplaceBlock(bPos, Blocks.AIR.defaultBlockState()) : ReplaceBlock(bPos);
                            }
                        }
                        else if (BuildTracker < 1){
                            if (getActiveTime() % (1 / BuildTracker) == 0 && server.random.nextDouble() <= BuildChance){
                                succeeded = TrueHollow && distance <= radius - thickness ? ReplaceBlock(bPos, Blocks.AIR.defaultBlockState()) : ReplaceBlock(bPos);
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
