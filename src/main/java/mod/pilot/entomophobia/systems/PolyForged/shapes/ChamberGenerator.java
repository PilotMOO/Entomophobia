package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.GhostSphere;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChamberGenerator extends RandomizedHollowSphereGenerator{
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean hydrophobic,
                            boolean replaceableOnly, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly, radius, thickness, buildChance, trueHollow);
    }
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean hydrophobic,
                            int maxHardness, int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness, radius, thickness, buildChance, trueHollow);
    }
    public ChamberGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean hydrophobic,
                            @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                            int radius, int thickness, double buildChance, boolean trueHollow) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist, radius, thickness, buildChance, trueHollow);
    }

    private final ArrayList<GhostSphere> GhostSpheres = new ArrayList<>();
    public ArrayList<GhostSphere> getGhostSpheres() {
        return new ArrayList<>(GhostSpheres);
    }
    public void addToGhostShapes(GhostSphere toAdd){
        GhostSpheres.add(toAdd);
    }
    protected boolean isThisAGhostPosition(BlockPos bPos){
        boolean flag = false;
        for (GhostSphere ghost : getGhostSpheres()){
            flag = ghost.isGhost(bPos);
            if (flag) break;
        }
        return flag;
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
                    BlockPos bPos = new BlockPos((int)(center.x + (x - radius) - 1), (int)(center.y + (y - radius) - 1), (int)(center.z + (z - radius) - 1));
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
    public GhostSphere GenerateInternalGhostSphere(){
        return new GhostSphere(getPosition(), radius - thickness);
    }
}
