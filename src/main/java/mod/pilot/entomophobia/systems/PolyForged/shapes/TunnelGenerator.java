package mod.pilot.entomophobia.systems.PolyForged.shapes;

import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import mod.pilot.entomophobia.systems.PolyForged.utility.GhostSphere;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TunnelGenerator extends HollowWeightedCircleLineGenerator{
    public TunnelGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, boolean replaceableOnly,
                           Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, replaceableOnly, start, end, hydrophobic, weight, thickness);
    }
    public TunnelGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, int maxHardness,
                           Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, maxHardness, start, end, hydrophobic, weight, thickness);
    }
    public TunnelGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes,
                           @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                           Vec3 start, Vec3 end, boolean hydrophobic, int weight, int thickness) {
        super(server, buildSpeed, blockTypes, whitelist, blacklist, start, end, hydrophobic, weight, thickness);
    }

    private ArrayList<GhostSphere> toAdd;
    private ArrayList<GhostSphere> GhostLineSnapshot;
    @Override
    protected void GenerateGhostPositions() {
        ArrayList<GhostSphere> ghostPositions = new ArrayList<>();

        int GhostLineWeight = (weight - (thickness * 2)) / 2;
        double distance = getStart().distanceTo(getEnd());
        for (int i = -1; i <= distance + 1; i++){
            ghostPositions.add(new GhostSphere(i == 0 ? getStart() : getStart().add(directionFromStartToFinish().scale(i)), GhostLineWeight));
        }
        GhostLinePositions = new ArrayList<>(ghostPositions);
        GhostLineSnapshot = new ArrayList<>(ghostPositions);
    }
    public void addToGhostSpheres(GhostSphere toAdd){
        if (this.toAdd == null){
            this.toAdd = new ArrayList<>();
        }
        this.toAdd.add(toAdd);
    }
    public ArrayList<GhostSphere> getGhostLineSpheres(int count, boolean startFromEnd){
        ArrayList<GhostSphere> toReturn = new ArrayList<>();
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
