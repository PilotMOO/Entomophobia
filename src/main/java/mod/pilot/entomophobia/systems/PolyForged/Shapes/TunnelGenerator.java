package mod.pilot.entomophobia.systems.PolyForged.Shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TunnelGenerator extends HollowWeightedCircleLineGenerator{
    public TunnelGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, boolean replaceableOnly, Vec3 start, Vec3 end, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, weight, thickness);
    }
    public TunnelGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, int maxHardness, Vec3 start, Vec3 end, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, weight, thickness);
    }
    public TunnelGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, Vec3 start, Vec3 end, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, weight, thickness);
    }

    private ArrayList<ArrayList<BlockPos>> toAdd = null;
    private ArrayList<ArrayList<BlockPos>> GhostLineSnapshot = null;
    @Override
    protected void GenerateGhostPositions() {
        ArrayList<ArrayList<BlockPos>> ghostPositions = new ArrayList<>();

        ServerLevel server = getServer();
        int GhostLineWeight = weight - (thickness * 2);
        double distance = getStart().distanceTo(getEnd());
        for (int i = -1; i <= distance + 1; i++){
            ArrayList<BlockPos> ghostSphere = new ArrayList<>();
            for (int x = 0; x <= GhostLineWeight; x++){
                for (int y = 0; y <= GhostLineWeight; y++){
                    for (int z = 0; z <= GhostLineWeight; z++){
                        Vec3 buildPos = i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i));
                        double distanceToCore = Mth.sqrt((x - (float) GhostLineWeight / 2) * (x - (float) GhostLineWeight / 2) + (y - (float) GhostLineWeight / 2) * (y - (float) GhostLineWeight / 2) + (z - (float) GhostLineWeight / 2) * (z - (float) GhostLineWeight / 2));
                        BlockPos bPos = new BlockPos((int)(buildPos.x + x - GhostLineWeight / 2), (int)(buildPos.y + y - GhostLineWeight / 2), (int)(buildPos.z + z - GhostLineWeight / 2));
                        if (canThisBeReplaced(server.getBlockState(bPos), bPos) && distanceToCore <= (double) GhostLineWeight / 2){
                            ghostSphere.add(bPos);
                        }
                    }
                }
            }
            ghostPositions.add(ghostSphere);
        }
        GhostLinePositions = new ArrayList<>(ghostPositions);
        GhostLineSnapshot = new ArrayList<>(ghostPositions);
    }
    public void addToGhostSpheres(ArrayList<BlockPos> toAdd){
        if (this.toAdd == null){
            this.toAdd = new ArrayList<>();
        }
        this.toAdd.add(toAdd);
    }
    public ArrayList<ArrayList<BlockPos>> getGhostLineSpheres(int count, boolean startFromEnd){
        ArrayList<ArrayList<BlockPos>> toReturn = new ArrayList<>();
        for (int i = startFromEnd ? GhostLineSnapshot.size() - 1 : 0; toReturn.size() < count && i < GhostLineSnapshot.size() && i >= 0; i += startFromEnd ? -1 : 1){
            toReturn.add(GhostLineSnapshot.get(i));
        }
        return toReturn;
    }

    @Override
    public boolean Build() {
        if (toAdd != null){
            GhostLinePositions.addAll(toAdd);
            toAdd = null;
        }
        return super.Build();
    }
}
