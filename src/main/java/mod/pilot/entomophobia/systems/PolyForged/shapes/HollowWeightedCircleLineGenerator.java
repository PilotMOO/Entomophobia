package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import mod.pilot.entomophobia.systems.PolyForged.utility.GhostSphere;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HollowWeightedCircleLineGenerator extends WeightedCircleVectorLineGenerator {
    public HollowWeightedCircleLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, boolean replaceableOnly,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, hydrophobic, weight);
        this.thickness = thickness;
    }

    public HollowWeightedCircleLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, int maxHardness,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, hydrophobic, weight);
        this.thickness = thickness;
    }

    public HollowWeightedCircleLineGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                                             @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                             Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, hydrophobic, weight);
        this.thickness = thickness;
    }

    public final int thickness;
    protected int DistanceTracker = 0;

    protected ArrayList<GhostSphere> GhostLinePositions;
    protected void GenerateGhostPositions(){
        ArrayList<GhostSphere> ghostPositions = new ArrayList<>();

        int GhostLineWeight = (weight - (thickness * 2) - 1) / 2;
        double distance = getStart().distanceTo(getEnd());
        for (int i = 0; i < distance; i++){
            ghostPositions.add(new GhostSphere(i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i)), GhostLineWeight));
        }
        GhostLinePositions = ghostPositions;
    }

    public ArrayList<GhostSphere> getGhostShapes() {
        return new ArrayList<>(GhostLinePositions);
    }

    protected boolean isThisAGhostPosition(BlockPos bPos){
        boolean flag = false;
        for (GhostSphere ghosts : GhostLinePositions){
            flag = ghosts.isGhost(bPos);
            if (flag) break;
        }
        return flag;
    }
    @Override
    public boolean canThisBeReplaced(BlockState state, BlockPos pos) {
        ServerLevel server = getServer();

        if (isThisAGhostPosition(pos)){
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
                        return ReplaceWhitelist.contains(state.getBlock().defaultBlockState()) && (ReplaceBlacklist == null || !ReplaceBlacklist.contains(state.getBlock().defaultBlockState()));
                    }
                }
                case 3 ->{
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
        return super.canThisBeReplaced(state, pos);
    }
    @Override
    public void Enable() {
        GenerateGhostPositions();
        super.Enable();
    }
    @Override
    public void Finish() {
        GhostLinePositions.clear();
        super.Finish();
    }

    @Override
    public boolean Build() {
        ServerLevel server = getServer();
        double distance = getStart().distanceTo(getEnd());
        ActiveTimeTick();
        double BuildTracker = getBuildSpeed();
        boolean succeeded = false;
        for (int i = DistanceTracker; i < distance; i++){
            for (int x = 0; x <= weight; x++){
                for (int y = 0; y <= weight; y++){
                    for (int z = 0; z <= weight; z++){
                        Vec3 buildPos = i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i));
                        double distanceToCore = Mth.sqrt((x - (float) weight / 2) * (x - (float) weight / 2) + (y - (float) weight / 2) * (y - (float) weight / 2) + (z - (float) weight / 2) * (z - (float) weight / 2));
                        BlockPos bPos = new BlockPos((int) (buildPos.x + x - weight / 2), (int) (buildPos.y + y - weight / 2), (int) (buildPos.z + z - weight / 2));
                        BlockState bState = server.getBlockState(bPos);
                        if (bPos.getY() <= server.getMinBuildHeight()){
                            Disable();
                            break;
                        }
                        if (canThisBeReplaced(bState, bPos) && distanceToCore <= (double) weight / 2){
                            if (BuildTracker >= 1){
                                succeeded = isThisAGhostPosition(bPos) ? ReplaceBlock(bPos, Blocks.AIR.defaultBlockState()) : ReplaceBlock(bPos);
                            }
                            else{
                                if (getActiveTime() % (int)(1 / BuildTracker) == 0){
                                    succeeded = isThisAGhostPosition(bPos) ? ReplaceBlock(bPos, Blocks.AIR.defaultBlockState()) : ReplaceBlock(bPos);
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
            if (succeeded) {
                if (BuildTracker <= 0){
                    break;
                }
            }
            else{
                DistanceTracker++;
            }
        }
        if (!succeeded){
            Finish();
        }
        return succeeded;
    }
}
