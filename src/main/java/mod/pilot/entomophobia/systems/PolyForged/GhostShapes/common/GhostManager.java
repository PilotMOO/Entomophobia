package mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public class GhostManager {
    public static ArrayList<BlockPos> TranslateToPositions(ArrayList<PackagedBlockPos> ghosts){
        ArrayList<BlockPos> toReturn = new ArrayList<>();
        for (PackagedBlockPos pbPos : ghosts){
            toReturn.add(PackagedBlockPos.Unpack(pbPos));
        }
        return toReturn;
    }
    public static ArrayList<PackagedBlockPos> TranslateToGhosts(ArrayList<BlockPos> positions){
        ArrayList<PackagedBlockPos> toReturn = new ArrayList<>();
        for (BlockPos bPos : positions){
            toReturn.add(PackagedBlockPos.Pack(bPos));
        }
        return toReturn;
    }
}
