package mod.pilot.entomophobia.systems.PolyForged.Shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChamberGenerator extends RandomizedHollowSphereGenerator{
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly, radius, thickness, buildChance, trueHollow);
    }
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, maxHardness, radius, thickness, buildChance, trueHollow);
    }
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist, radius, thickness, buildChance, trueHollow);
    }

    private final ArrayList<ArrayList<BlockPos>> GhostSpheres = new ArrayList<>();
    public ArrayList<ArrayList<BlockPos>> getGhostShapes() {
        return new ArrayList<>(GhostSpheres);
    }
    public void addToGhostShapes(ArrayList<BlockPos> toAdd){
        GhostSpheres.add(toAdd);
    }

    protected boolean isThisAGhostPosition(BlockPos bPos){
        boolean flag = false;
        ArrayList<ArrayList<BlockPos>> ghostSpheres = getGhostShapes();
        if (ghostSpheres == null){
            return false;
        }
        for (ArrayList<BlockPos> ghostSphere : ghostSpheres){
            if (ghostSphere == null){
                continue;
            }
            flag = ghostSphere.contains(bPos);
            if (flag) break;
        }
        return flag;
    }

    @Override
    public void Enable() {
        super.Enable();
    }
    @Override
    public boolean Build() {
        if (!isActive()) return false;
        //Code stolen from Harby-- thanks Harby
        ServerLevel server = getServer();
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for(int x = 0; x <= 2*radius; ++x) {
            for(int y = 0; y <= 2*radius; ++y) {
                for(int z = 0; z <= 2*radius; ++z) {
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - radius) - 1), (int)(center.y + (y - radius) - 1), (int)(center.z + (z - radius) - 1)));
                    double distance = center.distanceTo(bPos.getCenter());
                    BlockState bState = server.getBlockState(bPos);
                    if (canThisBeReplaced(bState, bPos) && distance <= radius && (TrueHollow || distance > radius - thickness)){
                        if (BuildTracker > 1){
                            if (server.random.nextDouble() <= BuildChance){
                                if (TrueHollow && distance <= radius - thickness){
                                    succeeded = ReplaceBlock(bPos, Blocks.AIR.defaultBlockState());
                                }
                                else if (!isThisAGhostPosition(bPos)){
                                    succeeded = ReplaceBlock(bPos);
                                }
                                else{
                                    succeeded = ReplaceBlock(bPos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                        else if (BuildTracker < 1){
                            if (getActiveTime() % (1 / BuildTracker) == 0 && server.random.nextDouble() <= BuildChance){
                                if (TrueHollow && distance <= radius - thickness){
                                    succeeded = ReplaceBlock(bPos, Blocks.AIR.defaultBlockState());
                                }
                                else if (!isThisAGhostPosition(bPos)){
                                    succeeded = ReplaceBlock(bPos);
                                }
                                else{
                                    succeeded = ReplaceBlock(bPos, Blocks.AIR.defaultBlockState());
                                }
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

    @Override
    public boolean canThisBeReplaced(BlockState state, BlockPos pos) {
        ServerLevel server = getServer();

        if (TrueHollow){
            switch (getPlacementDetail()){
                case 0 ->{
                    return state.canBeReplaced();
                }
                case 1 ->{
                    return (MaxHardness >= state.getDestroySpeed(server, pos) || state.canBeReplaced()) && state.getDestroySpeed(server, pos) != -1;
                }
                case 2 ->{
                    if (ReplaceWhitelist == null){
                        if (ReplaceBlacklist != null){
                            return (!ReplaceBlacklist.contains(state.getBlock().defaultBlockState()) || state.canBeReplaced()) && state.getDestroySpeed(server, pos) != -1;
                        }
                        return false;
                    }
                    else{
                        return (ReplaceWhitelist.contains(state.getBlock().defaultBlockState()) && (ReplaceBlacklist == null || !ReplaceBlacklist.contains(state.getBlock().defaultBlockState()))) || state.canBeReplaced() && state.getDestroySpeed(server, pos) != -1;
                    }
                }
                case 3 ->{
                    return  state.canBeReplaced() && state.getDestroySpeed(server, pos) != -1;
                }
                default -> {
                    return false;
                }
            }
        }
        return super.canThisBeReplaced(state, pos);
    }
    public ArrayList<BlockPos> GenerateInternalGhostSphere(){
        ArrayList<BlockPos> toReturn = new ArrayList<>();
        for(int x = 0; x <= 2*radius; ++x) {
            for(int y = 0; y <= 2*radius; ++y) {
                for(int z = 0; z <= 2*radius; ++z) {
                    double distance = Mth.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius) + (z - radius) * (z - radius));
                    Vec3 center = getPosition();
                    BlockPos bPos = new BlockPos(new Vec3i((int)(center.x + (x - radius) - 1), (int)(center.y + (y - radius) - 1), (int)(center.z + (z - radius) - 1)));
                    if (distance < radius - thickness){
                        toReturn.add(bPos);
                    }
                }
            }
        }
        return toReturn;
    }
}
